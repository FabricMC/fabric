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

import java.util.Optional;
import java.util.function.Consumer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;

@Mixin(EnchantmentHelper.class)
abstract class EnchantmentHelperMixin {
	@Unique
	private static final ThreadLocal<ItemStack> CURRENT_STACK = new ThreadLocal<>();

	@Redirect(
			method = "getPossibleEntries",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z")
	)
	private static boolean useCustomEnchantingChecks(EnchantmentTarget target, Item item, int power, ItemStack stack, boolean treasureAllowed, @Local Enchantment enchantment) {
		return stack.canBeEnchantedWith(enchantment, EnchantingContext.RANDOM_ENCHANTMENT);
	}

	@ModifyReturnValue(method = "getLevel", at = @At("RETURN"))
	private static int getIntrinsicLevelIfPresent(int original, Enchantment ench, ItemStack stack) {
		int intrinsicLevel = stack.getItem().getIntrinsicEnchantments(stack).getOrDefault(ench, 0);
		return Math.max(original, intrinsicLevel);
	}

	@WrapOperation(
			method = "forEachEnchantment(Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;Lnet/minecraft/item/ItemStack;)V",
			at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V", remap = false)
	)
	private static void setCurrentStack(
			Optional<Enchantment> instance, Consumer<? super Enchantment> action, Operation<Void> original,
			@Local(argsOnly = true) ItemStack stack // local to avoid unnecessary AW
	) {
		CURRENT_STACK.set(stack);
		original.call(instance, action);
		CURRENT_STACK.remove();
	}

	@ModifyArg(
			method = "method_17883",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper$Consumer;accept(Lnet/minecraft/enchantment/Enchantment;I)V")
	)
	private static int iterateOverIntrinsicEnchantments(Enchantment enchantment, int original) {
		ItemStack stack = CURRENT_STACK.get();
		int intrinsicLevel = stack.getItem().getIntrinsicEnchantments(stack).getOrDefault(enchantment, 0);
		return Math.max(original, intrinsicLevel);
	}
}
