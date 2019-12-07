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

package net.fabricmc.fabric.api.networking.v1.sender;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.receiver.ClientPacketReceiverRegistries;
import net.fabricmc.fabric.api.networking.v1.receiver.ServerPacketReceiverRegistries;
import net.fabricmc.fabric.impl.networking.hook.PlayNetworkHandlerHook;
import net.fabricmc.fabric.impl.networking.hook.ServerLoginNetworkHandlerHook;

/**
 * Utility methods for working with packet senders.
 */
public final class PacketSenders {
	private PacketSenders() {
	}

	/**
	 * Gets the custom payload packet sender of a server play network handler.
	 *
	 * @param handler the network handler
	 * @return the packet sender
	 * @see ClientPacketReceiverRegistries#PLAY
	 */
	public static PlayPacketSender of(ServerPlayNetworkHandler handler) {
		return ((PlayNetworkHandlerHook) handler).getPacketSender();
	}

	/**
	 * Gets the custom payload packet sender of a client play network handler.
	 *
	 * @param handler the network handler
	 * @return the packet sender
	 * @see ServerPacketReceiverRegistries#PLAY
	 */
	@Environment(EnvType.CLIENT) // method descriptor
	public static PlayPacketSender of(ClientPlayNetworkHandler handler) {
		return ((PlayNetworkHandlerHook) handler).getPacketSender();
	}

	/**
	 * Gets the login query packet sender of a server login network handler.
	 *
	 * @param handler the network handler
	 * @return the packet sender
	 * @see ClientPacketReceiverRegistries#LOGIN_QUERY
	 * @see ServerPacketReceiverRegistries#LOGIN_QUERY_RESPONSE
	 */
	public static PacketSender of(ServerLoginNetworkHandler handler) {
		return ((ServerLoginNetworkHandlerHook) handler).getPacketSender();
	}

	/**
	 * Gets the custom payload packet sender of a the network handler of the current client player.
	 *
	 * @return the packet sender
	 * @see ServerPacketReceiverRegistries#PLAY
	 */
	@Environment(EnvType.CLIENT)
	public static PlayPacketSender ofClient() {
		return of(MinecraftClient.getInstance().player.networkHandler);
	}

	/**
	 * Gets the custom payload packet sender of a server player's network handler.
	 *
	 * @param player the server player
	 * @return the packet sender
	 * @see ClientPacketReceiverRegistries#PLAY
	 */
	public static PlayPacketSender of(ServerPlayerEntity player) {
		return of(player.networkHandler);
	}
}
