/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.client.model;

import com.sun.istack.internal.Nullable;
import net.fabricmc.fabric.api.client.model.CustomModelLoader;
import net.fabricmc.fabric.api.client.model.CustomModelLoaderException;
import net.fabricmc.fabric.api.client.model.ModelLoaderRegistry;
import net.fabricmc.fabric.api.client.model.ModelRequester;
import net.fabricmc.loader.launch.common.FabricLauncherBase;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModelLoaderRegistryImpl implements ModelLoaderRegistry {
	public static class LoaderInstance {
		private final Logger logger;
		private final List<CustomModelLoader> modelLoaders;
		private final List<ModelRequester> modelRequesters;

		private LoaderInstance(ModelLoaderRegistryImpl i, ResourceManager manager) {
			logger = ModelLoaderRegistryImpl.LOGGER;
			modelLoaders = i.loaderSuppliers.stream().map((s) -> s.apply(manager)).collect(Collectors.toList());
			modelRequesters = i.requesterSuppliers.stream().map(Supplier::get).collect(Collectors.toList());
		}

		public void onModelPopulation(Consumer<ModelIdentifier> addModel) {
			modelRequesters.forEach((r) -> r.getRequests().forEach(addModel));
		}

		@Nullable
		public UnbakedModel loadModel(Identifier identifier) {
			List<CustomModelLoader> loaders = modelLoaders.stream()
				.filter((l) -> l.accepts(identifier))
				.collect(Collectors.toList());

			switch (loaders.size()) {
				case 0: {
					return null;
				}
				case 1: {
					try {
						return loaders.get(0).load(identifier);
					} catch (CustomModelLoaderException e) {
						logger.error(e);
						return null;
					}
				}
				default: {
					StringBuilder builder = new StringBuilder("Conflict - multiple loaders claimed the same unbaked model:");
					for (CustomModelLoader loader : loaders) {
						builder.append("\n\t - ").append(loader.getClass().getName());
					}
					logger.error(builder.toString());
					return null;
				}
			}
		}

		public void finish(ModelLoader loader) {
			// TODO: Replace call with a Loader API ASAP!
			Map<Identifier, BakedModel> map = loader.getBakedModelMap();
			if (FabricLauncherBase.getLauncher().isDevelopment()) {
				// In development, catch people trying to access MIDs outside of
				// their area of interest.

				modelRequesters.forEach((r) -> {
					Collection<ModelIdentifier> c = r.getRequests();
					r.receive((m) -> {
						if (!c.contains(m)) {
							LOGGER.error("Tried to access ModelIdentifier " + m + " outside of ModelRequester scope - not allowed!");
							return null;
						} else {
							return map.get(m);
						}
					});
				});
			} else {
				// In production, don't waste valuable performance on that.
				modelRequesters.forEach((r) -> r.receive(loader.getBakedModelMap()::get));
			}
		}
	}

	private static final Logger LOGGER = LogManager.getLogger();
	public static final ModelLoaderRegistryImpl INSTANCE = new ModelLoaderRegistryImpl();

	private final List<Function<ResourceManager, CustomModelLoader>> loaderSuppliers = new ArrayList<>();
	private final List<Supplier<ModelRequester>> requesterSuppliers = new ArrayList<>();

	@Override
	public void registerRequester(Supplier<ModelRequester> requesterSupplier) {
		requesterSuppliers.add(requesterSupplier);
	}

	@Override
	public void registerLoader(Function<ResourceManager, CustomModelLoader> loaderSupplier) {
		loaderSuppliers.add(loaderSupplier);
	}

	public static LoaderInstance begin(ResourceManager manager) {
		return new LoaderInstance(INSTANCE, manager);
	}
}
