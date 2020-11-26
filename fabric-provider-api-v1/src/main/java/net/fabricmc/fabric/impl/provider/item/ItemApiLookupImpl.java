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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.provider.v1.ApiProviderMap;
import net.fabricmc.fabric.api.provider.v1.item.ItemApiLookup;

final class ItemApiLookupImpl<T, C> implements ItemApiLookup<T, C> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ApiProviderMap<Item, ItemApiProvider<T, C>> providerMap = ApiProviderMap.create();
	private final List<ItemApiProvider<T, C>> fallbackProviders = new CopyOnWriteArrayList<>();

	@Nullable
	@Override
	public T get(ItemStack stack, C context) {
		Objects.requireNonNull(stack, "World cannot be null");
		// Providers have the final say whether a null context is allowed.

		@Nullable
		final ItemApiProvider<T, C> provider = providerMap.get(stack.getItem());

		if (provider != null) {
			T instance = provider.get(stack, context);
			if (instance != null) {
				return instance;
			}
		}

		for (ItemApiProvider<T, C> fallbackProvider : fallbackProviders) {
			T instance = fallbackProvider.get(stack, context);
			if (instance != null) {
				return instance;
			}
		}

		return null;
	}

	@Override
	public void register(ItemApiProvider<T, C> provider, ItemConvertible... items) {
		Objects.requireNonNull(provider, "ItemApiProvider cannot be null");

		for (ItemConvertible item : items) {
			Objects.requireNonNull(item, "Passed item convertible cannot be null");
			Objects.requireNonNull(item.asItem(), "Item convertible in item form cannot be null: " + item.toString());

			if (providerMap.putIfAbsent(item.asItem(), provider) != null) {
				LOGGER.warn("Encountered duplicate API provider registration for item: " + Registry.ITEM.getId(item.asItem()));
			}
		}
	}

	@Override
	public void registerFallback(ItemApiProvider<T, C> provider) {
		Objects.requireNonNull(provider, "ItemApiProvider cannot be null");

		fallbackProviders.add(provider);
	}
}
