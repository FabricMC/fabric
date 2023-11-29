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

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.network.message.SignedMessage;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
	@Inject(method = "sendChatMessage", at = @At(value = "HEAD"), cancellable = true)
	public void sendChatMessage(SentMessage message, boolean filterMaskEnabled, MessageType.Parameters params, CallbackInfo ci) {
		if (message instanceof SentMessage.Profileless) return;

		SignedMessage signedMessage = ((SentMessageChatAccessor) message).getMessage();
		UUID sender = signedMessage.signedHeader().sender();
		if (!ServerMessageEvents.ALLOW_MESSAGE_TO_PLAYER.invoker().allowMessageToPlayer(signedMessage, sender, ((ServerPlayerEntity) (Object) this), params)) ci.cancel();
	}
}
