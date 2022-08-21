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

package net.fabricmc.fabric.api.item.v1;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface FabricItemStack {
	/**
	 * Determines if the item will have a leftover item after it's been used.
	 * Example: Using lava in a furnace as fuel,
	 * Stack-aware version of {@link Item#hasRecipeRemainder()}.
	 *
	 * @return true if the item has a recipe remainder
	 */
	default boolean hasRecipeRemainder() {
		return ((ItemStack) this).getItem().hasRecipeRemainder((ItemStack) this);
	}

	/**
	 * Return a leftover item for use in recipes
	 * Stack-aware version of {@link Item#getRecipeRemainder()}.
	 *
	 * @return the leftover item
	 */
	default ItemStack getRecipeRemainder() {
		if (!hasRecipeRemainder()) {
			return ItemStack.EMPTY;
		}

		return ((ItemStack) this).getItem().getRecipeRemainder((ItemStack) this);
	}
}
