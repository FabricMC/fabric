package net.fabricmc.fabric.mixin.message;

import net.fabricmc.fabric.api.message.v1.MessageChannels;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
	@Inject(method = "sendChatMessage", at = @At(value = "HEAD"), cancellable = true)
	public void sendChatMessage(SentMessage message, boolean filterMaskEnabled, MessageType.Parameters params, CallbackInfo ci) {
		if (!MessageChannels.isInSameChannel(((ServerPlayerEntity)(Object)this).getUuid(), ((SentMessageAccessor) message).getMessage().createMetadata().sender())) ci.cancel();
	}
}
