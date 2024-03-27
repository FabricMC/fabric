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

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;

/*
 * There is one context for each vanilla call to Enchantment#isAcceptableItem. The reason why RANDOM_ENCHANTMENT
 * feels like a kitchen sink is because it corresponds to the one in EnchantmentHelper, which is shared across multiple
 * uses.
 *
 * This also gets in the way of adding further context (nullable Player and BlockPos have been suggested
 * in the past). It's not impossible to do so, but a probably a bit more brittle.
 */
/**
 * An enum that describes the various contexts in which the game checks whether an enchantment can be applied to an item.
 */
public enum EnchantingContext {
	/**
	 * When generating a random enchantment for the item. This includes the enchanting table, random
	 * mob equipment, and the {@code enchant_with_levels} loot function.
	 *
	 * @see EnchantmentHelper#generateEnchantments(Random, ItemStack, int, boolean)
	 */
	RANDOM_ENCHANTMENT,
	/**
	 * When trying to apply an enchantment in an anvil.
	 */
	ANVIL,
	/**
	 * When using the {@code /enchant} command.
	 */
	ENCHANT_COMMAND,
	/**
	 * When randomly enchanting an item using the {@code enchant_randomly} loot function without a list of enchantments
	 * to choose from.
	 */
	LOOT_RANDOM_ENCHANTMENT
}
