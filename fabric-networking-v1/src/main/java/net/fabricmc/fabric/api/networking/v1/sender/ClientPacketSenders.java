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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.receiver.ServerPacketReceiverRegistries;
import net.fabricmc.fabric.impl.networking.hook.PlayNetworkHandlerHook;

/**
 * Utility methods for getting packet senders on logical client.
 */
public final class ClientPacketSenders {
	private ClientPacketSenders() {
	}

	/**
	 * Gets the custom payload packet sender of a client play network handler.
	 *
	 * @param handler the network handler
	 * @return the packet sender
	 * @see ServerPacketReceiverRegistries#PLAY
	 */
	public static PlayPacketSender of(ClientPlayNetworkHandler handler) {
		return ((PlayNetworkHandlerHook) handler).getPacketSender();
	}

	/**
	 * Gets the custom payload packet sender of a the network handler of the current client player.
	 *
	 * @return the packet sender
	 * @see ServerPacketReceiverRegistries#PLAY
	 */
	@Environment(EnvType.CLIENT)
	public static PlayPacketSender of() {
		return of(MinecraftClient.getInstance().player.networkHandler);
	}
}
