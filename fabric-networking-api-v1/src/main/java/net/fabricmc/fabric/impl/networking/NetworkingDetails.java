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
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.client.ClientNetworking;
import net.fabricmc.fabric.api.networking.v1.server.ServerNetworking;
import net.fabricmc.fabric.api.networking.v1.util.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingDetails;
import net.fabricmc.fabric.impl.networking.server.QueryIdFactory;
import net.fabricmc.fabric.impl.networking.server.ServerNetworkingDetails;

public final class NetworkingDetails {
	public static final String MOD_ID = "fabric-networking-api-v1";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final Identifier REGISTER_CHANNEL = new Identifier("minecraft", "register");
	public static final Identifier UNREGISTER_CHANNEL = new Identifier("minecraft", "unregister");
	public static final Identifier EARLY_REGISTRATION_CHANNEL = new Identifier(MOD_ID, "early_registration");
	public static final boolean WARN_UNREGISTERED_PACKETS = Boolean
			.parseBoolean(System.getProperty(MOD_ID + ".warnUnregisteredPackets", "true"));
	public static final OffThreadGameAccessPolicy OFF_THREAD_GAME_ACCESS_POLICY = OffThreadGameAccessPolicy
			.lookup(MOD_ID + ".offThreadGameAccess", OffThreadGameAccessPolicy.THROW);

	public static QueryIdFactory createQueryIdManager() {
		// todo incremental ids or randomized
		return new QueryIdFactory() {
			private final AtomicInteger currentId = new AtomicInteger();

			@Override
			public int nextId() {
				return currentId.getAndIncrement();
			}
		};
	}

	public static void init() {
		ServerNetworking.LOGIN_QUERY_START.register(handler -> {
			PacketByteBuf buf = PacketByteBufs.create();
			Collection<Identifier> channels = ServerNetworkingDetails.PLAY.getChannels();
			buf.writeVarInt(channels.size());

			for (Identifier id : channels) {
				buf.writeIdentifier(id);
			}

			ServerNetworking.getLoginSender(handler).sendPacket(EARLY_REGISTRATION_CHANNEL, buf);
			NetworkingDetails.LOGGER.debug("Sent accepted channels to the client");
		});
		ServerNetworking.getLoginReceiver().register(EARLY_REGISTRATION_CHANNEL, (context, buf) -> {
			if (!context.isUnderstood()) {
				return;
			}

			int n = buf.readVarInt();
			List<Identifier> ids = new ArrayList<>(n);

			for (int i = 0; i < n; i++) {
				ids.add(buf.readIdentifier());
			}

			((ChannelInfoHolder) context.getListener().getConnection()).getChannels().addAll(ids);
			NetworkingDetails.LOGGER.debug("Received accepted channels from the client");
		});
	}

	@Environment(EnvType.CLIENT)
	public static void clientInit() {
		ClientNetworking.getLoginReceiver().register(EARLY_REGISTRATION_CHANNEL, (context, buf) -> {
			int n = buf.readVarInt();
			List<Identifier> ids = new ArrayList<>(n);

			for (int i = 0; i < n; i++) {
				ids.add(buf.readIdentifier());
			}

			((ChannelInfoHolder) context.getListener().getConnection()).getChannels().addAll(ids);
			NetworkingDetails.LOGGER.debug("Received accepted channels from the server");

			PacketByteBuf response = PacketByteBufs.create();
			Collection<Identifier> channels = ClientNetworkingDetails.PLAY.getChannels();
			response.writeVarInt(channels.size());

			for (Identifier id : channels) {
				response.writeIdentifier(id);
			}

			context.respond(response);
			NetworkingDetails.LOGGER.debug("Sent accepted channels to the server");
		});
	}
}
