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

package net.fabricmc.fabric.mixin.chat;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.MessageType;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.Message;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.chat.v1.ServerChatEvents;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Inject(method = "broadcast(Lnet/minecraft/server/filter/Message;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"))
	private void onSendChatMessage(Message<SignedChatMessage> message, ServerPlayerEntity sender, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
		ServerChatEvents.SEND_CHAT_MESSAGE.invoker().onSendChatMessage(message, sender, typeKey);
	}

	@Inject(method = "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"))
	private void onSendGameMessage(Text message, Function<ServerPlayerEntity, Text> playerMessageFactory, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
		ServerChatEvents.SEND_GAME_MESSAGE.invoker().onSendGameMessage(message, typeKey);
	}

	@Inject(method = "method_44166", at = @At("HEAD"))
	private void onSendCommandMessage(Message<SignedChatMessage> message, ServerCommandSource source, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
		ServerChatEvents.SEND_COMMAND_MESSAGE.invoker().onSendCommandMessage(message, source, typeKey);
	}
}
