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

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.random.Random;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events relating to enchantments, allowing for finer control of what enchantments can apply to different items.
 */
public class EnchantmentEvents {
	/**
	 * An event that checks whether an {@link Enchantment} can be applied to an {@link ItemStack}, and can override the
	 *
	 * <p>This should <i>only</i> be used for <b>vanilla enchantments</b> if the vanilla tag-based system is not sufficient,
	 * for example if an item should be able to be enchanted with Respiration, but not other helmet enchantments. For modded
	 * enchantments, override {@link Enchantment#isAcceptableItem(ItemStack)} instead.</p>
	 *
	 * <p>Note that allowing an enchantment using this event does not guarantee the item will receive that enchantment,
	 * only that it isn't forbidden from doing so.</p>
	 *
	 * @see AllowEnchanting#allowEnchanting(Enchantment, ItemStack, EnchantingContext)
	 */
	public static final Event<AllowEnchanting> ALLOW_ENCHANTING = EventFactory.createArrayBacked(
			AllowEnchanting.class,
			callbacks -> (enchantment, target, context) -> {
				for (AllowEnchanting callback : callbacks) {
					ActionResult result = callback.allowEnchanting(enchantment, target, context);

					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			}
	);

	@FunctionalInterface
	public interface AllowEnchanting {
		/**
		 * Checks whether an {@link Enchantment} should be applied to a given {@link ItemStack}.
		 *
		 * @param enchantment the enchantment that may be applied
		 * @param target the target item
		 * @param enchantingContext the enchanting context in which this check is made
		 * @return {@link ActionResult#SUCCESS} if the enchantment may be applied, {@link ActionResult#FAIL} if it
		 * may not, {@link ActionResult#PASS} to fall back to other callbacks/vanilla behavior
		 * @see EnchantingContext
		 */
		ActionResult allowEnchanting(
				Enchantment enchantment,
				ItemStack target,
				EnchantingContext enchantingContext
		);
	}

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
}
