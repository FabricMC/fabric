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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

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
	private final List<ExtraModelProvider> modelProviders = new ArrayList<>();
	private final List<ModelAppender> modelAppenders = new ArrayList<>();
	private final List<Function<ResourceManager, ModelResourceProvider>> resourceProviderSuppliers = new ArrayList<>();
	private final List<Function<ResourceManager, ModelVariantProvider>> variantProviderSuppliers = new ArrayList<>();

	{
		// Grabs the resource manager to use it in the main model loading code.
		// When using the v1 API, data should be loaded in parallel before model loading starts.
		PreparableModelLoadingPlugin.register(
				(resourceManager, executor) -> CompletableFuture.completedFuture(resourceManager),
				this::onInitializeModelLoader);
	}

	private void onInitializeModelLoader(ResourceManager resourceManager, ModelLoadingPlugin.Context pluginContext) {
		Consumer<Identifier> extraModelConsumer = pluginContext::addModels;
		Consumer<ModelIdentifier> extraModelConsumer2 = pluginContext::addModels;
		// A bit hacky, but avoids the allocation of a new context wrapper every time.
		ModelProviderContext resourceProviderContext = ((ModelLoaderPluginContextImpl) pluginContext).modelGetter::apply;

		for (ExtraModelProvider provider : modelProviders) {
			provider.provideExtraModels(resourceManager, extraModelConsumer);
		}

		for (ModelAppender appender : modelAppenders) {
			appender.appendAll(resourceManager, extraModelConsumer2);
		}

		for (Function<ResourceManager, ModelResourceProvider> supplier : resourceProviderSuppliers) {
			ModelResourceProvider provider = supplier.apply(resourceManager);

			pluginContext.resolveModel().register(resolverContext -> {
				try {
					return provider.loadModelResource(resolverContext.id(), resourceProviderContext);
				} catch (ModelProviderException e) {
					throw new RuntimeException(e);
				}
			});
		}

		for (Function<ResourceManager, ModelVariantProvider> supplier : variantProviderSuppliers) {
			ModelVariantProvider provider = supplier.apply(resourceManager);
			((ModelLoaderPluginContextImpl) pluginContext).legacyVariantProviders().register(modelId -> {
				try {
					return provider.loadModelVariant(modelId, resourceProviderContext);
				} catch (ModelProviderException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	@Override
	public void registerModelProvider(ExtraModelProvider provider) {
		modelProviders.add(provider);
	}

	@Override
	public void registerAppender(ModelAppender appender) {
		modelAppenders.add(appender);
	}

	@Override
	public void registerResourceProvider(Function<ResourceManager, ModelResourceProvider> providerSupplier) {
		resourceProviderSuppliers.add(providerSupplier);
	}

	@Override
	public void registerVariantProvider(Function<ResourceManager, ModelVariantProvider> providerSupplier) {
		variantProviderSuppliers.add(providerSupplier);
	}
}
