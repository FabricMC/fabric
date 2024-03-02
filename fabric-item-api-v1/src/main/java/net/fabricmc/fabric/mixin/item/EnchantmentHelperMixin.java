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

package net.fabricmc.fabric.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	@WrapOperation(
			method = "getPossibleEntries",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z")
	)
	private static boolean callAllowEnchantingEvent(Enchantment instance, ItemStack stack, Operation<Boolean> original) {
		ActionResult result = EnchantmentEvents.ALLOW_ENCHANTING.invoker().allowEnchanting(
				instance,
				stack,
				EnchantmentEvents.EnchantingContext.RANDOM_ENCHANTMENT
		);
		return result == ActionResult.PASS ? original.call(instance, stack) : result.isAccepted();
	}
}
