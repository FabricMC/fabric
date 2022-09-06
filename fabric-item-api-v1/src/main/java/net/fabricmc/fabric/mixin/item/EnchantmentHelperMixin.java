package net.fabricmc.fabric.mixin.item;

import net.minecraft.enchantment.EnchantmentHelper;

import net.minecraft.item.Item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Implements stack-aware item enchantability
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	@Redirect (method = "calculateRequiredExperienceLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getEnchantability()I"))
	private static int fabric$getEnchantability(Item item, Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
		return item.getEnchantability(stack);
	}

	@Redirect (method = "generateEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getEnchantability()I"))
	private static int fabric$getEnchantability(Item item, Random random, ItemStack stack, int level, boolean treasureAllowed) {
		return item.getEnchantability(stack);
	}
}
