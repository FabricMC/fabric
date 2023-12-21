package net.fabricmc.fabric.mixin.item.shears;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.impl.item.ShearsHelper;

@Mixin({PumpkinBlock.class, BeehiveBlock.class})
public abstract class BlockInteractShearsMixin {
	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"), method = "onUse")
	private boolean isShears(boolean original, @Local ItemStack stack) {
		// allows anything in fabric:shears to shear pumpkins and beehives
		return original || ShearsHelper.isShears(stack);
	}
}
