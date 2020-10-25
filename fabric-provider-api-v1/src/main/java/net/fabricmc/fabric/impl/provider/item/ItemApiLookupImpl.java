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

package net.fabricmc.fabric.impl.provider.item;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.provider.v1.ApiProviderMap;
import net.fabricmc.fabric.api.provider.v1.ContextKey;
import net.fabricmc.fabric.api.provider.v1.item.ItemApiLookup;

final class ItemApiLookupImpl<T, C> implements ItemApiLookup<T, C> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ApiProviderMap<Item, ItemApiProvider<?, ?>> providerMap = ApiProviderMap.create();
	private final Identifier id;
	private final ContextKey<C> contextKey;

	ItemApiLookupImpl(Identifier apiId, ContextKey<C> contextKey) {
		this.id = apiId;
		this.contextKey = contextKey;
	}

	@Nullable
	@Override
	public T get(ItemStack stack, C context) {
		Objects.requireNonNull(stack, "World cannot be null");
		// Providers have the final say whether a null context is allowed.

		@SuppressWarnings("unchecked")
		@Nullable
		final ItemApiProvider<T, C> provider = (ItemApiProvider<T, C>) providerMap.get(stack.getItem());

		if (provider != null) {
			return provider.get(stack, context);
		}

		return null;
	}

	@Override
	public void register(ItemApiProvider<T, C> provider, ItemConvertible... items) {
		Objects.requireNonNull(provider);

		for (ItemConvertible item : items) {
			Objects.requireNonNull(item, "Passed item convertible cannot be null");
			Objects.requireNonNull(item.asItem(), "Item convertible in item form cannot be null: " + item.toString());

			if (providerMap.putIfAbsent(item.asItem(), provider) != null) {
				LOGGER.warn("Encountered duplicate API provider registration for item: " + Registry.ITEM.getId(item.asItem()));
			}
		}
	}

	@Override
	public Identifier getApiId() {
		return this.id;
	}

	@Override
	public ContextKey<C> getContextKey() {
		return this.contextKey;
	}
}
