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

package net.fabricmc.fabric.api.enchanting.v1;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Implement this interface on your enchantments if you want the player to use them on an enchanting table.
 * Your enchantment's target will be ignored if you use this interface.
 */
public interface FabricEnchantment {
	/**
	 * Returns whether the player can enchant the given stack with this enchantment in an enchanting table.
	 * Note that the stack must also be {@link ItemStack#isEnchantable()} and the {@link Item#getEnchantability()} must be greater than 0.
	 * The similar sounding method {@link net.minecraft.enchantment.Enchantment#isAcceptableItem(ItemStack)} is used by vanilla to determine whether an is <b>generally</b> allowed to be on a stack.
	 * @param stack The current stack
	 * @return Whether this enchantment will be shown
	 */
	boolean canPlayerEnchant(ItemStack stack);
}
