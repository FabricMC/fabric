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

package net.fabricmc.fabric.test.networking.play;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.test.networking.NetworkingTestmods;

public final class NetworkingPlayPacketClientTest implements ClientModInitializer, ClientPlayNetworking.PlayPacketHandler<NetworkingPlayPacketTest.OverlayPacket> {
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.INIT.register((handler, client) -> ClientPlayNetworking.registerReceiver(NetworkingPlayPacketTest.OverlayPacket.PACKET_TYPE, this));
	}

	@Override
	public void receive(NetworkingPlayPacketTest.OverlayPacket packet, ClientPlayerEntity player, PacketSender sender) {
		MinecraftClient.getInstance().inGameHud.setOverlayMessage(packet.message(), true);
	}

	@Override
	public boolean receiveOnNetworkThread(NetworkingPlayPacketTest.OverlayPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
		// processOnNetworkThread allows complex handling of packets, such as
		// packet merging or splitting. However, it is pretty big for a simple test mod.
		// Checks like these should normally be implemented in the receiver.
		String content = packet.message().getString();

		if (content.contains("spam")) {
			NetworkingTestmods.LOGGER.info("Filtered spam packet {}", content);
			return false; // Do not call the receiver.
		}

		return true;
	}
}
