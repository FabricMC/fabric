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

package net.fabricmc.fabric.impl.modprotocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerConfigurationTask;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.impl.modprotocol.payload.ModProtocolRequestS2CPayload;
import net.fabricmc.loader.api.ModContainer;

public final class ModProtocolManager {
	public static final Map<String, ModProtocol> LOCAL_MOD_PROTOCOLS_BY_ID = new HashMap<>();
	public static final List<ModProtocol> LOCAL_MOD_PROTOCOLS = new ArrayList<>();
	public static final List<ModProtocol> CLIENT_REQUIRED = new ArrayList<>();
	public static final List<ModProtocol> SERVER_REQUIRED = new ArrayList<>();

	public static void setupClient(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
		if (!ServerConfigurationNetworking.canSend(handler, ModProtocolRequestS2CPayload.ID)) {
			if (CLIENT_REQUIRED.isEmpty()) {
				return;
			} else {
				handler.disconnect(Text.literal("Server requires mods // todo better message"));
			}
		}

		handler.addTask(new SyncConfigurationTask());
	}

	public static ValidationResult validateClient(Map<String, ModProtocol> received) {
		return validate(received, LOCAL_MOD_PROTOCOLS_BY_ID, SERVER_REQUIRED);
	}

	public static ValidationResult validate(Map<String, ModProtocol> received, Map<String, ModProtocol> localById, List<ModProtocol> requiredRemote) {
		var supported = new Object2IntOpenHashMap<String>();
		var missingLocal = new ArrayList<ModProtocol>();
		var missingRemote = new ArrayList<ModProtocol>();

		for (var modProtocol : received.values()) {
			var local = localById.get(modProtocol.id());
			if (local != null) {
				supported.put(modProtocol.id(), local.getHighestVersion(modProtocol.protocols()));
			} else if (modProtocol.requiredClient()) {
				missingLocal.add(modProtocol);
			}
		}

		for (var modProtocol : requiredRemote) {
			var remote = received.get(modProtocol.id());
			if (remote == null || supported.getOrDefault(modProtocol.id(), -1) == -1) {
				missingRemote.add(modProtocol);
			}
		}


		return new ValidationResult(supported, missingLocal, missingRemote);
	}

	public static void collectModProtocols() {
		ModProtocolLocator.provide(ModProtocolManager::add);

	}

	private static void add(ModContainer container, ModProtocol protocol) {
		if (LOCAL_MOD_PROTOCOLS_BY_ID.containsKey(protocol.id())) {
			ModProtocolInit.LOGGER.warn("Found duplicate protocol id '{}' provided by mod '{}'", protocol.id(), container.getMetadata().getId());
			return;
		}
		LOCAL_MOD_PROTOCOLS_BY_ID.put(protocol.id(), protocol);
		LOCAL_MOD_PROTOCOLS.add(protocol);

		if (protocol.requiredClient()) {
			CLIENT_REQUIRED.add(protocol);
		}
		if (protocol.requiredServer()) {
			SERVER_REQUIRED.add(protocol);
		}
	}

	public static class SyncConfigurationTask implements ServerPlayerConfigurationTask {
		public static final Key KEY = new Key("fabric:mod_protocol_sync");
		@Override
		public void sendPacket(Consumer<Packet<?>> sender) {
			sender.accept(new CustomPayloadS2CPacket(new ModProtocolRequestS2CPayload(LOCAL_MOD_PROTOCOLS)));
		}

		@Override
		public Key getKey() {
			return KEY;
		}
	}

	public record ValidationResult(Object2IntMap<String> supportedProtocols, List<ModProtocol> missingLocal, List<ModProtocol> missingRemote) {
		public boolean isSuccess() {
			return missingLocal.isEmpty() && missingRemote.isEmpty();
		}
	}
}
