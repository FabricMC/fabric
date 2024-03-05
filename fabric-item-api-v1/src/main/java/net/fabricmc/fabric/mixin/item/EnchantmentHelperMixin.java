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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;

@Mixin(EnchantmentHelper.class)
abstract class EnchantmentHelperMixin {
	@Redirect(
			method = "getPossibleEntries",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z")
	)
	private static boolean useCustomEnchantingChecks(Enchantment instance, ItemStack stack) {
		return stack.canBeEnchantedWith(instance, EnchantingContext.RANDOM_ENCHANTMENT);
	}

	@ModifyReturnValue(method = "getLevel", at = @At("RETURN"))
	private static int getIntrinsicLevelIfPresent(int original, Enchantment ench, ItemStack stack) {
		int intrinsicLevel = stack.getItem().getIntrinsicEnchantments(stack).getLevel(ench);
		return Math.max(original, intrinsicLevel);
	}

	// Other injectors require a code reference to EnchantmentHelper$Consumer which would need an AW.
	@ModifyArgs(
			method = "forEachEnchantment(Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;Lnet/minecraft/item/ItemStack;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;accept(Lnet/minecraft/enchantment/Enchantment;I)V")
	)
	private static void iterateOverIntrinsicEnchantments(Args args, @Local(argsOnly = true) ItemStack stack) {
		int intrinsicLevel = stack.getItem().getIntrinsicEnchantments(stack).getLevel(args.get(0));
		args.set(1, Math.max(args.get(1), intrinsicLevel));
	}
}
