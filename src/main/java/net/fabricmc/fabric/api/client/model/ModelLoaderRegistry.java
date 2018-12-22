/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

import net.fabricmc.fabric.impl.client.model.ModelLoaderRegistryImpl;
import net.minecraft.resource.ResourceManager;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ModelLoaderRegistry {
	ModelLoaderRegistry INSTANCE = ModelLoaderRegistryImpl.INSTANCE;

	/**
	 * Register a ModelRequester provider.
	 *
	 * @param requesterSupplier The ModelRequester supplier, instantiated with every ModelLoader.
	 */
	void registerRequester(Supplier<ModelRequester> requesterSupplier);

	/**
	 * Register a CustomModelLoader provider.
	 *
	 * @param loaderSupplier The CustomModelLoader supplier, instantiated with every ModelLoader.
	 */
	void registerLoader(Function<ResourceManager, CustomModelLoader> loaderSupplier);
}
