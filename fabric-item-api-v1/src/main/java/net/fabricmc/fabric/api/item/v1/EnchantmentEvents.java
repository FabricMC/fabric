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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Events relating to enchantments, allowing for finer control of what enchantments can apply to different items.
 */
public class EnchantmentEvents {
	/**
	 * An event that allowed overriding whether an {@link Enchantment} can be applied to an {@link ItemStack}.
	 *
	 * <p>This should only be used to modify the behavior of <i>external</i> items with regards to <i>external</i> enchantments,
	 * where 'external' means either vanilla or from another mod. For instance, a mod might allow enchanting a pickaxe
	 * with Sharpness (and only Sharpness) under certain specific conditions.</p>
	 *
	 * <p>To modify the behavior of your own modded <i>enchantments</i>, use {@link Enchantment#isAcceptableItem(ItemStack)} instead.
	 * To modify the behavior of your own modded <i>items</i>, use {@link FabricItem#canBeEnchantedWith(ItemStack, Enchantment, EnchantingContext)} instead.</p>
	 *
	 * <p>Note that allowing an enchantment using this event does not guarantee the item will receive that enchantment,
	 * only that it isn't forbidden from doing so.</p>
	 *
	 * @see AllowEnchanting#allowEnchanting(Enchantment, ItemStack, EnchantingContext)
	 * @see Enchantment#isAcceptableItem(ItemStack)
	 * @see FabricItem#canBeEnchantedWith(ItemStack, Enchantment, EnchantingContext)
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
}
