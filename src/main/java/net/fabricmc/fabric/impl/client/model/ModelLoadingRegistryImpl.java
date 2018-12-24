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

import com.google.common.collect.Lists;
import com.sun.istack.internal.Nullable;
import net.fabricmc.fabric.api.client.model.ModelAppender;
import net.fabricmc.fabric.api.client.model.ModelProvider;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
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

public class ModelLoadingRegistryImpl implements ModelLoadingRegistry {
	private static final boolean DEBUG_MODEL_LOADING = FabricLauncherBase.getLauncher().isDevelopment()
		|| Boolean.valueOf(System.getProperty("fabric.debugModelLoading", "false"));

	public static class LoaderInstance implements ModelProvider.Context {
		private final Logger logger;
		private final ResourceManager manager;
		private final List<ModelProvider> modelLoaders;
		private final List<ModelAppender> modelAppenders;
		private ModelLoader loader;

		private LoaderInstance(ModelLoadingRegistryImpl i, ModelLoader loader, ResourceManager manager) {
			this.logger = ModelLoadingRegistryImpl.LOGGER;
			this.loader = loader;
			this.manager = manager;
			this.modelLoaders = i.loaderSuppliers.stream().map((s) -> s.apply(manager)).collect(Collectors.toList());
			this.modelAppenders = i.appenders;
		}

		@Override
		public UnbakedModel loadModel(Identifier id) {
			if (loader == null) {
				throw new RuntimeException("Called loadModel too late!");
			}

			return loader.getOrLoadModel(id);
		}

		public void onModelPopulation(Consumer<ModelIdentifier> addModel) {
			for (ModelAppender appender : modelAppenders) {
				appender.append(manager, addModel);
			}
		}

		@Nullable
		public UnbakedModel loadCustomModel(Identifier identifier) {
			if (!DEBUG_MODEL_LOADING) {
				for (ModelProvider provider : modelLoaders) {
					try {
						UnbakedModel model = provider.load(identifier, this);
						if (model != null) {
							return model;
						}
					} catch (ModelProviderException e) {
						logger.error(e);
						return null;
					}
				}

				return null;
			}

			UnbakedModel modelLoaded = null;
			ModelProvider providerUsed = null;
			List<ModelProvider> providersApplied = null;

			for (ModelProvider provider : modelLoaders) {
				try {
					UnbakedModel model = provider.load(identifier, this);
					if (model != null) {
						if (providersApplied != null) {
							providersApplied.add(provider);
						} else if (providerUsed != null) {
							providersApplied = Lists.newArrayList(providerUsed, provider);
						} else {
							modelLoaded = model;
							providerUsed = provider;
						}
					}
				} catch (ModelProviderException e) {
					logger.error(e);
					return null;
				}
			}

			if (providersApplied != null) {
				StringBuilder builder = new StringBuilder("Conflict - multiple ModelProviders claimed the same unbaked model:");
				for (ModelProvider loader : providersApplied) {
					builder.append("\n\t - ").append(loader.getClass().getName());
				}
				logger.error(builder.toString());
				return null;
			} else {
				return modelLoaded;
			}
		}

		public void finish() {
			loader = null;
		}
	}

	private static final Logger LOGGER = LogManager.getLogger();
	public static final ModelLoadingRegistryImpl INSTANCE = new ModelLoadingRegistryImpl();

	private final List<Function<ResourceManager, ModelProvider>> loaderSuppliers = new ArrayList<>();
	private final List<ModelAppender> appenders = new ArrayList<>();

	@Override
	public void registerAppender(ModelAppender appender) {
		appenders.add(appender);
	}

	@Override
	public void registerProvider(Function<ResourceManager, ModelProvider> loaderSupplier) {
		loaderSuppliers.add(loaderSupplier);
	}

	public static LoaderInstance begin(ModelLoader loader, ResourceManager manager) {
		return new LoaderInstance(INSTANCE, loader, manager);
	}
}
