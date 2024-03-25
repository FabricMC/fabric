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

package net.fabricmc.fabric.impl.lookup.item;

import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiLookupMap;
import net.fabricmc.fabric.api.lookup.v1.custom.ApiProviderMap;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;

public class ItemApiLookupImpl<A, C> implements ItemApiLookup<A, C> {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-api-lookup-api-v1/item");
	private static final ApiLookupMap<ItemApiLookup<?, ?>> LOOKUPS = ApiLookupMap.create(ItemApiLookupImpl::new);

	@SuppressWarnings("unchecked")
	public static <A, C> ItemApiLookup<A, C> get(Identifier lookupId, Class<A> apiClass, Class<C> contextClass) {
		return (ItemApiLookup<A, C>) LOOKUPS.getLookup(lookupId, apiClass, contextClass);
	}

	public static <A, C> Event<ItemApiProvider<A, C>> newEvent() {
		return EventFactory.createArrayBacked(ItemApiProvider.class, providers -> (itemStack, context) -> {
			for (ItemApiProvider<A, C> provider : providers) {
				A api = provider.find(itemStack, context);
				if (api != null) return api;
			}

			return null;
		});
	}
	private final Identifier identifier;
	private final Class<A> apiClass;
	private final Class<C> contextClass;
	private final Event<ItemApiProvider<A, C>> preliminary = newEvent();
	/**
	 * It can't reflect phase order.
	 */
	private final ApiProviderMap<Item, Event<ItemApiProvider<A, C>>> itemSpecific = ApiProviderMap.create();
	/**
	 * It can't reflect phase order.
	 */
	@ApiStatus.Experimental
	private final Multimap<Item, ItemApiProvider<A, C>> itemSpecificProviders = Multimaps.synchronizedMultimap(HashMultimap.create());
	private final Event<ItemApiProvider<A, C>> fallback = newEvent();

	@SuppressWarnings("unchecked")
	private ItemApiLookupImpl(Identifier identifier, Class<?> apiClass, Class<?> contextClass) {
		this.identifier = identifier;
		this.apiClass = (Class<A>) apiClass;
		this.contextClass = (Class<C>) contextClass;
	}

	@Override
	public Identifier getId() {
		return identifier;
	}

	@Override
	public Class<A> apiClass() {
		return apiClass;
	}

	@Override
	public Class<C> contextClass() {
		return contextClass;
	}

	@Override
	@Nullable
	@Deprecated(forRemoval = true)
	public ItemApiProvider<A, C> getProvider(Item item) {
		for (ItemApiProvider<A, C> provider : itemSpecificProviders.get(item)) {
			return provider;
		}

		return null;
	}

	@Override
	public Event<ItemApiProvider<A, C>> preliminary() {
		return preliminary;
	}

	@Override
	public Map<Item, Event<ItemApiProvider<A, C>>> itemSpecific() {
		return itemSpecific.asMap();
	}

	@Override
	public @NotNull Event<ItemApiProvider<A, C>> getSpecificFor(@NotNull Item item) {
		Event<ItemApiProvider<A, C>> event = itemSpecific.get(item);

		if (event == null) {
			event = newEvent();
			itemSpecific.putIfAbsent(item, event);
		}

		return event;
	}

	@Override
	public Event<ItemApiProvider<A, C>> fallback() {
		return fallback;
	}
}
