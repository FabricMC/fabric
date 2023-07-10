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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
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
	private final ModelLoaderPluginContextImpl pluginContext;

	private final ObjectArrayList<BlockStateResolverContext> blockStateResolverContextStack = new ObjectArrayList<>();
	private final Set<Block> resolvingBlocks = Collections.newSetFromMap(new IdentityHashMap<>());

	private final ObjectArrayList<OnLoadModifierContext> onLoadModifierContextStack = new ObjectArrayList<>();
	private final ObjectArrayList<BeforeBakeModifierContext> beforeBakeModifierContextStack = new ObjectArrayList<>();
	private final ObjectArrayList<AfterBakeModifierContext> afterBakeModifierContextStack = new ObjectArrayList<>();

	public ModelLoadingEventDispatcher(ModelLoader loader) {
		this.loader = loader;
		this.resolverContext = new ResolverContext();
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

		BlockStateResolver resolver = pluginContext.getResolver(id);

		if (resolver != null) {
			Identifier blockId = new Identifier(id.getNamespace(), id.getPath());
			Block block = Registries.BLOCK.get(blockId);

			if (!resolvingBlocks.add(block)) {
				throw new IllegalStateException("Circular reference while models for block " + block);
			}

			try {
				resolveBlockStates(block, blockId, resolver);
			} finally {
				resolvingBlocks.remove(block);
			}

			return true;
		}

		return false;
	}

	@Nullable
	private UnbakedModel resolveModelResource(Identifier resourceId) {
		return pluginContext.resolveModel().invoker().resolveModel(resourceId, resolverContext);
	}

	private void resolveBlockStates(Block block, Identifier blockId, BlockStateResolver resolver) {
		// Get and prepare context
		if (blockStateResolverContextStack.isEmpty()) {
			blockStateResolverContextStack.add(new BlockStateResolverContext());
		}

		BlockStateResolverContext context = blockStateResolverContextStack.pop();
		context.prepare(block);
		Map<BlockState, UnbakedModel> resolvedModels = context.models;

		// Call resolver
		try {
			resolver.resolveBlockStates(context);
		} catch (Exception e) {
			LOGGER.error("Failed to resolve blockstates for block {}", context.block, e);
			// Clear map to fill with missing models.
			resolvedModels.clear();
		}

		// Copy models over to the loader
		for (BlockState state : context.block.getStateManager().getStates()) {
			@Nullable
			UnbakedModel resolvedModel = resolvedModels.get(state);
			ModelIdentifier modelId = BlockModels.getModelId(blockId, state);

			if (resolvedModel == null) {
				LOGGER.error("Resolver failed to supply a model for blockstate {} in block {}. Using missing model instead.", state, context.block);
				resolvedModel = ((ModelLoaderHooks) loader).fabric_getMissingModel();
			}

			((ModelLoaderHooks) loader).fabric_putModel(modelId, resolvedModel);
		}

		// Store context for reuse
		blockStateResolverContextStack.add(context);
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
		private Block block;
		private final Map<BlockState, UnbakedModel> models = new IdentityHashMap<>();

		private void prepare(Block block) {
			this.block = block;
			models.clear();
		}

		@Override
		public void setModel(BlockState state, UnbakedModel model) {
			if (!state.isOf(block)) {
				throw new IllegalArgumentException("Attempted to set model for " + state + " on block " + block);
			}

			if (models.put(state, model) != null) {
				throw new IllegalStateException("Duplicate model for " + state + " on block " + block);
			}
		}

		@Override
		public UnbakedModel getOrLoadModel(Identifier id) {
			return ((ModelLoaderHooks) loader).fabric_getOrLoadModel(id);
		}

		@Override
		public Block block() {
			return block;
		}

		@Override
		public ModelLoader loader() {
			return loader;
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
