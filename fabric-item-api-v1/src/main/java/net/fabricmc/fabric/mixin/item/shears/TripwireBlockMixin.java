package net.fabricmc.fabric.mixin.item.shears;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;

import net.minecraft.block.TripwireBlock;
import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TripwireBlock.class)
public abstract class TripwireBlockMixin {
	@ModifyExpressionValue(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"), method = "onBreak")
	private boolean isShears(boolean original, @Local PlayerEntity player) {
		// allows anything in fabric:shears to silently break tripwire (string)
		return original || player.getMainHandStack().isIn(ConventionalItemTags.SHEARS);
	}
}
