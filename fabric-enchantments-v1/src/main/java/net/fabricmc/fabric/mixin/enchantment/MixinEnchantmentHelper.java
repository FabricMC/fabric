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

import net.fabricmc.fabric.api.enchantment.FabricEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {
	@Unique
	private static Enchantment currentEnchantment;

	@Inject(method = "getHighestApplicableEnchantmentsAtPower", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void nextEnchantment(int level, ItemStack itemStack, boolean allowTreasure, CallbackInfoReturnable<List> callbackInfoReturnable, List enchantmentList, Item item, boolean isBook, Iterator enchantmentIterator, Enchantment enchantment) {
		currentEnchantment = enchantment;
	}

	@Redirect(method = "getHighestApplicableEnchantmentsAtPower", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
	private static boolean isEnchantmentApplicable(EnchantmentTarget enchantmentTarget, Item item, int level, ItemStack itemStack, boolean allowTreasure) {
		if(currentEnchantment instanceof FabricEnchantment)
			return ((FabricEnchantment) currentEnchantment).getEnchantmentTarget().isAcceptableItem(item);
		return enchantmentTarget.isAcceptableItem(item);
	}
}
