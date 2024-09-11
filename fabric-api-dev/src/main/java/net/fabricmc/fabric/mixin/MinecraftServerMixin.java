package net.fabricmc.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.fabric.FabricDev;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@Dynamic("@ModifyExpressionValue's the FIELD GET of SharedConstants.isDevelopment to add a OR condition for FabricDev.TICK_TEST_MANAGER")
	@ModifyExpressionValue(method = "tickWorlds", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private static boolean fabric$mevIsDevelopmentForDevModule(boolean original) {
		return original || FabricDev.TICK_TEST_MANAGER;
	}
}
