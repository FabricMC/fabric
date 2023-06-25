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

import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResourceProvider;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelVariantProvider;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public class ModelLoaderPluginContextImpl implements ModelLoadingPlugin.Context {
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelLoaderPluginContextImpl.class);

	private final ResourceManager resourceManager;
	final Set<Identifier> extraModels = new LinkedHashSet<>();
	private final Event<ModelVariantProvider> variantProviders = EventFactory.createArrayBacked(ModelVariantProvider.class, providers -> (modelId, context) -> {
		for (ModelVariantProvider provider : providers) {
			try {
				UnbakedModel model = provider.loadModelVariant(modelId, context);

				if (model != null) {
					return model;
				}
			} catch (Exception exception) {
				LOGGER.error("Failed to load custom model variant", exception);
			}
		}

		return null;
	});
	private final Event<ModelResourceProvider> resourceProviders = EventFactory.createArrayBacked(ModelResourceProvider.class, providers -> (resourceId, context) -> {
		for (ModelResourceProvider provider : providers) {
			try {
				UnbakedModel model = provider.loadModelResource(resourceId, context);

				if (model != null) {
					return model;
				}
			} catch (Exception exception) {
				LOGGER.error("Failed to load custom model resource", exception);
			}
		}

		return null;
	});

	/**
	 * This field is used by the v0 wrapper to avoid constantly wrapping the context in hot code.
	 */
	public final ModelProviderContext providerContext;

	public ModelLoaderPluginContextImpl(ResourceManager resourceManager, ModelProviderContext providerContext) {
		this.resourceManager = resourceManager;
		this.providerContext = providerContext;
	}

	@Override
	public ResourceManager resourceManager() {
		return resourceManager;
	}

	@Override
	public void addModel(Identifier identifier) {
		extraModels.add(identifier);
	}

	@Override
	public Event<ModelVariantProvider> variantProviders() {
		return variantProviders;
	}

	@Override
	public Event<ModelResourceProvider> resourceProviders() {
		return resourceProviders;
	}
}
