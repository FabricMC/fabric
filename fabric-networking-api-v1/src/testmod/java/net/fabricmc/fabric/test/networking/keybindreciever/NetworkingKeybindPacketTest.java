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

package net.fabricmc.fabric.test.networking.keybindreciever;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.test.networking.NetworkingTestmods;

// Listens for a packet from the client which is sent to the server when a keybinding is pressed.
// In response the server will send a message containing the keybind text letting the client know it pressed that key.
public final class NetworkingKeybindPacketTest implements ModInitializer {
	public static final Identifier KEYBINDING_PACKET_ID = NetworkingTestmods.id("keybind_press_test");

	private static void receive(KeybindPayload payload, ServerPlayerEntity player, PacketSender responseSender) {
		player.server.execute(() -> player.sendMessage(Text.literal("So you pressed ").append(Text.keybind("fabric-networking-api-v1-testmod-keybind").styled(style -> style.withFormatting(Formatting.BLUE))), false));
	}

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.play(NetworkSide.SERVERBOUND).register(KeybindPayload.ID, KeybindPayload.CODEC);
		ServerPlayConnectionEvents.INIT.register((handler, server) -> ServerPlayNetworking.registerReceiver(handler, KeybindPayload.ID, NetworkingKeybindPacketTest::receive));
	}

	private record KeybindPayload() implements CustomPayload {
		public static final CustomPayload.Id<KeybindPayload> ID = new CustomPayload.Id<>(KEYBINDING_PACKET_ID);
		public static final PacketCodec<RegistryByteBuf, KeybindPayload> CODEC = CustomPayload.codecOf((value, buf) -> {}, buf -> new KeybindPayload());

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

}
