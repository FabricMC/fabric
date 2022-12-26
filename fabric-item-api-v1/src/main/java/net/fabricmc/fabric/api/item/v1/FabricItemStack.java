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

/*
 * Fabric-provided extensions for {@link ItemStack}.
 * This interface is automatically implemented on all item stacks via Mixin and interface injection.
 */
public interface FabricItemStack {
	/**
	 * Return a leftover item for use in recipes.
	 *
	 * <p>See {@link FabricItem#getRecipeRemainder(ItemStack)} for a more in depth description.
	 *
	 * <p>Stack-aware version of {@link Item#getRecipeRemainder()}.
	 *
	 * @return the leftover item
	 */
	default ItemStack getRecipeRemainder() {
		return ((ItemStack) this).getItem().getRecipeRemainder((ItemStack) this);
	}
}
