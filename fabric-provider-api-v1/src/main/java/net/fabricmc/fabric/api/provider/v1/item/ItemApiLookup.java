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

package net.fabricmc.fabric.api.provider.v1.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

public interface ItemApiLookup<T, C> {
	@Nullable
	T get(ItemStack stack, C context);

	void register(ItemApiProvider<T, C> provider, ItemConvertible... items);

	void registerFallback(ItemApiProvider<T, C> provider);

	@FunctionalInterface
	interface ItemApiProvider<T, C> {
		@Nullable
		T get(ItemStack stack, C context);
	}
}
