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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.enchantment.v1.FabricEnchantment;

/**
 * A mixin to handle the verification of FabricEnchantments with custom targets.
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	// This target inside of the nested while loops but right before the call to acceptable item in the jump.
	// This is convenient because it co-opts vanilla's normal registry iterator rather than us iterating over the registry a second time.
	@Inject(method = "getPossibleEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private static void getPossibleEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> callback, List<EnchantmentLevelEntry> list, Item item, boolean bl, Iterator<Enchantment> var6, Enchantment enchantment) {
		// If the enchantment type is null (ie. it would cause a null pointer exception normally) and its null because its a FabricEnchantment
		while (enchantment.type == null && enchantment instanceof FabricEnchantment) {
			// Then check if the item in question is a book or a valid target
			if (bl || enchantment.isAcceptableItem(stack)) {
				// If it is then add all of the enchantment levels just like vanilla does
				// This for loop is basically copied from vanilla, it copies exactly the logic it uses to add the enchantment entries
				for (int i = enchantment.getMaxLevel(); i > enchantment.getMinLevel() - 1; i--) {
					if (power >= enchantment.getMinPower(i) && power <= enchantment.getMaxPower(i)) {
						list.add(new EnchantmentLevelEntry(enchantment, i));
						break;
					}
				}
			}

			// Because the enchantment type is null, and we already handled adding the FabricEnchantment we need to skip to the next vanilla enchant
			// If the iterator has another value this is easy
			if (var6.hasNext()) {
				// We just set the enchantment to the next iterator value
				// This will make the while false and it'll go back to vanilla functionality with a vanilla enchantment with a valid enchantment target
				enchantment = var6.next();
			// If the iterator doesn't have another value this is more tricky
			// Simply ending the mixin without making the enchantment value have a valid target means that a null pointer will be thrown
			} else {
				// So, because we know the iterator is empty we know we can safely return the enchantment entry list without any unintended side effects
				callback.setReturnValue(list);
				break;
			}
		}

		// If this point is reached it means that the enchantment value has been set to a vanilla enchantment from the iterator
		// and that vanilla verification functionality will resume. This also means that vanilla enchantments that are registered
		// which have a null value for their target will successfully throw a null pointer exception as expected.
	}
}
