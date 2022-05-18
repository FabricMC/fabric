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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.impl.chat.PreviewCacheAccess;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	public ServerPlayerEntity player;

	@Inject(method = "handleMessage", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/class_7492;decorate(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/network/encryption/ChatMessageSignature;Z)Lnet/minecraft/network/encryption/SignedChatMessage;"))
	private void clearCachedPreviewAfterUse(ChatMessageC2SPacket packet, TextStream.Message message, CallbackInfo ci) {
		((PreviewCacheAccess) this.player).fabric_setPreview(null, null);
	}
}
