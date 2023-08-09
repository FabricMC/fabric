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

import java.util.Arrays;
import java.util.function.Consumer;

import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerConfigurationTask;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.impl.networking.server.ServerConfigurationNetworkAddon;
import net.fabricmc.fabric.impl.networking.server.ServerNetworkingImpl;

public class CommonPacketsImpl {
	public static final int PACKET_VERSION_1 = 1;
	public static final int[] SUPPORTED_COMMON_PACKET_VERSIONS = new int[]{ PACKET_VERSION_1 };

	public static void init() {
		ServerConfigurationNetworking.registerGlobalReceiver(CommonVersionPayload.PACKET_ID, (server, handler, buf, responseSender) -> {
			var payload = new CommonVersionPayload(buf);
			ServerConfigurationNetworkAddon addon = ServerNetworkingImpl.getAddon(handler);
			addon.onCommonVersionPacket(getNegotiatedVersion(payload));
			handler.completeTask(CommonVersionConfigurationTask.KEY);
		});

		ServerConfigurationNetworking.registerGlobalReceiver(CommonRegisterPayload.PACKET_ID, (server, handler, buf, responseSender) -> {
			var payload = new CommonRegisterPayload(buf);
			ServerConfigurationNetworkAddon addon = ServerNetworkingImpl.getAddon(handler);

			if (CommonRegisterPayload.PLAY_PHASE.equals(payload.phase())) {
				if (payload.version() != addon.getNegotiatedVersion()) {
					throw new IllegalStateException("Negotiated common packet version: %d but received packet with version: %d".formatted(addon.getNegotiatedVersion(), payload.version()));
				}

				// Play phase hasnt started yet, add them to the pending names.
				addon.getChannelInfoHolder().getPendingChannelsNames(NetworkState.PLAY).addAll(payload.channels());
				NetworkingImpl.LOGGER.debug("Received accepted channels from the client for play phase");
			} else {
				addon.onCommonRegisterPacket(payload);
			}

			handler.completeTask(CommonRegisterConfigurationTask.KEY);
		});

		// Create a configuration task to send and receive the common packets
		ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
			final ServerConfigurationNetworkAddon addon = ServerNetworkingImpl.getAddon(handler);

			if (ServerConfigurationNetworking.canSend(handler, CommonVersionPayload.PACKET_ID)) {
				// Tasks are processed in order.
				handler.addTask(new CommonVersionConfigurationTask(addon));

				if (ServerConfigurationNetworking.canSend(handler, CommonRegisterPayload.PACKET_ID)) {
					handler.addTask(new CommonRegisterConfigurationTask(addon));
				}
			}
		});
	}

	// A configuration phase task to send and receive the version packets.
	private record CommonVersionConfigurationTask(ServerConfigurationNetworkAddon addon) implements ServerPlayerConfigurationTask {
		public static final Key KEY = new Key(CommonVersionPayload.PACKET_ID.toString());

		@Override
		public void sendPacket(Consumer<Packet<?>> sender) {
			addon.sendPacket(new CommonVersionPayload(SUPPORTED_COMMON_PACKET_VERSIONS));
		}

		@Override
		public Key getKey() {
			return KEY;
		}
	}

	// A configuration phase task to send and receive the registration packets.
	private record CommonRegisterConfigurationTask(ServerConfigurationNetworkAddon addon) implements ServerPlayerConfigurationTask {
		public static final Key KEY = new Key(CommonRegisterPayload.PACKET_ID.toString());

		@Override
		public void sendPacket(Consumer<Packet<?>> sender) {
			addon.sendPacket(addon.createRegisterPayload());
		}

		@Override
		public Key getKey() {
			return KEY;
		}
	}

	private static int getNegotiatedVersion(CommonVersionPayload payload) {
		int version = getHighestCommonVersion(payload.versions(), SUPPORTED_COMMON_PACKET_VERSIONS);

		if (version <= 0) {
			throw new UnsupportedOperationException("server does not support any requested versions from client");
		}

		return version;
	}

	public static int getHighestCommonVersion(int[] a, int[] b) {
		int[] as = a.clone();
		int[] bs = b.clone();

		Arrays.sort(as);
		Arrays.sort(bs);

		int ap = as.length - 1;
		int bp = bs.length - 1;

		while (ap >= 0 && bp >= 0) {
			if (as[ap] == bs[bp]) {
				return as[ap];
			}

			if (as[ap] > bs[bp]) {
				ap--;
			} else {
				bp--;
			}
		}

		return -1;
	}
}
