package net.fabricmc.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.fabric.FabricDev;

import net.minecraft.server.dedicated.EulaReader;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EulaReader.class)
public class EulaReaderMixin {
	@Dynamic("@ModifyExpressionValue's the FIELD GET of SharedConstants.isDevelopment to add a OR condition for FabricDev.ALWAYS_AGREE_TO_EULA")
	@ModifyExpressionValue(method = {"<init>", "createEulaFile"}, at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private boolean fabric$mevIsDevelopmentForDevModule(boolean original) {
		return original || FabricDev.ALWAYS_AGREE_TO_EULA;
	}
}
