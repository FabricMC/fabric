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

public class ModelLoadingEventDispatcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelLoadingEventDispatcher.class);

	private static final List<ModelLoadingPlugin> PLUGINS = new ArrayList<>();

	public static void registerPlugin(ModelLoadingPlugin plugin) {
		PLUGINS.add(plugin);
	}

	private final ModelLoader loader;
	private final ResolverContext resolverContext;
	private final ModelLoaderPluginContextImpl context;

	public ModelLoadingEventDispatcher(ModelLoader loader, ResourceManager manager) {
		this.loader = loader;
		this.resolverContext = new ResolverContext(this.loader);
		this.context = new ModelLoaderPluginContextImpl(manager, resolverContext);

		for (ModelLoadingPlugin plugin : PLUGINS) {
			try {
				plugin.onInitializeModelLoader(context);
			} catch (Exception exception) {
				LOGGER.error("Failed to initialize model loading plugin {}", plugin.getClass().getName(), exception);
			}
		}
	}

	public void addExtraModels(Consumer<Identifier> extraModelConsumer) {
		for (Identifier id : context.extraModels) {
			extraModelConsumer.accept(id);
		}
	}

	@Nullable
	public UnbakedModel resolveModel(Identifier id) {
		if (id instanceof ModelIdentifier modelId) {
			return resolveModelVariant(modelId);
		} else {
			return resolveModelResource(id);
		}
	}

	@Nullable
	private UnbakedModel resolveModelVariant(ModelIdentifier variantId) {
		UnbakedModel model = context.resolveModelVariant().invoker().resolveModelVariant(variantId, resolverContext);

		if (model != null) {
			return model;
		}

		// Replicating the special-case from ModelLoader as loadModelFromJson is insufficiently patchable
		if (Objects.equals(variantId.getVariant(), "inventory")) {
			Identifier resourceId = variantId.withPrefixedPath("item/");
			model = resolveModelResource(resourceId);

			if (model != null) {
				return model;
			}
		}

		return null;
	}

	@Nullable
	private UnbakedModel resolveModelResource(Identifier resourceId) {
		return context.resolveModelResource().invoker().resolveModelResource(resourceId, resolverContext);
	}

	public UnbakedModel modifyModelOnLoad(Identifier identifier, UnbakedModel model) {
		ModelModifier.Unbaked.Context observerContext = new UnbakedModifierContext(identifier, loader);
		return context.modifyModelOnLoad().invoker().modifyUnbakedModel(model, observerContext);
	}

	public UnbakedModel modifyModelBeforeBake(Identifier identifier, UnbakedModel model) {
		ModelModifier.Unbaked.Context observerContext = new UnbakedModifierContext(identifier, loader);
		return context.modifyModelBeforeBake().invoker().modifyUnbakedModel(model, observerContext);
	}

	public BakedModel modifyModelAfterBake(Identifier identifier, UnbakedModel model, BakedModel bakedModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
		ModelModifier.Baked.Context observerContext = new BakedModifierContext(identifier, model, textureGetter, settings, baker, loader);
		return context.modifyModelAfterBake().invoker().modifyBakedModel(bakedModel, observerContext);
	}

	private record ResolverContext(ModelLoader loader) implements ModelResolver.Context {
		@Override
		public UnbakedModel loadModel(Identifier id) {
			return ((ModelLoaderHooks) loader).fabric_tryLoadModel(id);
		}
	}

	private record UnbakedModifierContext(Identifier identifier, ModelLoader loader) implements ModelModifier.Unbaked.Context { }

	private record BakedModifierContext(Identifier identifier, UnbakedModel sourceModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker, ModelLoader loader) implements ModelModifier.Baked.Context { }
}
