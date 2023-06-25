/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.fabric.impl.client.model.loading;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelProviderContext;

public class ModelLoaderInstance implements ModelProviderContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelLoaderInstance.class);

	private static final List<ModelLoadingPlugin> PLUGINS = new ArrayList<>();

	public static void registerPlugin(ModelLoadingPlugin plugin) {
		PLUGINS.add(plugin);
	}

	private ModelLoader loader;
	private final ModelLoaderPluginContextImpl context;

	public ModelLoaderInstance(ModelLoader loader, ResourceManager manager) {
		this.loader = loader;
		this.context = new ModelLoaderPluginContextImpl(manager, this);

		for (ModelLoadingPlugin plugin : PLUGINS) {
			try {
				plugin.onInitializeModelLoader(context);
			} catch (Exception exception) {
				LOGGER.error("Failed to run model loading plugin {}", plugin.getClass().getName(), exception);
			}
		}
	}

	@Override
	public UnbakedModel loadModel(Identifier id) {
		if (loader == null) {
			throw new RuntimeException("Called loadModel too late!");
		}

		return ((ModelLoaderHooks) loader).fabric_loadModel(id);
	}

	public void onModelPopulation(Consumer<Identifier> addModel) {
		for (Identifier id : context.extraModels) {
			addModel.accept(id);
		}
	}

	@Nullable
	public UnbakedModel loadModelFromResource(Identifier resourceId) {
		return context.resourceProviders().invoker().loadModelResource(resourceId, this);
	}

	@Nullable
	public UnbakedModel loadModelFromVariant(Identifier variantId) {
		if (!(variantId instanceof ModelIdentifier modelId)) {
			return loadModelFromResource(variantId);
		} else {
			UnbakedModel model = context.variantProviders().invoker().loadModelVariant(modelId, this);

			if (model != null) {
				return model;
			}

			// Replicating the special-case from ModelLoader as loadModelFromJson is insufficiently patchable
			if (Objects.equals(modelId.getVariant(), "inventory")) {
				Identifier resourceId = new Identifier(modelId.getNamespace(), "item/" + modelId.getPath());
				model = loadModelFromResource(resourceId);

				if (model != null) {
					return model;
				}
			}

			return null;
		}
	}

	public void finish() {
		loader = null;
	}
}
