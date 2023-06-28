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
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;

public class ModelLoaderInstance implements ModelResolver.Context {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelLoaderInstance.class);

	private static final List<ModelLoadingPlugin> PLUGINS = new ArrayList<>();

	public static void registerPlugin(ModelLoadingPlugin plugin) {
		PLUGINS.add(plugin);
	}

	private final ModelLoader loader;
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
		return ((ModelLoaderHooks) loader).fabric_loadModel(id);
	}

	public void onModelPopulation(Consumer<Identifier> addModel) {
		for (Identifier id : context.extraModels) {
			addModel.accept(id);
		}
	}

	@Nullable
	public UnbakedModel resolveModelResource(Identifier resourceId) {
		return context.resolveModelResource().invoker().resolveModelResource(resourceId, this);
	}

	@Nullable
	public UnbakedModel resolveModelVariant(Identifier variantId) {
		if (!(variantId instanceof ModelIdentifier modelId)) {
			return resolveModelResource(variantId);
		} else {
			UnbakedModel model = context.resolveModelVariant().invoker().resolveModelVariant(modelId, this);

			if (model != null) {
				return model;
			}

			// Replicating the special-case from ModelLoader as loadModelFromJson is insufficiently patchable
			if (Objects.equals(modelId.getVariant(), "inventory")) {
				Identifier resourceId = new Identifier(modelId.getNamespace(), "item/" + modelId.getPath());
				model = resolveModelResource(resourceId);

				if (model != null) {
					return model;
				}
			}

			return null;
		}
	}

	private record UnbakedModifierContext(Identifier identifier, ModelLoader loader) implements ModelModifier.Unbaked.Context { }

	public UnbakedModel modifyModelOnLoad(Identifier identifier, UnbakedModel model) {
		ModelModifier.Unbaked.Context observerContext = new UnbakedModifierContext(identifier, loader);
		return context.modifyModelOnLoad().invoker().modifyUnbakedModel(model, observerContext);
	}

	public UnbakedModel modifyModelBeforeBake(Identifier identifier, UnbakedModel model) {
		ModelModifier.Unbaked.Context observerContext = new UnbakedModifierContext(identifier, loader);
		return context.modifyModelBeforeBake().invoker().modifyUnbakedModel(model, observerContext);
	}

	private record BakedModifierContext(Identifier identifier, UnbakedModel sourceModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker, ModelLoader loader) implements ModelModifier.Baked.Context { }

	public BakedModel modifyModelAfterBake(Identifier identifier, UnbakedModel model, BakedModel bakedModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
		ModelModifier.Baked.Context observerContext = new BakedModifierContext(identifier, model, textureGetter, settings, baker, loader);
		return context.modifyModelAfterBake().invoker().modifyBakedModel(bakedModel, observerContext);
	}
}
