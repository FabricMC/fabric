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

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.lookup.v1.ApiLookupMap;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;

public class ItemApiLookupRegistryImpl {
	private static final ApiLookupMap<ItemApiLookup<?, ?>> PROVIDERS = ApiLookupMap.create(ItemApiLookupImpl::new);

	@SuppressWarnings("unchecked")
	public static <T, C> ItemApiLookup<T, C> getLookup(Identifier lookupId, Class<T> apiClass, Class<C> contextClass) {
		return (ItemApiLookup<T, C>) PROVIDERS.getLookup(lookupId, apiClass, contextClass);
	}

	private ItemApiLookupRegistryImpl() {
	}
}
