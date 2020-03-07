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

package net.fabricmc.fabric.api.enchantment.v1;

import net.minecraft.item.Item;

/**
 * Allows mods to define custom enchantment targets outside of Vanilla's {@link net.minecraft.enchantment.EnchantmentTarget} enum.
 * Enchantment targets restrict what items a certain {@link FabricEnchantment} can be applied to.
 */
public interface FabricEnchantmentTarget {
	/**
	 * Checks whether enchantments with this target can be applied to this item.
	 * @param item the item to be checked
	 * @return <code>true</code> if the enchantment can be applied to this item
	 */
	boolean isAcceptableItem(Item item);
}
