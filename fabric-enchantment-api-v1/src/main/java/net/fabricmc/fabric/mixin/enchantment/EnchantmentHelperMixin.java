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
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.enchantment.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.enchantment.v1.FabricEnchantment;
import net.fabricmc.fabric.api.util.TriState;

/**
 * A mixin to handle the verification of FabricEnchantments with custom targets.
 *
 * @author Vaerian (vaeriann@gmail.com or @Vaerian on GitHub).
 *
 * <p>Please contact the author, Vaerian, at the email or GitHub profile listed above
 * with any questions surrounding implementation choices, functionality, or updating
 * to newer versions of the game.</p>
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	@Unique
	private static Enchantment currentEnchantment;

	// A simple read/write lock to prevent a data race by ensure that the
	// value captured from the inject is not modified by the time it gets to the redirect mixin.
	@Unique
	private static Lock lock = new ReentrantLock();

	@Inject(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void getEnchantment(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> callback, List<Enchantment> enchantmentList, Item item, boolean isBook, Iterator<Enchantment> enchantmentIterator, Enchantment enchantment) {
		// Start a loop
		while (true) {
			// Check if you have the lock, if you don't then continue the loop
			if (lock.tryLock()) {
				// If you have the lock set the enchantment
				currentEnchantment = enchantment;
				// Break out of the check loop because we know we have the lock now
				break;
			}
		}
	}

	@Redirect(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
	private static boolean isAcceptableItem(EnchantmentTarget enchantmentTarget, Item item, int power, ItemStack stack, boolean treasureAllowed) {
		// The only way this code is reached is if we know we have the lock because otherwise
		// the above mixin will block until we get the lock.
		// Do all of the reading logic we need to do and unlock the lock before each possible return.
		// Ask the enchantment event if it would like to override the logic.
		TriState callback = EnchantmentEvents.ACCEPT_ENCHANTMENT.invoker().shouldAccept(currentEnchantment, stack);

		// If it simply delegates to default
		if (callback == TriState.DEFAULT) {
			// Make a variable for what the result will be
			boolean val;

			// If the enchantment is a fabric enchantment call our custom method
			if (currentEnchantment instanceof FabricEnchantment) {
				val = ((FabricEnchantment) currentEnchantment).isEnchantableItem(stack);
			// If the enchantment is a vanilla enchantment call the vanilla method
			} else {
				val = currentEnchantment.type.isAcceptableItem(item);
			}

			// Unlock the lock because we're done using the enchantment value
			lock.unlock();
			// Return the value from the default enchantment logic
			return val;
		// Otherwise, if the event does want to override the logic
		} else {
			// Unlock the lock
			lock.unlock();
			// And simply return the value from the callback
			return callback.get();
		}
	}
}
