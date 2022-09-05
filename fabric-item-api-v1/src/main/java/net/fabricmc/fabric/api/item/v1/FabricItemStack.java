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

/**
 * General-purpose Fabric-provided extensions for {@link ItemStack} subclasses.
 *
 * <p>Note: This interface is automatically implemented on the {@link ItemStack} class via Mixin and interface injection.
 *
 * <p>Note to maintainers: Functions should only be added to this interface if they are general-purpose enough,
 * to be evaluated on a case-by-case basis. Otherwise, they are better suited for more specialized APIs.
 */
public interface FabricItemStack {
	/**
	 * Returns a leftover item stack after this item stack is consumed in a recipe.
	 * (This is also known as a "recipe remainder".)
	 * For example, using a lava bucket in a furnace as fuel will leave an empty bucket.
	 *
	 * <p>This is a stack-aware version of {@link Item#getRecipeRemainder()}.
	 *
	 * @return the leftover item stack
	 */
	default ItemStack getRecipeRemainder() {
		return ((ItemStack) this).getItem().getRecipeRemainder((ItemStack) this);
	}
}
