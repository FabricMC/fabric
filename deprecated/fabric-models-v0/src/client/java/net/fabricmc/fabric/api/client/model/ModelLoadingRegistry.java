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

package net.fabricmc.fabric.api.client.model;

import java.util.function.Function;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.impl.client.model.ModelLoadingRegistryImpl;

/**
 * @deprecated Register a {@link ModelLoadingPlugin} instead.
 */
@Deprecated
public interface ModelLoadingRegistry {
	ModelLoadingRegistry INSTANCE = new ModelLoadingRegistryImpl();

	/**
	 * Register a model provider, which can request loading additional models.
	 * @see ExtraModelProvider
	 */
	void registerModelProvider(ExtraModelProvider appender);

	/**
	 * Register a model appender, which can request loading additional models.
	 * @deprecated Use {@link #registerModelProvider(ExtraModelProvider)} instead, which supports loading of plain {@link Identifier}s
	 */
	@Deprecated
	void registerAppender(ModelAppender appender);

	/**
	 * Register a ModelResourceProvider supplier.
	 *
	 * @param providerSupplier The ModelResourceProvider supplier, instantiated with every ModelLoader.
	 */
	void registerResourceProvider(Function<ResourceManager, ModelResourceProvider> providerSupplier);

	/**
	 * Register a ModelVariantProvider supplier.
	 *
	 * @param providerSupplier The ModelVariantProvider supplier, instantiated with every ModelLoader.
	 */
	void registerVariantProvider(Function<ResourceManager, ModelVariantProvider> providerSupplier);
}
