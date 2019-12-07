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

import static net.fabricmc.fabric.impl.testmod.network.TestModEntry.LOGGER;

import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.receiver.ClientPacketReceiverRegistries;
import net.fabricmc.fabric.api.networking.v1.sender.PacketByteBufs;

public final class TestModClientEntry implements ClientModInitializer {
	public static final boolean RECEIVE_MESSAGE = true;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Fabric network test mod clientloaded!");

		if (RECEIVE_MESSAGE) {
			ClientPacketReceiverRegistries.LOGIN_QUERY.register(TestModEntry.LOGIN_PLUGIN_MESSAGE, (context, buffer) -> {
				String serverBrand = buffer.readString();

				LOGGER.info("Logging into server of brand {}", serverBrand);

				PacketByteBuf response = PacketByteBufs.create();
				response.writeString(ClientBrandRetriever.getClientModName());
				context.sendResponse(response);
			});
		}
	}
}
