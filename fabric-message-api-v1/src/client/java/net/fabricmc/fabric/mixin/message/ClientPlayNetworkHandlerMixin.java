package net.fabricmc.fabric.mixin.message;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayNetworkHandler;

import net.fabricmc.fabric.api.message.v1.ClientMessageEvents;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	private void fabric_message_api_onSendChatMessage(String content, CallbackInfo ci) {
		if (!ClientMessageEvents.ALLOW_SEND_CHAT_MESSAGE.invoker().allowSendChatMessage(content)) {
			ci.cancel();
			return;
		}
		ClientMessageEvents.SEND_CHAT_MESSAGE.invoker().onSendChatMessage(content);
	}

	@Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
	private void fabric_message_api_onSendCommandMessage(String command, CallbackInfo ci) {
		if (!ClientMessageEvents.ALLOW_SEND_COMMAND_MESSAGE.invoker().allowSendCommandMessage(command)) {
			ci.cancel();
			return;
		}
		ClientMessageEvents.SEND_COMMAND_MESSAGE.invoker().onSendCommandMessage(command);
	}
}
