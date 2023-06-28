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
	private final Event<ModelResolver.Variant> variantProviders = EventFactory.createArrayBacked(ModelResolver.Variant.class, providers -> (modelId, context) -> {
		for (ModelResolver.Variant provider : providers) {
			try {
				UnbakedModel model = provider.resolveModelVariant(modelId, context);

				if (model != null) {
					return model;
				}
			} catch (Exception exception) {
				LOGGER.error("Failed to load custom model variant", exception);
			}
		}

		return null;
	});
	private final Event<ModelResolver.Resource> resourceProviders = EventFactory.createArrayBacked(ModelResolver.Resource.class, providers -> (resourceId, context) -> {
		for (ModelResolver.Resource provider : providers) {
			try {
				UnbakedModel model = provider.resolveModelResource(resourceId, context);

				if (model != null) {
					return model;
				}
			} catch (Exception exception) {
				LOGGER.error("Failed to load custom model resource", exception);
			}
		}

		return null;
	});

	private static final Identifier[] MODEL_MODIFIER_PHASES = new Identifier[]{ ModelModifier.OVERRIDE_PHASE, ModelModifier.DEFAULT_PHASE, ModelModifier.WRAP_PHASE, ModelModifier.WRAP_LAST_PHASE };

	private static Event<ModelModifier.Unbaked> createUnbakedModelEvent() {
		return EventFactory.createWithPhases(ModelModifier.Unbaked.class, modifiers -> (model, context) -> {
			for (ModelModifier.Unbaked modifier : modifiers) {
				try {
					model = modifier.modifyUnbakedModel(model, context);
				} catch (Exception exception) {
					LOGGER.error("Unbaked model modifier threw error", exception);
				}
			}

			return model;
		}, MODEL_MODIFIER_PHASES);
	}

	private final Event<ModelModifier.Unbaked> unbakedModelLoadModifiers = createUnbakedModelEvent();
	private final Event<ModelModifier.Unbaked> unbakedModelPreBakeModifiers = createUnbakedModelEvent();
	private final Event<ModelModifier.Baked> bakedModelLoadModifiers = EventFactory.createWithPhases(ModelModifier.Baked.class, modifiers -> (model, context) -> {
		for (ModelModifier.Baked modifier : modifiers) {
			try {
				model = modifier.modifyBakedModel(model, context);
			} catch (Exception exception) {
				LOGGER.error("Baked model modifier threw error", exception);
			}
		}

		return model;
	}, MODEL_MODIFIER_PHASES);

	/**
	 * This field is used by the v0 wrapper to avoid constantly wrapping the context in hot code.
	 */
	public final ModelResolver.Context providerContext;

	public ModelLoaderPluginContextImpl(ResourceManager resourceManager, ModelResolver.Context providerContext) {
		this.resourceManager = resourceManager;
		this.providerContext = providerContext;
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
		return variantProviders;
	}

	@Override
	public Event<ModelResolver.Resource> resolveModelResource() {
		return resourceProviders;
	}

	@Override
	public Event<ModelModifier.Unbaked> modifyModelOnLoad() {
		return unbakedModelLoadModifiers;
	}

	@Override
	public Event<ModelModifier.Unbaked> modifyModelBeforeBake() {
		return unbakedModelPreBakeModifiers;
	}

	@Override
	public Event<ModelModifier.Baked> modifyModelAfterBake() {
		return bakedModelLoadModifiers;
	}
}
