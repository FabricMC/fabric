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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayNetworkHandler;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

/**
 * Mixin to {@link ClientPlayNetworkHandler} to listen for sending messages and commands.
 * Priority set to 800 to inject before {@code fabric-command-api} so that this api will be called first.
 */
@Mixin(value = ClientPlayNetworkHandler.class, priority = 800)
public abstract class ClientPlayNetworkHandlerMixin {
	@Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
	private void fabric_allowSendChatMessage(String content, CallbackInfo ci) {
		if (!ClientSendMessageEvents.ALLOW_CHAT.invoker().allowSendChatMessage(content)) {
			ClientSendMessageEvents.CHAT_CANCELED.invoker().onSendChatMessageCanceled(content);
			ci.cancel();
		}
	}

	@ModifyVariable(method = "sendChatMessage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private String fabric_modifySendChatMessage(String content) {
		content = ClientSendMessageEvents.MODIFY_CHAT.invoker().modifySendChatMessage(content);
		ClientSendMessageEvents.CHAT.invoker().onSendChatMessage(content);
		return content;
	}

	@Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
	private void fabric_allowSendCommandMessage(String command, CallbackInfo ci) {
		if (!ClientSendMessageEvents.ALLOW_COMMAND.invoker().allowSendCommandMessage(command)) {
			ClientSendMessageEvents.COMMAND_CANCELED.invoker().onSendCommandMessageCanceled(command);
			ci.cancel();
		}
	}

	@ModifyVariable(method = "sendChatCommand", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private String fabric_modifySendCommandMessage(String command) {
		command = ClientSendMessageEvents.MODIFY_COMMAND.invoker().modifySendCommandMessage(command);
		ClientSendMessageEvents.COMMAND.invoker().onSendCommandMessage(command);
		return command;
	}
}
