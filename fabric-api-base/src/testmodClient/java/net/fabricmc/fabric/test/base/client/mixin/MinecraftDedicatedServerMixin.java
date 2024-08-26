package net.fabricmc.fabric.test.base.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;

import net.fabricmc.fabric.test.base.client.TestDedicatedServer;

@Mixin(MinecraftDedicatedServer.class)
public abstract class MinecraftDedicatedServerMixin {
	@Inject(method = "setupServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/MinecraftDedicatedServer;loadWorld()V"))
	private void captureServerInstance(CallbackInfoReturnable<Boolean> cir) {
		// Capture the server instance once the server is ready to be connected to
		TestDedicatedServer.DEDICATED_SERVER_REF.set((MinecraftDedicatedServer) (Object) this);
	}
}
