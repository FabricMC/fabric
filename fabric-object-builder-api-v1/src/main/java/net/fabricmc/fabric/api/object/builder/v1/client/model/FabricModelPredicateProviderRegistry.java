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

package net.fabricmc.fabric.api.object.builder.v1.client.model;

import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.mixin.object.builder.ModelPredicateProviderRegistryAccessor;
import net.fabricmc.fabric.mixin.object.builder.ModelPredicateProviderRegistrySpecificAccessor;

/**
 * Allows registering model predicate providers for item models.
 *
 * <p>A registered model predicate providers for an item can be retrieved through
 * {@link net.minecraft.client.item.ModelPredicateProviderRegistry#get(Item, Identifier)}.</p>
 *
 * @see net.minecraft.client.item.ModelPredicateProviderRegistry
 */
public final class FabricModelPredicateProviderRegistry {
	/**
	 * Registers a model predicate provider that is applicable for any item.
	 *
	 * @param id       the identifier of the provider
	 * @param provider the provider
	 */
	public static void register(Identifier id, ModelPredicateProvider provider) {
		ModelPredicateProviderRegistryAccessor.callRegister(id, provider);
	}

	/**
	 * Registers a model predicate provider to a specific item.
	 *
	 * @param item     the item the provider is associated to
	 * @param id       the identifier of the provider
	 * @param provider the provider
	 */
	public static void register(Item item, Identifier id, ModelPredicateProvider provider) {
		ModelPredicateProviderRegistrySpecificAccessor.callRegister(item, id, provider);
	}
}
