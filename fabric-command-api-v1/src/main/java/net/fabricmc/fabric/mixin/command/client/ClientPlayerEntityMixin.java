package net.fabricmc.fabric.mixin.command.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayerEntity;

import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;

@Mixin(ClientPlayerEntity.class)
abstract class ClientPlayerEntityMixin {
	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	private void onSendChatMessage(String message, CallbackInfo info) {
		if (ClientCommandInternals.executeCommand(message)) {
			info.cancel();
		}
	}
}
