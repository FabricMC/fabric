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
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ModelLoaderPluginContextImpl implements ModelLoadingPlugin.Context {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelLoaderPluginContextImpl.class);

	private final ResourceManager resourceManager;
	final Set<Identifier> extraModels = new LinkedHashSet<>();

	private final Event<ModelResolver.Variant> variantResolvers = EventFactory.createArrayBacked(ModelResolver.Variant.class, providers -> (modelId, context) -> {
		for (ModelResolver.Variant provider : providers) {
			try {
				UnbakedModel model = provider.resolveModelVariant(modelId, context);

				if (model != null) {
					return model;
				}
			} catch (Exception exception) {
				LOGGER.error("Failed to resolve custom model variant", exception);
			}
		}

		return null;
	});

	private final Event<ModelResolver.Resource> resourceResolvers = EventFactory.createArrayBacked(ModelResolver.Resource.class, providers -> (resourceId, context) -> {
		for (ModelResolver.Resource provider : providers) {
			try {
				UnbakedModel model = provider.resolveModelResource(resourceId, context);

				if (model != null) {
					return model;
				}
			} catch (Exception exception) {
				LOGGER.error("Failed to resolve custom model resource", exception);
			}
		}

		return null;
	});

	private static final Identifier[] MODEL_MODIFIER_PHASES = new Identifier[] { ModelModifier.OVERRIDE_PHASE, ModelModifier.DEFAULT_PHASE, ModelModifier.WRAP_PHASE, ModelModifier.WRAP_LAST_PHASE };

	private static Event<ModelModifier.Unbaked> createUnbakedModelEvent() {
		return EventFactory.createWithPhases(ModelModifier.Unbaked.class, modifiers -> (model, context) -> {
			for (ModelModifier.Unbaked modifier : modifiers) {
				try {
					model = modifier.modifyUnbakedModel(model, context);
				} catch (Exception exception) {
					LOGGER.error("Failed to modify unbaked model", exception);
				}
			}

			return model;
		}, MODEL_MODIFIER_PHASES);
	}

	private final Event<ModelModifier.Unbaked> onLoadModifiers = createUnbakedModelEvent();
	private final Event<ModelModifier.Unbaked> beforeBakeModifiers = createUnbakedModelEvent();
	private final Event<ModelModifier.Baked> afterBakeModifiers = EventFactory.createWithPhases(ModelModifier.Baked.class, modifiers -> (model, context) -> {
		for (ModelModifier.Baked modifier : modifiers) {
			try {
				model = modifier.modifyBakedModel(model, context);
			} catch (Exception exception) {
				LOGGER.error("Failed to modify baked model", exception);
			}
		}

		return model;
	}, MODEL_MODIFIER_PHASES);

	/**
	 * This field is used by the v0 wrapper to avoid constantly wrapping the context in hot code.
	 */
	public final ModelResolver.Context resolverContext;

	public ModelLoaderPluginContextImpl(ResourceManager resourceManager, ModelResolver.Context resolverContext) {
		this.resourceManager = resourceManager;
		this.resolverContext = resolverContext;
	}

	@Override
	public ResourceManager resourceManager() {
		return resourceManager;
	}

	@Override
	public void addModels(Identifier... identifiers) {
		for (Identifier id : identifiers) {
			extraModels.add(id);
		}
	}

	@Override
	public void addModels(Collection<? extends Identifier> identifiers) {
		extraModels.addAll(identifiers);
	}

	@Override
	public Event<ModelResolver.Variant> resolveModelVariant() {
		return variantResolvers;
	}

	@Override
	public Event<ModelResolver.Resource> resolveModelResource() {
		return resourceResolvers;
	}

	@Override
	public Event<ModelModifier.Unbaked> modifyModelOnLoad() {
		return onLoadModifiers;
	}

	@Override
	public Event<ModelModifier.Unbaked> modifyModelBeforeBake() {
		return beforeBakeModifiers;
	}

	@Override
	public Event<ModelModifier.Baked> modifyModelAfterBake() {
		return afterBakeModifiers;
	}
}
