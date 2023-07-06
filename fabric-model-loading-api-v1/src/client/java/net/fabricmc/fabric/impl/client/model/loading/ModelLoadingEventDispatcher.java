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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
	private final ModelLoaderPluginContextImpl pluginContext;

	private final ObjectArrayList<OnLoadModifierContext> onLoadModifierContext = new ObjectArrayList<>();
	private final ObjectArrayList<BeforeBakeModifierContext> beforeBakeModifierContext = new ObjectArrayList<>();
	private final ObjectArrayList<AfterBakeModifierContext> afterBakeModifierContext = new ObjectArrayList<>();

	public ModelLoadingEventDispatcher(ModelLoader loader, ResourceManager manager) {
		this.loader = loader;
		this.resolverContext = new ResolverContext();
		this.pluginContext = new ModelLoaderPluginContextImpl(manager, resolverContext);

		for (ModelLoadingPlugin plugin : PLUGINS) {
			try {
				plugin.onInitializeModelLoader(pluginContext);
			} catch (Exception exception) {
				LOGGER.error("Failed to initialize model loading plugin {}", plugin.getClass().getName(), exception);
			}
		}
	}

	public void addExtraModels(Consumer<Identifier> extraModelConsumer) {
		for (Identifier id : pluginContext.extraModels) {
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
		UnbakedModel model = pluginContext.resolveModelVariant().invoker().resolveModelVariant(variantId, resolverContext);

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
		return pluginContext.resolveModelResource().invoker().resolveModelResource(resourceId, resolverContext);
	}

	public UnbakedModel modifyModelOnLoad(Identifier id, UnbakedModel model) {
		if (onLoadModifierContext.isEmpty()) {
			onLoadModifierContext.add(new OnLoadModifierContext());
		}

		OnLoadModifierContext context = onLoadModifierContext.pop();
		context.prepare(id);

		model = pluginContext.modifyModelOnLoad().invoker().modifyModelOnLoad(model, context);

		onLoadModifierContext.push(context);
		return model;
	}

	public UnbakedModel modifyModelBeforeBake(Identifier id, UnbakedModel model, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
		if (beforeBakeModifierContext.isEmpty()) {
			beforeBakeModifierContext.add(new BeforeBakeModifierContext());
		}

		BeforeBakeModifierContext context = beforeBakeModifierContext.pop();
		context.prepare(id, textureGetter, settings, baker);

		model = pluginContext.modifyModelBeforeBake().invoker().modifyModelBeforeBake(model, context);

		beforeBakeModifierContext.push(context);
		return model;
	}

	public BakedModel modifyModelAfterBake(Identifier id, UnbakedModel sourceModel, BakedModel bakedModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
		if (afterBakeModifierContext.isEmpty()) {
			afterBakeModifierContext.add(new AfterBakeModifierContext());
		}

		AfterBakeModifierContext context = afterBakeModifierContext.pop();
		context.prepare(id, sourceModel, textureGetter, settings, baker);

		bakedModel = pluginContext.modifyModelAfterBake().invoker().modifyModelAfterBake(bakedModel, context);

		afterBakeModifierContext.push(context);
		return bakedModel;
	}

	private class ResolverContext implements ModelResolver.Context {
		@Override
		public ModelLoader loader() {
			return loader;
		}

		@Override
		public UnbakedModel getOrLoadModel(Identifier id) {
			return ((ModelLoaderHooks) loader).fabric_getOrLoadModel(id);
		}
	}

	private class OnLoadModifierContext implements ModelModifier.OnLoad.Context {
		private Identifier id;

		private void prepare(Identifier id) {
			this.id = id;
		}

		@Override
		public Identifier id() {
			return id;
		}

		@Override
		public UnbakedModel getOrLoadModel(Identifier id) {
			return ((ModelLoaderHooks) loader).fabric_getOrLoadModel(id);
		}

		@Override
		public ModelLoader loader() {
			return loader;
		}
	}

	private class BeforeBakeModifierContext implements ModelModifier.BeforeBake.Context {
		private Identifier id;
		private Function<SpriteIdentifier, Sprite> textureGetter;
		private ModelBakeSettings settings;
		private Baker baker;

		private void prepare(Identifier id, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
			this.id = id;
			this.textureGetter = textureGetter;
			this.settings = settings;
			this.baker = baker;
		}

		@Override
		public Identifier id() {
			return id;
		}

		@Override
		public Function<SpriteIdentifier, Sprite> textureGetter() {
			return textureGetter;
		}

		@Override
		public ModelBakeSettings settings() {
			return settings;
		}

		@Override
		public Baker baker() {
			return baker;
		}

		@Override
		public ModelLoader loader() {
			return loader;
		}
	}

	private class AfterBakeModifierContext implements ModelModifier.AfterBake.Context {
		private Identifier id;
		private UnbakedModel sourceModel;
		private Function<SpriteIdentifier, Sprite> textureGetter;
		private ModelBakeSettings settings;
		private Baker baker;

		private void prepare(Identifier id, UnbakedModel sourceModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
			this.id = id;
			this.sourceModel = sourceModel;
			this.textureGetter = textureGetter;
			this.settings = settings;
			this.baker = baker;
		}

		@Override
		public Identifier id() {
			return id;
		}

		@Override
		public UnbakedModel sourceModel() {
			return sourceModel;
		}

		@Override
		public Function<SpriteIdentifier, Sprite> textureGetter() {
			return textureGetter;
		}

		@Override
		public ModelBakeSettings settings() {
			return settings;
		}

		@Override
		public Baker baker() {
			return baker;
		}

		@Override
		public ModelLoader loader() {
			return loader;
		}
	}
}
