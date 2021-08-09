package net.fabricmc.fabric.mixin.gametest;

import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.test.TestManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

	@Inject(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;updatePlayerLatency()V", shift = At.Shift.AFTER))
	private void tickWorlds(BooleanSupplier shouldKeepTicking, CallbackInfo callbackInfo) {
		// Will be invoked by vanilla when enabled
		if (!SharedConstants.isDevelopment) {
			TestManager.INSTANCE.tick();
		}
	}
}
