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

import net.minecraft.util.registry.Registry;
import org.objectweb.asm.Opcodes;
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

import net.fabricmc.fabric.api.enchantment.v1.FabricEnchantment;

import java.util.Iterator;

/**
 * A mixin to add enchanted books for custom enchantments to the right item groups.
 *
 * @author Vaerian (vaeriann@gmail.com or @Vaerian on GitHub).
 *
 * Please contact the author, Vaerian, at the email or GitHub profile listed above
 * with any questions surrounding implementation choices, functionality, or updating
 * to newer versions of the game.
 */
@Mixin(EnchantedBookItem.class)
public class EnchantedBookItemMixin {
	@Shadow
	public static ItemStack forEnchantment(EnchantmentLevelEntry info) {
		throw new AssertionError("Mixin shadow failed: net.fabricmc.fabric.mixin.enchantment.EnchantedBookItemMixin#forEnchantment");
	}

	// This target mixes in right inside of the enchantment iterator
	@Inject(method = "appendStacks", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks, CallbackInfo callback, Iterator<Enchantment> var3, Enchantment enchantment) {
		System.out.println(group);
		// When the iterator first encounters a FabricEnchantment it'll start this loop until it reaches a normal enchantment
		while (enchantment instanceof FabricEnchantment) {
			// If the FabricEnchantment indicates it should be added to the group
			if (((FabricEnchantment) enchantment).isAcceptableItemGroup(group)) {
				System.out.println("Acceptable");
				// Add it to the group
				stacks.add(forEnchantment(new EnchantmentLevelEntry(enchantment, enchantment.getMaxLevel())));
			}
			// If the iterator has another enchantment in it
			if (var3.hasNext()) {
				// Then set the enchantment value to the next enchantment
				enchantment = var3.next();
				// At this point if it's a fabric enchantment this loop will continue
				// If it's a normal enchantment it'll go back to vanilla functionality
			// Otherwise if there's no other enchantments in the iterator
			} else {
				// We can safely cancel the rest of the method because everything has been handled
				callback.cancel();
				break;
			}
		}
	}

	@Inject(method = "appendStacks", at = @At("TAIL"))
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks, CallbackInfo callback) {
		for (Enchantment enchantment : Registry.ENCHANTMENT) {
			if (enchantment instanceof FabricEnchantment && ((FabricEnchantment) enchantment).isAcceptableItemGroup(group)) {
				stacks.add(forEnchantment(new EnchantmentLevelEntry(enchantment, enchantment.getMaxLevel())));
			}
		}
	}
}
