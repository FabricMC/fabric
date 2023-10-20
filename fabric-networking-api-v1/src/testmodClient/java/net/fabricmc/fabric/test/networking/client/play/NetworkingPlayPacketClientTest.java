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

package net.fabricmc.fabric.test.networking.client.play;

import com.mojang.brigadier.Command;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.test.networking.NetworkingTestmods;
import net.fabricmc.fabric.test.networking.play.NetworkingPlayPacketTest;

public final class NetworkingPlayPacketClientTest implements ClientModInitializer, ClientPlayNetworking.PlayPacketHandler<NetworkingPlayPacketTest.OverlayPacket> {
	private static final Identifier UNKNOWN_TEST_CHANNEL = NetworkingTestmods.id("unknown_test_channel");

	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.INIT.register((handler, client) -> ClientPlayNetworking.registerReceiver(NetworkingPlayPacketTest.OverlayPacket.PACKET_TYPE, this));

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
				ClientCommandManager.literal("clientnetworktestcommand")
						.then(ClientCommandManager.literal("unknown").executes(context -> {
							ClientPlayNetworking.send(UNKNOWN_TEST_CHANNEL, PacketByteBufs.create());
							return Command.SINGLE_SUCCESS;
						}
		))));
	}

	@Override
	public void receive(NetworkingPlayPacketTest.OverlayPacket packet, ClientPlayerEntity player, PacketSender sender) {
		MinecraftClient.getInstance().inGameHud.setOverlayMessage(packet.message(), true);
	}
}
