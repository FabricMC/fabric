package net.fabricmc.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.fabric.FabricDev;

import net.minecraft.util.collection.Weight;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Weight.class)
public class WeightMixin {
	@Dynamic("@ModifyExpressionValue's the FIELD GET of SharedConstants.isDevelopment to add a OR condition for FabricDev.ZERO_WEIGHT_WARNING")
	@ModifyExpressionValue(method = "validate", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private static boolean fabric$mevIsDevelopmentForDevModule(boolean original) {
		return original || FabricDev.ZERO_WEIGHT_WARNING;
	}
}
