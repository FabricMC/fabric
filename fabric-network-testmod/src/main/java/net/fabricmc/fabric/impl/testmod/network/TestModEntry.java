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

package net.fabricmc.fabric.impl.testmod.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.event.LoginQueryStartCallback;
import net.fabricmc.fabric.api.networking.v1.receiver.ServerPacketReceiverRegistries;
import net.fabricmc.fabric.api.networking.v1.sender.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.sender.PacketSender;

public final class TestModEntry implements ModInitializer {
	public static final String ID = "fabric-network-testmod";
	public static final Logger LOGGER = LogManager.getLogger(ID);
	public static final Identifier LOGIN_PLUGIN_MESSAGE = id("diamond_heart");

	public static Identifier id(String name) {
		return new Identifier(ID, name);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Fabric network test mod loaded!");
		LoginQueryStartCallback.EVENT.register(this::onLoginQueryStart);
		ServerPacketReceiverRegistries.LOGIN_QUERY_RESPONSE.register(LOGIN_PLUGIN_MESSAGE, (context, buffer) -> {
			if (!context.isUnderstood()) {
				LOGGER.info("The client didn't understand the brand request, shame!");
				return;
			}

			String clientBrand = buffer.readString(32767); // No arg version is client only!
			LOGGER.info("Encountered client of brand {}", clientBrand);
		});
	}

	private void onLoginQueryStart(MinecraftServer server, ServerLoginNetworkHandler networkHandler, PacketSender sender) {
		LOGGER.info("Received query start event!");
		PacketByteBuf data = PacketByteBufs.create();
		data.writeString(server.getServerModName());
		sender.send(LOGIN_PLUGIN_MESSAGE, data);
	}
}
