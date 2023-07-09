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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Block;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;

public class ModelLoadingEventDispatcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelLoadingEventDispatcher.class);

	private record RegisteredPlugin<T>(PreparableModelLoadingPlugin.DataLoader<T> loader, PreparableModelLoadingPlugin<T> plugin) { }

	private static final List<RegisteredPlugin<?>> PLUGINS = new ArrayList<>();

	public static void registerPlugin(ModelLoadingPlugin plugin) {
		registerPlugin(
				(resourceManager, executor) -> CompletableFuture.completedFuture(null),
				(data, pluginContext) -> plugin.onInitializeModelLoader(pluginContext));
	}

	public static <T> void registerPlugin(PreparableModelLoadingPlugin.DataLoader<T> loader, PreparableModelLoadingPlugin<T> plugin) {
		PLUGINS.add(new RegisteredPlugin<>(loader, plugin));
	}

	private static <T> CompletableFuture<ModelLoadingPlugin> preparePlugin(RegisteredPlugin<T> plugin, ResourceManager resourceManager, Executor executor) {
		CompletableFuture<T> dataFuture = plugin.loader.load(resourceManager, executor);
		return dataFuture.thenApply(data -> pluginContext -> plugin.plugin.onInitializeModelLoader(data, pluginContext));
	}

	/**
	 * The current exception behavior as of 1.20 is as follows.
	 * If getting a {@link CompletableFuture}s throws then the whole client will crash.
	 * If a {@link CompletableFuture} completes exceptionally then the resource reload will fail.
	 */
	public static CompletableFuture<List<ModelLoadingPlugin>> preparePlugins(ResourceManager resourceManager, Executor executor) {
		List<CompletableFuture<ModelLoadingPlugin>> futures = new ArrayList<>();

		for (RegisteredPlugin<?> plugin : PLUGINS) {
			futures.add(preparePlugin(plugin, resourceManager, executor));
		}

		return Util.combine(futures);
	}

	public static final ThreadLocal<List<ModelLoadingPlugin>> CURRENT_PLUGINS = new ThreadLocal<>();

	private final ModelLoader loader;
	private final ResolverContext resolverContext;
	private final BlockStateResolverContext blockStateResolverContext;
	private final ModelLoaderPluginContextImpl pluginContext;

	private final ObjectArrayList<OnLoadModifierContext> onLoadModifierContextStack = new ObjectArrayList<>();
	private final ObjectArrayList<BeforeBakeModifierContext> beforeBakeModifierContextStack = new ObjectArrayList<>();
	private final ObjectArrayList<AfterBakeModifierContext> afterBakeModifierContextStack = new ObjectArrayList<>();

	public ModelLoadingEventDispatcher(ModelLoader loader) {
		this.loader = loader;
		this.resolverContext = new ResolverContext();
		this.blockStateResolverContext = new BlockStateResolverContext();
		this.pluginContext = new ModelLoaderPluginContextImpl(resolverContext);

		for (ModelLoadingPlugin plugin : CURRENT_PLUGINS.get()) {
			try {
				plugin.onInitializeModelLoader(pluginContext);
			} catch (Exception exception) {
				LOGGER.error("Failed to initialize model loading plugin {}", plugin.getClass().getName(), exception);
			}
		}

		CURRENT_PLUGINS.remove();
	}

	public void addExtraModels(Consumer<Identifier> extraModelConsumer) {
		for (Identifier id : pluginContext.extraModels) {
			extraModelConsumer.accept(id);
		}
	}

	public boolean resolveModel(Identifier id) {
		if (id instanceof ModelIdentifier modelId) {
			return resolveModelVariant(modelId);
		} else {
			UnbakedModel model = resolveModelResource(id);
			if (model != null) {
				((ModelLoaderHooks) loader).fabric_putModel(id, model);
				return true;
			}
			return false;
		}
	}

	private boolean resolveModelVariant(ModelIdentifier id) {
		// Replicating the special-case from ModelLoader as loadModelFromJson is insufficiently patchable
		if (Objects.equals(id.getVariant(), "inventory")) {
			Identifier resourceId = id.withPrefixedPath("item/");
			UnbakedModel model = resolveModelResource(resourceId);
			if (model != null) {
				((ModelLoaderHooks) loader).fabric_putModel(id, model);
				// loader.unbakedModels.put(resourceId, model);
				return true;
			}
			return false;
		}

		Identifier blockId = new Identifier(id.getNamespace(), id.getPath());
		if (Registries.BLOCK.containsId(blockId)) {
			Block block = Registries.BLOCK.get(blockId);
			BlockStateResolver resolver = BlockStateResolverRegistry.get(block);
			if (resolver != null) {
				resolver.resolve(blockStateResolverContext);
				// TODO: Check to ensure the model maps actually have models for all ModelIdentifiers of this block.
				// If not, populate missing IDs with the missing model or throw an exception.
				return true;
			}
		}

		return false;
	}

	@Nullable
	private UnbakedModel resolveModelResource(Identifier resourceId) {
		return pluginContext.resolveModelResource().invoker().resolveModelResource(resourceId, resolverContext);
	}

	public UnbakedModel modifyModelOnLoad(Identifier id, UnbakedModel model) {
		if (onLoadModifierContextStack.isEmpty()) {
			onLoadModifierContextStack.add(new OnLoadModifierContext());
		}

		OnLoadModifierContext context = onLoadModifierContextStack.pop();
		context.prepare(id);

		model = pluginContext.modifyModelOnLoad().invoker().modifyModelOnLoad(model, context);

		onLoadModifierContextStack.push(context);
		return model;
	}

	public UnbakedModel modifyModelBeforeBake(Identifier id, UnbakedModel model, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
		if (beforeBakeModifierContextStack.isEmpty()) {
			beforeBakeModifierContextStack.add(new BeforeBakeModifierContext());
		}

		BeforeBakeModifierContext context = beforeBakeModifierContextStack.pop();
		context.prepare(id, textureGetter, settings, baker);

		model = pluginContext.modifyModelBeforeBake().invoker().modifyModelBeforeBake(model, context);

		beforeBakeModifierContextStack.push(context);
		return model;
	}

	public BakedModel modifyModelAfterBake(Identifier id, UnbakedModel sourceModel, BakedModel bakedModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
		if (afterBakeModifierContextStack.isEmpty()) {
			afterBakeModifierContextStack.add(new AfterBakeModifierContext());
		}

		AfterBakeModifierContext context = afterBakeModifierContextStack.pop();
		context.prepare(id, sourceModel, textureGetter, settings, baker);

		bakedModel = pluginContext.modifyModelAfterBake().invoker().modifyModelAfterBake(bakedModel, context);

		afterBakeModifierContextStack.push(context);
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

	private class BlockStateResolverContext implements BlockStateResolver.Context {
		@Override
		public void putModel(ModelIdentifier id, UnbakedModel model) {
			((ModelLoaderHooks) loader).fabric_putModel(id, model);
		}

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
