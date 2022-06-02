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

package net.fabricmc.fabric.mixin.message;

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
	@Inject(method = "broadcast(Lnet/minecraft/server/filter/FilteredMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"), cancellable = true)
	private void onSendChatMessage(FilteredMessage<SignedMessage> message, ServerPlayerEntity sender, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
		if (!ServerMessageEvents.ALLOW_CHAT_MESSAGE.invoker().allowChatMessage(message, sender, typeKey)) {
			ci.cancel();
			return;
		}

		ServerMessageEvents.CHAT_MESSAGE.invoker().onChatMessage(message, sender, typeKey);
	}

	@Inject(method = "broadcast(Lnet/minecraft/text/Text;Ljava/util/function/Function;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"), cancellable = true)
	private void onSendGameMessage(Text message, Function<ServerPlayerEntity, Text> playerMessageFactory, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
		if (!ServerMessageEvents.ALLOW_GAME_MESSAGE.invoker().allowGameMessage(message, typeKey)) {
			ci.cancel();
			return;
		}

		ServerMessageEvents.GAME_MESSAGE.invoker().onGameMessage(message, typeKey);
	}

	@Inject(method = "broadcast(Lnet/minecraft/server/filter/FilteredMessage;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/util/registry/RegistryKey;)V", at = @At("HEAD"), cancellable = true)
	private void onSendCommandMessage(FilteredMessage<SignedMessage> message, ServerCommandSource source, RegistryKey<MessageType> typeKey, CallbackInfo ci) {
		if (!ServerMessageEvents.ALLOW_COMMAND_MESSAGE.invoker().allowCommandMessage(message, source, typeKey)) {
			ci.cancel();
			return;
		}

		ServerMessageEvents.COMMAND_MESSAGE.invoker().onCommandMessage(message, source, typeKey);
	}
}
