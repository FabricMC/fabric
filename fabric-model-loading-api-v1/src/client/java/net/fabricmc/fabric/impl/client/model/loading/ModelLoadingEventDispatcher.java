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

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
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
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;

public class ModelLoadingEventDispatcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelLoadingEventDispatcher.class);

	private final ModelLoader loader;
	private final ModelLoaderPluginContextImpl pluginContext;

	private final ObjectArrayList<ModelResolverContext> modelResolverContextStack = new ObjectArrayList<>();

	private final ObjectArrayList<BlockStateResolverContext> blockStateResolverContextStack = new ObjectArrayList<>();
	private final ReferenceSet<Block> resolvingBlocks = new ReferenceOpenHashSet<>();

	private final ObjectArrayList<OnLoadModifierContext> onLoadModifierContextStack = new ObjectArrayList<>();
	private final ObjectArrayList<BeforeBakeModifierContext> beforeBakeModifierContextStack = new ObjectArrayList<>();
	private final ObjectArrayList<AfterBakeModifierContext> afterBakeModifierContextStack = new ObjectArrayList<>();

	public ModelLoadingEventDispatcher(ModelLoader loader, List<ModelLoadingPlugin> plugins) {
		this.loader = loader;
		this.pluginContext = new ModelLoaderPluginContextImpl(((ModelLoaderHooks) loader)::fabric_getOrLoadModel);

		for (ModelLoadingPlugin plugin : plugins) {
			try {
				plugin.onInitializeModelLoader(pluginContext);
			} catch (Exception exception) {
				LOGGER.error("Failed to initialize model loading plugin", exception);
			}
		}
	}

	public void addExtraModels(Consumer<Identifier> extraModelConsumer) {
		for (Identifier id : pluginContext.extraModels) {
			extraModelConsumer.accept(id);
		}
	}

	/**
	 * @return {@code true} to cancel the vanilla method
	 */
	public boolean loadModel(Identifier id) {
		if (id instanceof ModelIdentifier modelId) {
			if ("inventory".equals(modelId.getVariant())) {
				// We ALWAYS override the vanilla inventory model code path entirely, even for vanilla item models.
				// See loadItemModel for an explanation.
				loadItemModel(modelId);
				return true;
			} else {
				// Prioritize block state resolver over legacy variant provider
				BlockStateResolverHolder resolver = pluginContext.getBlockStateResolver(modelId);

				if (resolver != null) {
					loadBlockStateModels(resolver.resolver(), resolver.block(), resolver.blockId());
					return true;
				}

				UnbakedModel legacyModel = legacyLoadModelVariant(modelId);

				if (legacyModel != null) {
					((ModelLoaderHooks) loader).fabric_putModel(id, legacyModel);
					return true;
				}

				return false;
			}
		} else {
			UnbakedModel model = resolveModel(id);

			if (model != null) {
				((ModelLoaderHooks) loader).fabric_putModel(id, model);
				return true;
			}

			return false;
		}
	}

	@Nullable
	private UnbakedModel legacyLoadModelVariant(ModelIdentifier modelId) {
		return pluginContext.legacyVariantProviders().invoker().loadModelVariant(modelId);
	}

	/**
	 * This function handles both modded item models and vanilla item models.
	 * The vanilla code path for item models is never used.
	 * See the long comment in the function for an explanation.
	 */
	private void loadItemModel(ModelIdentifier modelId) {
		ModelLoaderHooks loaderHooks = (ModelLoaderHooks) loader;

		Identifier id = modelId.withPrefixedPath("item/");

		// Legacy variant provider
		UnbakedModel model = legacyLoadModelVariant(modelId);

		// Model resolver
		if (model == null) {
			model = resolveModel(id);
		}

		// Load from the vanilla code path otherwise.
		if (model == null) {
			model = loaderHooks.fabric_loadModelFromJson(id);
		}

		// This is a bit tricky:
		// We have a single UnbakedModel now, but there are two identifiers:
		// the ModelIdentifier (...#inventory) and the Identifier (...:item/...).
		// So we call the on load modifier now and then directly add the model to the ModelLoader,
		// reimplementing the behavior of ModelLoader#put.
		// Calling ModelLoader#put is not an option as the model for the Identifier would not be replaced by an on load modifier.
		// This is why we override the vanilla code path entirely.
		model = modifyModelOnLoad(modelId, model);

		loaderHooks.fabric_putModelDirectly(modelId, model);
		loaderHooks.fabric_putModelDirectly(id, model);
		loaderHooks.fabric_queueModelDependencies(model);
	}

	private void loadBlockStateModels(BlockStateResolver resolver, Block block, Identifier blockId) {
		if (!resolvingBlocks.add(block)) {
			throw new IllegalStateException("Circular reference while resolving models for block " + block);
		}

		try {
			resolveBlockStates(resolver, block, blockId);
		} finally {
			resolvingBlocks.remove(block);
		}
	}

	private void resolveBlockStates(BlockStateResolver resolver, Block block, Identifier blockId) {
		// Get and prepare context
		if (blockStateResolverContextStack.isEmpty()) {
			blockStateResolverContextStack.add(new BlockStateResolverContext());
		}

		BlockStateResolverContext context = blockStateResolverContextStack.pop();
		context.prepare(block);

		Reference2ReferenceMap<BlockState, UnbakedModel> resolvedModels = context.models;
		ImmutableList<BlockState> allStates = block.getStateManager().getStates();
		boolean thrown = false;

		// Call resolver
		try {
			resolver.resolveBlockStates(context);
		} catch (Exception e) {
			LOGGER.error("Failed to resolve block state models for block {}. Using missing model for all states.", block, e);
			thrown = true;
		}

		// Copy models over to the loader
		if (thrown) {
			UnbakedModel missingModel = ((ModelLoaderHooks) loader).fabric_getMissingModel();

			for (BlockState state : allStates) {
				ModelIdentifier modelId = BlockModels.getModelId(blockId, state);
				((ModelLoaderHooks) loader).fabric_putModelDirectly(modelId, missingModel);
			}
		} else if (resolvedModels.size() == allStates.size()) {
			// If there are as many resolved models as total states, all states have
			// been resolved and models do not need to be null-checked.
			resolvedModels.forEach((state, model) -> {
				ModelIdentifier modelId = BlockModels.getModelId(blockId, state);
				((ModelLoaderHooks) loader).fabric_putModel(modelId, model);
			});
		} else {
			UnbakedModel missingModel = ((ModelLoaderHooks) loader).fabric_getMissingModel();

			for (BlockState state : allStates) {
				ModelIdentifier modelId = BlockModels.getModelId(blockId, state);
				@Nullable
				UnbakedModel model = resolvedModels.get(state);

				if (model == null) {
					LOGGER.error("Block state resolver did not provide a model for state {} in block {}. Using missing model.", state, block);
					((ModelLoaderHooks) loader).fabric_putModelDirectly(modelId, missingModel);
				} else {
					((ModelLoaderHooks) loader).fabric_putModel(modelId, model);
				}
			}
		}

		resolvedModels.clear();

		// Store context for reuse
		blockStateResolverContextStack.add(context);
	}

	@Nullable
	private UnbakedModel resolveModel(Identifier id) {
		if (modelResolverContextStack.isEmpty()) {
			modelResolverContextStack.add(new ModelResolverContext());
		}

		ModelResolverContext context = modelResolverContextStack.pop();
		context.prepare(id);

		UnbakedModel model = pluginContext.resolveModel().invoker().resolveModel(context);

		modelResolverContextStack.push(context);
		return model;
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

	public UnbakedModel modifyModelBeforeBake(UnbakedModel model, Identifier id, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
		if (beforeBakeModifierContextStack.isEmpty()) {
			beforeBakeModifierContextStack.add(new BeforeBakeModifierContext());
		}

		BeforeBakeModifierContext context = beforeBakeModifierContextStack.pop();
		context.prepare(id, textureGetter, settings, baker);

		model = pluginContext.modifyModelBeforeBake().invoker().modifyModelBeforeBake(model, context);

		beforeBakeModifierContextStack.push(context);
		return model;
	}

	public BakedModel modifyModelAfterBake(BakedModel model, Identifier id, UnbakedModel sourceModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
		if (afterBakeModifierContextStack.isEmpty()) {
			afterBakeModifierContextStack.add(new AfterBakeModifierContext());
		}

		AfterBakeModifierContext context = afterBakeModifierContextStack.pop();
		context.prepare(id, sourceModel, textureGetter, settings, baker);

		model = pluginContext.modifyModelAfterBake().invoker().modifyModelAfterBake(model, context);

		afterBakeModifierContextStack.push(context);
		return model;
	}

	private class ModelResolverContext implements ModelResolver.Context {
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

	private class BlockStateResolverContext implements BlockStateResolver.Context {
		private Block block;
		private final Reference2ReferenceMap<BlockState, UnbakedModel> models = new Reference2ReferenceOpenHashMap<>();

		private void prepare(Block block) {
			this.block = block;
			models.clear();
		}

		@Override
		public Block block() {
			return block;
		}

		@Override
		public void setModel(BlockState state, UnbakedModel model) {
			Objects.requireNonNull(model, "state cannot be null");
			Objects.requireNonNull(model, "model cannot be null");

			if (!state.isOf(block)) {
				throw new IllegalArgumentException("Attempted to set model for state " + state + " on block " + block);
			}

			if (models.putIfAbsent(state, model) != null) {
				throw new IllegalStateException("Duplicate model for state " + state + " on block " + block);
			}
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
