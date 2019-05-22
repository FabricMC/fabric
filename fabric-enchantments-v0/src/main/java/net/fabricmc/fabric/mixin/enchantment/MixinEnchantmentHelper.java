package net.fabricmc.fabric.mixin.enchantment;

import net.fabricmc.fabric.api.enchantment.FabricEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {
	private static Enchantment fabric_currentEnchantment;

	@Inject(method = "getHighestApplicableEnchantmentsAtPower", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void nextEnchantment(int level, ItemStack itemStack, boolean allowTreasure, CallbackInfoReturnable<List> callbackInfoReturnable, List enchantmentList, Item item, boolean isBook, Iterator enchantmentIterator, Enchantment enchantment) {
		fabric_currentEnchantment = enchantment;
	}

	@Redirect(method = "getHighestApplicableEnchantmentsAtPower", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"))
	private static boolean isEnchantmentApplicable(EnchantmentTarget enchantmentTarget, Item item, int level, ItemStack itemStack, boolean allowTreasure) {
		if(fabric_currentEnchantment instanceof FabricEnchantment)
			return ((FabricEnchantment) fabric_currentEnchantment).getEnchantmentTarget().isAcceptableItem(item);
		return enchantmentTarget.isAcceptableItem(item);
	}
}
