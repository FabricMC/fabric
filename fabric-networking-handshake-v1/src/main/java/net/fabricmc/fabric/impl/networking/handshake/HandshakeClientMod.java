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

package net.fabricmc.fabric.impl.networking.handshake;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.util.PacketByteBuf;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.networking.v1.receiver.ClientLoginQueryPacketContext;
import net.fabricmc.fabric.api.networking.v1.receiver.ClientPacketReceiverRegistries;
import net.fabricmc.fabric.api.networking.v1.sender.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

public final class HandshakeClientMod implements ClientModInitializer {
	private Map<String, String> modVersions;

	@Override
	public void onInitializeClient() {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			ModMetadata metadata = mod.getMetadata();
			builder.put(metadata.getId(), metadata.getVersion().getFriendlyString());
		}

		modVersions = builder.build();

		ClientPacketReceiverRegistries.LOGIN_QUERY.register(HandshakeMod.HELLO_CHANNEL, this::handleLoginQuery);
	}

	private void handleLoginQuery(ClientLoginQueryPacketContext context, PacketByteBuf buffer) {
		int n = buffer.readVarInt();
		PacketByteBuf response = PacketByteBufs.create();
		response.writeVarInt(n);

		for (int i = 1; i <= n; i++) {
			String modId = buffer.readString();
			response.writeString(modId);
			response.writeString(modVersions.getOrDefault(modId, ""));
		}

		context.sendResponse(response);
	}
}
