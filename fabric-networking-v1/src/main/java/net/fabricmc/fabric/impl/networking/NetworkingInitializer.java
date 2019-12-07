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

package net.fabricmc.fabric.impl.networking;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.MapMaker;

import net.minecraft.network.ClientConnection;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.event.LoginQueryStartCallback;
import net.fabricmc.fabric.api.networking.v1.receiver.ServerPacketReceiverRegistries;
import net.fabricmc.fabric.api.networking.v1.sender.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.receiver.ServerPacketReceivers;

public final class NetworkingInitializer implements ModInitializer {
	private static NetworkingInitializer instance;
	private final Map<ClientConnection, Identifier[]> connectionSpecificChannels = new MapMaker().weakKeys().initialCapacity(2).concurrencyLevel(2).makeMap();

	public static NetworkingInitializer getInstance() {
		return instance;
	}

	public Map<ClientConnection, Identifier[]> getConnectionSpecificChannels() {
		return connectionSpecificChannels;
	}

	@Override
	public void onInitialize() {
		instance = this;
		LoginQueryStartCallback.EVENT.register((server, networkHandler, sender) -> {
			sender.send(PacketHelper.QUERY_CHANNELS, writeChannels(ServerPacketReceivers.PLAY.getAcceptedChannels()));
		});
		ServerPacketReceiverRegistries.LOGIN_QUERY_RESPONSE.register(PacketHelper.QUERY_CHANNELS, (context, buffer) -> {
			if (!context.isUnderstood()) {
				return;
			}

			this.getConnectionSpecificChannels().put(context.getNetworkHandler().getConnection(), readChannels(buffer));
		});
	}

	static PacketByteBuf writeChannels(Collection<Identifier> ids) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(ids.size());

		for (Identifier id : ids) {
			buf.writeString(id.toString(), 32767);
		}

		return buf;
	}

	static Identifier[] readChannels(PacketByteBuf buffer) {
		int n = buffer.readVarInt();
		Identifier[] ids = new Identifier[n];

		for (int i = 0; i < n; i++) {
			ids[i] = new Identifier(buffer.readString(32767));
		}

		return ids;
	}
}
