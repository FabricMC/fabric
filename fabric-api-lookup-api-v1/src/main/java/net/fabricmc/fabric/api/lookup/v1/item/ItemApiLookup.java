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

package net.fabricmc.fabric.api.lookup.v1.item;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;
import net.minecraft.item.ItemConvertible;

import net.fabricmc.fabric.impl.lookup.item.ItemApiLookupImpl;

@ApiStatus.NonExtendable
public interface ItemApiLookup<A, C> {
	static <A, C> ItemApiLookup<A, C> get(Identifier lookupId, Class<A> apiClass, Class<C> contextClass) {
		return ItemApiLookupImpl.get(lookupId, apiClass, contextClass);
	}

	@Nullable
	A find(ItemKey itemKey, C context);

	void registerSelf(ItemConvertible... items);

	void registerForItems(ItemApiProvider<A, C> provider, ItemConvertible... items);

	void registerFallback(ItemApiProvider<A, C> provider);

	@FunctionalInterface
	interface ItemApiProvider<T, C> {
		@Nullable
		T find(ItemKey itemKey, C context);
	}
}
