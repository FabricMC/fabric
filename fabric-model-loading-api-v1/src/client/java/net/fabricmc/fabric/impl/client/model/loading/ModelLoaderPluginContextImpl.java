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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Block;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ModelLoaderPluginContextImpl implements ModelLoadingPlugin.Context {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelLoaderPluginContextImpl.class);

	final Set<Identifier> extraModels = new LinkedHashSet<>();

	private final Map<BlockKey, BlockStateResolverHolder> blockStateResolvers = new HashMap<>();
	private final BlockKey lookupKey = new BlockKey();

	private final Event<ModelResolver> modelResolvers = EventFactory.createArrayBacked(ModelResolver.class, resolvers -> context -> {
		for (ModelResolver resolver : resolvers) {
			try {
				UnbakedModel model = resolver.resolveModel(context);

				if (model != null) {
					return model;
				}
			} catch (Exception exception) {
				LOGGER.error("Failed to resolve model", exception);
			}
		}

		return null;
	});

	private static final Identifier[] MODEL_MODIFIER_PHASES = new Identifier[] { ModelModifier.OVERRIDE_PHASE, ModelModifier.DEFAULT_PHASE, ModelModifier.WRAP_PHASE, ModelModifier.WRAP_LAST_PHASE };

	private final Event<ModelModifier.OnLoad> onLoadModifiers = EventFactory.createWithPhases(ModelModifier.OnLoad.class, modifiers -> (model, context) -> {
		for (ModelModifier.OnLoad modifier : modifiers) {
			try {
				model = modifier.modifyModelOnLoad(model, context);
			} catch (Exception exception) {
				LOGGER.error("Failed to modify unbaked model on load", exception);
			}
		}

		return model;
	}, MODEL_MODIFIER_PHASES);
	private final Event<ModelModifier.BeforeBake> beforeBakeModifiers = EventFactory.createWithPhases(ModelModifier.BeforeBake.class, modifiers -> (model, context) -> {
		for (ModelModifier.BeforeBake modifier : modifiers) {
			try {
				model = modifier.modifyModelBeforeBake(model, context);
			} catch (Exception exception) {
				LOGGER.error("Failed to modify unbaked model before bake", exception);
			}
		}

		return model;
	}, MODEL_MODIFIER_PHASES);
	private final Event<ModelModifier.AfterBake> afterBakeModifiers = EventFactory.createWithPhases(ModelModifier.AfterBake.class, modifiers -> (model, context) -> {
		for (ModelModifier.AfterBake modifier : modifiers) {
			try {
				model = modifier.modifyModelAfterBake(model, context);
			} catch (Exception exception) {
				LOGGER.error("Failed to modify baked model after bake", exception);
			}
		}

		return model;
	}, MODEL_MODIFIER_PHASES);

	/**
	 * This field is used by the v0 wrapper to avoid constantly wrapping the context in hot code.
	 */
	public final Function<Identifier, UnbakedModel> modelGetter;

	public ModelLoaderPluginContextImpl(Function<Identifier, UnbakedModel> modelGetter) {
		this.modelGetter = modelGetter;
	}

	@Override
	public void addModels(Identifier... ids) {
		for (Identifier id : ids) {
			extraModels.add(id);
		}
	}

	@Override
	public void addModels(Collection<? extends Identifier> ids) {
		extraModels.addAll(ids);
	}

	@Override
	public void registerBlockStateResolver(Block block, BlockStateResolver resolver) {
		Objects.requireNonNull(block, "block cannot be null");
		Objects.requireNonNull(resolver, "resolver cannot be null");

		Optional<RegistryKey<Block>> optionalKey = Registries.BLOCK.getKey(block);

		if (optionalKey.isEmpty()) {
			throw new IllegalArgumentException("Received unregistered block");
		}

		Identifier blockId = optionalKey.get().getValue();
		BlockKey key = new BlockKey(blockId.getNamespace(), blockId.getPath());
		BlockStateResolverHolder holder = new BlockStateResolverHolder(resolver, block, blockId);

		if (blockStateResolvers.put(key, holder) != null) {
			throw new IllegalArgumentException("Duplicate block state resolver for block " + blockId);
		}
	}

	@Nullable
	BlockStateResolverHolder getBlockStateResolver(ModelIdentifier modelId) {
		BlockKey key = lookupKey;
		key.namespace = modelId.getNamespace();
		key.path = modelId.getPath();

		return blockStateResolvers.get(key);
	}

	@Override
	public Event<ModelResolver> resolveModel() {
		return modelResolvers;
	}

	@Override
	public Event<ModelModifier.OnLoad> modifyModelOnLoad() {
		return onLoadModifiers;
	}

	@Override
	public Event<ModelModifier.BeforeBake> modifyModelBeforeBake() {
		return beforeBakeModifiers;
	}

	@Override
	public Event<ModelModifier.AfterBake> modifyModelAfterBake() {
		return afterBakeModifiers;
	}

	private static class BlockKey {
		private String namespace;
		private String path;

		private BlockKey() {
		}

		private BlockKey(String namespace, String path) {
			this.namespace = namespace;
			this.path = path;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			BlockKey blockKey = (BlockKey) o;
			return namespace.equals(blockKey.namespace) && path.equals(blockKey.path);
		}

		@Override
		public int hashCode() {
			return 31 * namespace.hashCode() + path.hashCode();
		}
	}

	// Legacy v0 bridge - remove if the legacy v0 module is removed.

	private final Event<LegacyModelVariantProvider> legacyVariantProviders = EventFactory.createArrayBacked(LegacyModelVariantProvider.class, providers -> modelId -> {
		for (LegacyModelVariantProvider provider : providers) {
			try {
				UnbakedModel model = provider.loadModelVariant(modelId);

				if (model != null) {
					return model;
				}
			} catch (Exception exception) {
				LOGGER.error("Failed to run legacy model variant provider", exception);
			}
		}

		return null;
	});

	public Event<LegacyModelVariantProvider> legacyVariantProviders() {
		return legacyVariantProviders;
	}
}
