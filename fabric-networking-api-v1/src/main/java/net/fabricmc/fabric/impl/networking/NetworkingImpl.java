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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerNetworking;
import net.fabricmc.fabric.impl.networking.server.ServerNetworkingImpl;

public final class NetworkingImpl {
	public static final String MOD_ID = "fabric-networking-api-v1";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	/**
	 * Id of packet used to register supported channels.
	 */
	public static final Identifier REGISTER_CHANNEL = new Identifier("minecraft", "register");
	/**
	 * Id of packet used to unregister supported channels.
	 */
	public static final Identifier UNREGISTER_CHANNEL = new Identifier("minecraft", "unregister");
	/**
	 * Id of the packet used to declare all currently supported channels.
	 * Dynamic registration of supported channels is still allowed using {@link NetworkingImpl#REGISTER_CHANNEL} and {@link NetworkingImpl#UNREGISTER_CHANNEL}.
	 */
	public static final Identifier EARLY_REGISTRATION_CHANNEL = new Identifier(MOD_ID, "early_registration");

	public static void init() {
		ServerConnectionEvents.LOGIN_QUERY_START.register((handler, server, sender, synchronizer) -> {
			PacketByteBuf buf = PacketByteBufs.create();
			Collection<Identifier> channels = ServerNetworkingImpl.PLAY.getChannels();
			buf.writeVarInt(channels.size());

			for (Identifier id : channels) {
				buf.writeIdentifier(id);
			}

			sender.sendPacket(EARLY_REGISTRATION_CHANNEL, buf);
			NetworkingImpl.LOGGER.debug("Sent accepted channels to the client");
		});

		ServerNetworking.getLoginReceivers().register(EARLY_REGISTRATION_CHANNEL, (handler, server, sender, buf, understood, synchronizer) -> {
			if (!understood) {
				// The client is likely a vanilla client.
				return;
			}

			int n = buf.readVarInt();
			List<Identifier> ids = new ArrayList<>(n);

			for (int i = 0; i < n; i++) {
				ids.add(buf.readIdentifier());
			}

			((ChannelInfoHolder) handler.getConnection()).getChannels().addAll(ids);
			NetworkingImpl.LOGGER.debug("Received accepted channels from the client");
		});
	}

	public static boolean isReservedChannel(Identifier id) {
		return id.equals(REGISTER_CHANNEL) || id.equals(UNREGISTER_CHANNEL);
	}
}
