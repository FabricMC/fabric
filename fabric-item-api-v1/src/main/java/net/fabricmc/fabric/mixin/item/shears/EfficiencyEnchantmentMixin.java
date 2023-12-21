package net.fabricmc.fabric.mixin.item.shears;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.enchantment.EfficiencyEnchantment;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.impl.item.ShearsHelper;

@Mixin(EfficiencyEnchantment.class)
public abstract class EfficiencyEnchantmentMixin {
	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"), method = "isAcceptableItem")
	private boolean isShears(boolean original, @Local ItemStack stack) {
		// allows anything in fabric:shears to be enchanted with efficiency
		return original || ShearsHelper.isShears(stack);
	}
}
