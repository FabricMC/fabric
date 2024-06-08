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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
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
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;

public class ModelLoadingEventDispatcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelLoadingEventDispatcher.class);

	private final ModelLoader loader;
	private final ModelLoadingPluginContextImpl pluginContext;

	private final ObjectArrayList<ModelResolverContext> modelResolverContextStack = new ObjectArrayList<>();
	private final BlockStateResolverContext blockStateResolverContext = new BlockStateResolverContext();

	private final ObjectArrayList<OnLoadModifierContext> onLoadModifierContextStack = new ObjectArrayList<>();
	private final ObjectArrayList<BeforeBakeModifierContext> beforeBakeModifierContextStack = new ObjectArrayList<>();
	private final ObjectArrayList<AfterBakeModifierContext> afterBakeModifierContextStack = new ObjectArrayList<>();

	public ModelLoadingEventDispatcher(ModelLoader loader, List<ModelLoadingPlugin> plugins) {
		this.loader = loader;
		this.pluginContext = new ModelLoadingPluginContextImpl();

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

	public boolean loadBlockStateModels(Identifier id, StateManager<Block, BlockState> stateManager) {
		BlockStateResolver resolver = pluginContext.blockStateResolvers.get(id);

		if (resolver != null) {
			resolveBlockStates(resolver, stateManager.getOwner(), id);
			return true;
		} else {
			return false;
		}
	}

	private void resolveBlockStates(BlockStateResolver resolver, Block block, Identifier blockId) {
		BlockStateResolverContext context = blockStateResolverContext;
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
				((ModelLoaderHooks) loader).fabric_add(modelId, missingModel);
			}
		} else if (resolvedModels.size() == allStates.size()) {
			// If there are as many resolved models as total states, all states have
			// been resolved and models do not need to be null-checked.
			resolvedModels.forEach((state, model) -> {
				ModelIdentifier modelId = BlockModels.getModelId(blockId, state);
				((ModelLoaderHooks) loader).fabric_add(modelId, model);
			});
		} else {
			UnbakedModel missingModel = ((ModelLoaderHooks) loader).fabric_getMissingModel();

			for (BlockState state : allStates) {
				ModelIdentifier modelId = BlockModels.getModelId(blockId, state);
				@Nullable
				UnbakedModel model = resolvedModels.get(state);

				if (model == null) {
					LOGGER.error("Block state resolver did not provide a model for state {} in block {}. Using missing model.", state, block);
					((ModelLoaderHooks) loader).fabric_add(modelId, missingModel);
				} else {
					((ModelLoaderHooks) loader).fabric_add(modelId, model);
				}
			}
		}

		resolvedModels.clear();
	}

	@Nullable
	public UnbakedModel resolveModel(Identifier id) {
		if (modelResolverContextStack.isEmpty()) {
			modelResolverContextStack.add(new ModelResolverContext());
		}

		ModelResolverContext context = modelResolverContextStack.pop();
		context.prepare(id);

		UnbakedModel model = pluginContext.resolveModel().invoker().resolveModel(context);

		modelResolverContextStack.push(context);
		return model;
	}

	public UnbakedModel modifyModelOnLoad(UnbakedModel model, @UnknownNullability Identifier resourceId, @UnknownNullability ModelIdentifier topLevelId) {
		if (onLoadModifierContextStack.isEmpty()) {
			onLoadModifierContextStack.add(new OnLoadModifierContext());
		}

		OnLoadModifierContext context = onLoadModifierContextStack.pop();
		context.prepare(resourceId, topLevelId);

		model = pluginContext.modifyModelOnLoad().invoker().modifyModelOnLoad(model, context);

		onLoadModifierContextStack.push(context);
		return model;
	}

	public UnbakedModel modifyModelBeforeBake(UnbakedModel model, @UnknownNullability Identifier resourceId, @UnknownNullability ModelIdentifier topLevelId, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
		if (beforeBakeModifierContextStack.isEmpty()) {
			beforeBakeModifierContextStack.add(new BeforeBakeModifierContext());
		}

		BeforeBakeModifierContext context = beforeBakeModifierContextStack.pop();
		context.prepare(resourceId, topLevelId, textureGetter, settings, baker);

		model = pluginContext.modifyModelBeforeBake().invoker().modifyModelBeforeBake(model, context);

		beforeBakeModifierContextStack.push(context);
		return model;
	}

	@Nullable
	public BakedModel modifyModelAfterBake(@Nullable BakedModel model, @UnknownNullability Identifier resourceId, @UnknownNullability ModelIdentifier topLevelId, UnbakedModel sourceModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
		if (afterBakeModifierContextStack.isEmpty()) {
			afterBakeModifierContextStack.add(new AfterBakeModifierContext());
		}

		AfterBakeModifierContext context = afterBakeModifierContextStack.pop();
		context.prepare(resourceId, topLevelId, sourceModel, textureGetter, settings, baker);

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
		@UnknownNullability
		private Identifier resourceId;
		@UnknownNullability
		private ModelIdentifier topLevelId;

		private void prepare(@UnknownNullability Identifier resourceId, @UnknownNullability ModelIdentifier topLevelId) {
			this.resourceId = resourceId;
			this.topLevelId = topLevelId;
		}

		@Override
		@UnknownNullability("#topLevelId() != null")
		public Identifier resourceId() {
			return resourceId;
		}

		@Override
		@UnknownNullability("#resourceId() != null")
		public ModelIdentifier topLevelId() {
			return topLevelId;
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
		@UnknownNullability
		private Identifier resourceId;
		@UnknownNullability
		private ModelIdentifier topLevelId;
		private Function<SpriteIdentifier, Sprite> textureGetter;
		private ModelBakeSettings settings;
		private Baker baker;

		private void prepare(@UnknownNullability Identifier resourceId, @UnknownNullability ModelIdentifier topLevelId, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
			this.resourceId = resourceId;
			this.topLevelId = topLevelId;
			this.textureGetter = textureGetter;
			this.settings = settings;
			this.baker = baker;
		}

		@Override
		@UnknownNullability("#topLevelId() != null")
		public Identifier resourceId() {
			return resourceId;
		}

		@Override
		@UnknownNullability("#resourceId() != null")
		public ModelIdentifier topLevelId() {
			return topLevelId;
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
		@UnknownNullability
		private Identifier resourceId;
		@UnknownNullability
		private ModelIdentifier topLevelId;
		private UnbakedModel sourceModel;
		private Function<SpriteIdentifier, Sprite> textureGetter;
		private ModelBakeSettings settings;
		private Baker baker;

		private void prepare(@UnknownNullability Identifier resourceId, @UnknownNullability ModelIdentifier topLevelId, UnbakedModel sourceModel, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Baker baker) {
			this.resourceId = resourceId;
			this.topLevelId = topLevelId;
			this.sourceModel = sourceModel;
			this.textureGetter = textureGetter;
			this.settings = settings;
			this.baker = baker;
		}

		@Override
		@UnknownNullability("#topLevelId() != null")
		public Identifier resourceId() {
			return resourceId;
		}

		@Override
		@UnknownNullability("#resourceId() != null")
		public ModelIdentifier topLevelId() {
			return topLevelId;
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
