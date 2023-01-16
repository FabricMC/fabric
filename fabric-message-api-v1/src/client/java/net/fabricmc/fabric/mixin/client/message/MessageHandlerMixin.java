/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.client.message;

import java.time.Instant;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "processChatMessageInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getChatHud()Lnet/minecraft/client/gui/hud/ChatHud;", ordinal = 0), cancellable = true)
	private void fabric_allowSignedChatMessage(MessageType.Parameters params, SignedMessage message, Text decorated, GameProfile sender, boolean onlyShowSecureChat, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
		fabric_allowChatMessage(decorated, message, sender, params, receptionTimestamp, cir);
	}

	@Inject(method = "processChatMessageInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getChatHud()Lnet/minecraft/client/gui/hud/ChatHud;", ordinal = 1), cancellable = true)
	private void fabric_allowFilteredSignedChatMessage(MessageType.Parameters params, SignedMessage message, Text decorated, GameProfile sender, boolean onlyShowSecureChat, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
		Text filtered = message.filterMask().getFilteredText(message.getSignedContent());

		if (filtered != null) {
			fabric_allowChatMessage(params.applyChatDecoration(filtered), message, sender, params, receptionTimestamp, cir);
		}
	}

	/**
	 * A {@code null} {@link MessageIndicator} means the message is secure,
	 * according to {@link net.minecraft.client.network.message.MessageTrustStatus#createIndicator(SignedMessage)
	 * MessageTrustStatus.createIndicator(SignedMessage)}.
	 * The {@link MessageIndicator} is only modified with {@link MessageIndicator#modified(String)}
	 * when the message is modified by a listener registered to {@link ClientReceiveMessageEvents#MODIFY_CHAT}
	 * and the message is marked as secure.
	 */
	@Redirect(method = "processChatMessageInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V"))
	private void fabric_modifyAndNarrateSignedChatMessage(ChatHud chatHud, Text decorated, MessageSignatureData messageSignatureData, MessageIndicator messageIndicator, MessageType.Parameters params, SignedMessage message, Text decorated1, GameProfile sender, boolean onlyShowSecureChat, Instant receptionTimestamp) {
		Text modified = fabric_modifyChatMessage(decorated, message, sender, params, receptionTimestamp);
		chatHud.addMessage(modified, messageSignatureData, modified != decorated && messageIndicator == null ? MessageIndicator.modified(message.getSignedContent()) : messageIndicator);
		fabric_narrateDecorated(modified);
	}

	@Inject(method = "method_45745", at = @At("HEAD"), cancellable = true)
	private void fabric_allowProfilelessChatMessage(MessageType.Parameters params, Text content, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
		fabric_allowChatMessage(params.applyChatDecoration(content), null, null, params, receptionTimestamp, cir);
	}

	@Redirect(method = "method_45745", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V"))
	private void fabric_modifyAndNarrateProfilelessChatMessage(ChatHud chatHud, Text decorated, MessageType.Parameters params, Text message, Instant receptionTimestamp) {
		Text modified = fabric_modifyChatMessage(decorated, null, null, params, receptionTimestamp);
		chatHud.addMessage(modified);
		fabric_narrateDecorated(modified);
	}

	@Redirect(method = {"processChatMessageInternal", "method_45745"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;narrate(Lnet/minecraft/network/message/MessageType$Parameters;Lnet/minecraft/text/Text;)V"))
	private void fabric_cancelNarrate(MessageHandler messageHandler, MessageType.Parameters params, Text text) {
	}

	@Unique
	private void fabric_allowChatMessage(Text message, @Nullable SignedMessage signedMessage, @Nullable GameProfile sender, MessageType.Parameters params, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
		if (!ClientReceiveMessageEvents.ALLOW_CHAT.invoker().allowReceiveChatMessage(message, signedMessage, sender, params, receptionTimestamp)) {
			ClientReceiveMessageEvents.CHAT_CANCELED.invoker().onReceiveChatMessageCanceled(message, signedMessage, sender, params, receptionTimestamp);
			cir.setReturnValue(false);
		}
	}

	@Unique
	private Text fabric_modifyChatMessage(Text message, @Nullable SignedMessage signedMessage, @Nullable GameProfile sender, MessageType.Parameters params, Instant receptionTimestamp) {
		return ClientReceiveMessageEvents.MODIFY_CHAT.invoker().modifyReceivedChatMessage(message, signedMessage, sender, params, receptionTimestamp);
	}

	@Unique
	private void fabric_narrateDecorated(Text decorated) {
		client.getNarratorManager().narrateChatMessage(decorated);
	}

	@Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
	private void fabric_allowGameMessage(Text message, boolean overlay, CallbackInfo ci) {
		if (!ClientReceiveMessageEvents.ALLOW_GAME.invoker().allowReceiveGameMessage(message, overlay)) {
			ClientReceiveMessageEvents.GAME_CANCELED.invoker().onReceiveGameMessageCanceled(message, overlay);
			ci.cancel();
		}
	}

	@ModifyVariable(method = "onGameMessage", at = @At(value = "LOAD", ordinal = 0), ordinal = 0, argsOnly = true)
	private Text fabric_modifyGameMessage(Text message, Text message1, boolean overlay) {
		return ClientReceiveMessageEvents.MODIFY_GAME.invoker().modifyReceivedGameMessage(message, overlay);
	}
}
