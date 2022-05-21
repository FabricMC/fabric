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

package net.fabricmc.fabric.mixin.command.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.MessageSender;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

@Mixin(ClientCommandSource.class)
abstract class ClientCommandSourceMixin implements FabricClientCommandSource {
	@Shadow
	@Final
	private MinecraftClient client;

	@Override
	public void sendFeedback(Text message) {
		client.inGameHud.onChatMessage(getSystemMessageType(), message, new MessageSender(Util.NIL_UUID, message));
	}

	@Override
	public void sendError(Text message) {
		client.inGameHud.onChatMessage(getSystemMessageType(), Text.literal("").append(message).formatted(Formatting.RED), new MessageSender(Util.NIL_UUID, message));
	}

	@Unique
	private MessageType getSystemMessageType() {
		Registry<MessageType> registry = client.world.getRegistryManager().get(Registry.MESSAGE_TYPE_KEY);
		return registry.get(MessageType.SYSTEM);
	}

	@Override
	public MinecraftClient getClient() {
		return client;
	}

	@Override
	public ClientPlayerEntity getPlayer() {
		return client.player;
	}

	@Override
	public ClientWorld getWorld() {
		return client.world;
	}
}
