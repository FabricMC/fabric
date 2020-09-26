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

package net.fabricmc.fabric.mixin.enchantment;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.enchantment.v1.FabricEnchantment;

/**
 * A mixin to add enchanted books for custom enchantments to the right item groups.
 */
@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {
	@Shadow
	public static ItemStack forEnchantment(EnchantmentLevelEntry info) {
		throw new AssertionError("Mixin shadow failed: net.fabricmc.fabric.mixin.enchantment.EnchantedBookItemMixin#forEnchantment");
	}

	// This target mixes in right at the end of the append stacks method
	@Inject(method = "appendStacks", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks, CallbackInfo callback) {
		// Get an iterator for the enchantment registry. This mixin iterates over the enchantment
		// registry a second time, which does increase initialization time but because this method
		// is only called once in the initialization processs, and alternative mixins would be more
		// invasive, this is how it is.
		Iterator<Enchantment> iterator = Registry.ENCHANTMENT.iterator();

		// While the iterator has another element (basically for every element in the iterator)
		while (iterator.hasNext()) {
			Enchantment enchantment = iterator.next();

			// If the enchantment is a FabricEnchantment
			if (enchantment instanceof FabricEnchantment) {
				// If the item group is search, add the enchantment regardless of what it is
				if (group == ItemGroup.SEARCH) {
					// Add every level of enchanted book to the search tab (just like vanilla)
					for (int i = enchantment.getMinLevel(); i <= enchantment.getMaxLevel(); i++) {
						stacks.add(forEnchantment(new EnchantmentLevelEntry(enchantment, i)));
					}
				// Otherwise if the item group is an acceptable group for this enchantment
				} else if (((FabricEnchantment) enchantment).isAcceptableItemGroup(group)) {
					// Add the maximum level enchanted book for this enchantment to the item group (just like vanilla)
					stacks.add(forEnchantment(new EnchantmentLevelEntry(enchantment, enchantment.getMaxLevel())));
				}
			}
		}
	}
}
