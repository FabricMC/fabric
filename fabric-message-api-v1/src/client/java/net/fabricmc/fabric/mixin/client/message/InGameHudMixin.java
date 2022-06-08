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

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;

import net.fabricmc.fabric.api.client.message.v1.ClientMessageEvents;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Shadow
	@Final
	@Mutable
	private List<ClientChatListener> listeners;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void addCustomChatListener(MinecraftClient client, ItemRenderer itemRenderer, CallbackInfo ci) {
		List<ClientChatListener> listeners = new ArrayList<>(this.listeners);
		listeners.add((type, message, sender) -> {
			if (sender == null) {
				ClientMessageEvents.GAME_MESSAGE.invoker().onGameMessage(message, type);
			} else {
				ClientMessageEvents.CHAT_MESSAGE.invoker().onChatMessage(message, sender, type);
			}
		});
		this.listeners = listeners;
	}
}
