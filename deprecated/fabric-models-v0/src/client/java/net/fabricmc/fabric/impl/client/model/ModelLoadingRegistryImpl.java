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

package net.fabricmc.fabric.impl.client.model;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import net.minecraft.resource.ResourceManager;

import net.fabricmc.fabric.api.client.model.ExtraModelProvider;
import net.fabricmc.fabric.api.client.model.ModelAppender;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoaderPluginContextImpl;

public class ModelLoadingRegistryImpl implements ModelLoadingRegistry {
	@Override
	public void registerModelProvider(ExtraModelProvider appender) {
		registerResourceManagerPlugin((resourceManager, pluginContext) -> {
			appender.provideExtraModels(resourceManager, pluginContext::addModels);
		});
	}

	@Override
	public void registerAppender(ModelAppender appender) {
		registerModelProvider((manager, consumer) -> appender.appendAll(manager, consumer::accept));
	}

	@Override
	public void registerResourceProvider(Function<ResourceManager, ModelResourceProvider> providerSupplier) {
		registerResourceManagerPlugin((resourceManager, pluginContext) -> {
			ModelResourceProvider provider = providerSupplier.apply(resourceManager);
			ModelProviderContext providerContext = makeOldContext(pluginContext);

			pluginContext.resolveModel().register((resourceId, modelProviderContext) -> {
				try {
					return provider.loadModelResource(resourceId, providerContext);
				} catch (ModelProviderException e) {
					throw new RuntimeException(e);
				}
			});
		});
	}

	@Override
	public void registerVariantProvider(Function<ResourceManager, ModelVariantProvider> providerSupplier) {
		registerResourceManagerPlugin((resourceManager, pluginContext) -> {
			ModelVariantProvider provider = providerSupplier.apply(resourceManager);
			ModelProviderContext providerContext = makeOldContext(pluginContext);

			// TODO: v1 API does not directly support model variant providers, find a way to support this
//			pluginContext.resolveModelVariant().register((modelId, modelProviderContext) -> {
//				try {
//					return provider.loadModelVariant(modelId, providerContext);
//				} catch (ModelProviderException e) {
//					throw new RuntimeException(e);
//				}
//			});
		});
	}

	/**
	 * Grabs the resource manager to use it in the main model loading code.
	 * When using the v1 API, data should be loaded in parallel before model loading starts.
	 */
	private static void registerResourceManagerPlugin(PreparableModelLoadingPlugin<ResourceManager> plugin) {
		PreparableModelLoadingPlugin.register(
				(resourceManager, executor) -> CompletableFuture.completedFuture(resourceManager),
				plugin);
	}

	/**
	 * A bit hacky, but avoids the allocation of a new context wrapper every time.
	 */
	private static ModelProviderContext makeOldContext(ModelLoadingPlugin.Context pluginContext) {
		return ((ModelLoaderPluginContextImpl) pluginContext).resolverContext::getOrLoadModel;
	}
}
