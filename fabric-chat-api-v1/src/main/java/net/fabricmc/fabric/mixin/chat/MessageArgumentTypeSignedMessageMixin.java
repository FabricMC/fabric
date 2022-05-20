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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.Message;

import net.fabricmc.fabric.impl.chat.PreviewCacheAccess;

@Mixin(MessageArgumentType.class_7515.class)
public class MessageArgumentTypeSignedMessageMixin {
	@Inject(method = "method_44263", at = @At("RETURN"))
	private void clearCachedPreviewAfterUse(ServerCommandSource source, Message<SignedChatMessage> message, CallbackInfoReturnable<Message<SignedChatMessage>> cir) {
		// Note: there is only one previewable argument in vanilla.
		// See also: ServerPlayNetworkHandler#method_44156
		if (source.getPlayer() != null) {
			((PreviewCacheAccess) source.getPlayer()).fabric_setPreview(null, null);
		}
	}
}
