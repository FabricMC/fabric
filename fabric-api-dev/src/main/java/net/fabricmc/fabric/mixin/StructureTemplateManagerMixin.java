package net.fabricmc.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.fabric.FabricDev;

import net.minecraft.structure.StructureTemplateManager;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureTemplateManager.class)
public class StructureTemplateManagerMixin {
	@Dynamic("@ModifyExpressionValue's the FIELD GET of SharedConstants.isDevelopment to add a OR condition for FabricDev.ENABLE_LOADING_STRUCTURES_FROM_GAMETESTS")
	@ModifyExpressionValue(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private static boolean fabric$mevIsDevelopmentForDevModule(boolean original) {
		return original || FabricDev.ENABLE_LOADING_STRUCTURES_FROM_GAMETESTS;
	}
}
