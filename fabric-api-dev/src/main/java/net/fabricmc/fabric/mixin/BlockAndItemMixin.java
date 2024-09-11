package net.fabricmc.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.fabric.FabricDev;

import net.minecraft.block.Block;

import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Block.class, Item.class})
public class BlockAndItemMixin {
	@Dynamic("@ModifyExpressionValue's the FIELD GET of SharedConstants.isDevelopment to add a OR condition for FabricDev.LOG_CONVENTION_ISSUES")
	@ModifyExpressionValue(method = {
			"<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V",
			"<init>(Lnet/minecraft/item/Item$Settings;)V"
	}, at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private boolean fabric$mevIsDevelopmentForDevModule(boolean original) {
		return original || FabricDev.LOG_CONVENTION_ISSUES;
	}
}
