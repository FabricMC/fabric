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

package net.fabricmc.fabric.impl.registry.sync;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.registry.sync.packet.DirectRegistrySyncPacket;
import net.fabricmc.fabric.impl.registry.sync.packet.NbtRegistrySyncPacket;
import net.fabricmc.fabric.impl.registry.sync.packet.RegistrySyncPacket;

public class FabricRegistryClientInit implements ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(DirectRegistrySyncPacket.ID, (client, handler, buf, responseSender) ->
				receivePacket(client, handler, DirectRegistrySyncPacket.getInstance(), buf));

		ClientPlayNetworking.registerGlobalReceiver(NbtRegistrySyncPacket.ID, (client, handler, buf, responseSender) ->
				receivePacket(client, handler, NbtRegistrySyncPacket.getInstance(), buf));
	}

	private void receivePacket(MinecraftClient client, ClientPlayNetworkHandler handler, RegistrySyncPacket packet, PacketByteBuf buf) {
		RegistrySyncManager.receivePacket(client, packet, buf, RegistrySyncManager.DEBUG || !client.isInSingleplayer(), (e) -> {
			LOGGER.error("Registry remapping failed!", e);

			client.execute(() ->
					handler.getConnection().disconnect(new LiteralText("Registry remapping failed: " + e.getMessage())));
		});
	}
}
