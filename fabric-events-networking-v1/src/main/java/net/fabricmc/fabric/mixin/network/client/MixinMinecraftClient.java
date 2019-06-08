package net.fabricmc.fabric.mixin.network.client;

import net.fabricmc.fabric.api.event.network.client.ServerLeaveCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

	@Shadow public abstract ClientPlayNetworkHandler getNetworkHandler();

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at= @At("HEAD"))
	private void onDisconnect(Screen screen_1, CallbackInfo ci) {
		ClientPlayNetworkHandler handler = getNetworkHandler();
		if (handler != null) {
			ServerLeaveCallback.EVENT.invoker().onLeave(handler.getClientConnection());
		}
	}
}
