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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayNetworkHandler;

import net.fabricmc.fabric.api.client.message.v1.ClientMessageEvents;

/**
 * Mixin to {@link ClientPlayNetworkHandler} to listen for sending messages and commands.
 * Priority set to 800 to inject before {@code fabric-command-api} so that this api will be called first.
 */
@Mixin(value = ClientPlayNetworkHandler.class, priority = 800)
public abstract class ClientPlayNetworkHandlerMixin {
	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	private void fabric_onSendChatMessage(String content, CallbackInfo ci) {
		if (!ClientMessageEvents.ALLOW_SEND_CHAT_MESSAGE.invoker().allowSendChatMessage(content)) {
			ci.cancel();
			return;
		}

		ClientMessageEvents.SEND_CHAT_MESSAGE.invoker().onSendChatMessage(content);
	}

	@Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
	private void fabric_onSendCommandMessage(String command, CallbackInfo ci) {
		if (!ClientMessageEvents.ALLOW_SEND_COMMAND_MESSAGE.invoker().allowSendCommandMessage(command)) {
			ci.cancel();
			return;
		}

		ClientMessageEvents.SEND_COMMAND_MESSAGE.invoker().onSendCommandMessage(command);
	}
}
