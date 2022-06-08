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

package net.fabricmc.fabric.api.client.message.v1;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.network.message.MessageSender;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.mixin.client.message.ClientWorldAccessor;

/**
 * Clientside helper methods for use with {@link ClientMessageEvents}.
 */
@Environment(EnvType.CLIENT)
public final class ClientMessageHelper {
	/**
	 * Converts provided {@code sender} to {@link Entity} using the clientside entity lookup.
	 * This returns {@code null} if {@code sender} is {@code null} or if the entity is
	 * missing, in another world, or unloaded on the client.
	 * @param sender the message sender
	 * @return the entity from the sender, or {@code null} if the entity does not exist on the client
	 * @throws NullPointerException if the client is not in any world
	 * @see #getSenderPlayer(MessageSender)
	 */
	@Nullable
	public static Entity getSenderEntity(MessageSender sender) {
		MinecraftClient client = MinecraftClient.getInstance();
		Objects.requireNonNull(client.world, "Client is not in any world");
		if (sender == null) return null;
		return ((ClientWorldAccessor) client.world).getEntityLookup().get(sender.uuid());
	}

	/**
	 * Converts provided {@code sender} to {@link PlayerListEntry}.
	 * This returns {@code null} if {@code sender} is {@code null} or if the UUID
	 * is not found in the player list. This works for all players currently playing regardless
	 * of their current worlds.
	 * @param sender the message sender
	 * @return the player list entry from the sender, or {@code null} if the player does not exist on the client
	 * @throws NullPointerException if the client is not in any world
	 * @see #getSenderEntity(MessageSender)
	 */
	@Nullable
	public static PlayerListEntry getSenderPlayer(MessageSender sender) {
		MinecraftClient client = MinecraftClient.getInstance();
		Objects.requireNonNull(client.player, "Client is not in any world");
		if (sender == null) return null;
		return client.player.networkHandler.getPlayerListEntry(sender.uuid());
	}
}
