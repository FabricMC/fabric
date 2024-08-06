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
import java.util.Comparator;
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
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.impl.modprotocol.payload.ModProtocolRequestS2CPayload;
import net.fabricmc.loader.api.ModContainer;

public final class ModProtocolManager {
	public static final List<String> NAMESPACE_PRIORITY = new ArrayList<>(List.of("special", "mod", "feature"));
	public static final Comparator<ModProtocol> MOD_PROTOCOL_COMPARATOR = Comparator.<ModProtocol>comparingInt(x -> {
		var out = NAMESPACE_PRIORITY.indexOf(x.id().getNamespace());
		return out == -1 ? NAMESPACE_PRIORITY.size() : out;
	}).thenComparing(ModProtocol::id);

	public static final Map<Identifier, ModProtocol> LOCAL_MOD_PROTOCOLS_BY_ID = new HashMap<>();
	public static final List<ModProtocol> LOCAL_MOD_PROTOCOLS = new ArrayList<>();
	public static final List<ModProtocol> PING_SYNCED_PROTOCOLS = new ArrayList<>();
	public static final List<ModProtocol> CLIENT_REQUIRED = new ArrayList<>();
	public static final List<ModProtocol> SERVER_REQUIRED = new ArrayList<>();

	public static void setupClient(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
		if (!ServerConfigurationNetworking.canSend(handler, ModProtocolRequestS2CPayload.ID)) {
			if (CLIENT_REQUIRED.isEmpty()) {
				return;
			} else {
				handler.disconnect(constructMessage(CLIENT_REQUIRED, Map.of()));
			}
		}

		handler.addTask(new SyncConfigurationTask());
	}

	public static Text constructMessage(List<ModProtocol> missingProtocols, Map<Identifier, ModProtocol> localProtocols) {
		var text = Text.empty();
		text.append(TextUtil.translatable("text.fabric.mod_protocol.mismatched.title").formatted(Formatting.GOLD)).append("\n");
		text.append(TextUtil.translatable("text.fabric.mod_protocol.mismatched.desc").formatted(Formatting.YELLOW)).append("\n\n");
		text.append(TextUtil.translatable("text.fabric.mod_protocol.mismatched.entries.title").formatted(Formatting.RED)).append("\n");
		appendTextEntries(missingProtocols, localProtocols,  6, text::append);
		return text;
	}

	public static void appendTextEntries(List<ModProtocol> missingProtocols, Map<Identifier, ModProtocol> localProtocols, int limit, Consumer<Text> consumer) {
		missingProtocols.sort(MOD_PROTOCOL_COMPARATOR);
		if (limit == -1) {
			limit = missingProtocols.size();
		}
		var size = Math.min(limit, missingProtocols.size());
		for (int i = 0; i < size; i++) {
			var protocol = missingProtocols.get(i);
			var local = localProtocols.get(protocol.id());
			var localVersion = local == null ? TextUtil.translatable("text.fabric.mod_protocol.missing").formatted(Formatting.DARK_RED)
					: Text.literal(local.displayVersion()).formatted(Formatting.YELLOW);
			var remoteVersion = local == protocol ? TextUtil.translatable("text.fabric.mod_protocol.missing").formatted(Formatting.DARK_RED)
					: Text.literal(protocol.displayVersion()).formatted(Formatting.YELLOW);

			var text = TextUtil.translatable("text.fabric.mod_protocol.entry",
					Text.literal(protocol.displayName()).formatted(Formatting.WHITE), localVersion, remoteVersion).formatted(Formatting.GRAY);
			if (i + 1 < size) {
				text.append("\n");
			}
			consumer.accept(text);
		}
		if (limit < missingProtocols.size()) {
			consumer.accept(Text.literal("\n").append(TextUtil.translatable("text.fabric.mod_protocol.and_x_more", missingProtocols.size() - size).formatted(Formatting.GRAY, Formatting.ITALIC)));
		}
	}

	public static ValidationResult validateClient(Map<Identifier, ModProtocol> received) {
		return validate(received, LOCAL_MOD_PROTOCOLS_BY_ID, SERVER_REQUIRED);
	}

	public static ValidationResult validate(Map<Identifier, ModProtocol> received, Map<Identifier, ModProtocol> localById, List<ModProtocol> requiredRemote) {
		var supported = new Object2IntOpenHashMap<Identifier>();
		var missingLocal = new ArrayList<ModProtocol>();
		var missingRemote = new ArrayList<ModProtocol>();

		for (var modProtocol : received.values()) {
			var local = localById.get(modProtocol.id());
			if (local != null) {
				var version = local.getHighestVersion(modProtocol.protocols());
				if (version != -1) {
					supported.put(modProtocol.id(), version);
				} else if (modProtocol.requiredClient()) {
					missingLocal.add(modProtocol);
				}
			} else if (modProtocol.requiredClient()) {
				missingLocal.add(modProtocol);
			}
		}

		for (var modProtocol : requiredRemote) {
			var remote = received.get(modProtocol.id());
			if (remote == null) {
				missingRemote.add(modProtocol);
			}
		}


		return new ValidationResult(supported, missingLocal, missingRemote);
	}

	public static void collectModProtocols() {
		ModProtocolLocator.provide(ModProtocolManager::add);
	}

	public static void add(ModContainer container, ModProtocol protocol) {
		if (LOCAL_MOD_PROTOCOLS_BY_ID.containsKey(protocol.id())) {
			ModProtocolInit.LOGGER.warn("Found duplicate protocol id '{}' provided by mod '{}'", protocol.id(), (container != null ? container.getMetadata().getId() : "<NULL!>"));
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
		if (protocol.syncWithServerMetadata()) {
			PING_SYNCED_PROTOCOLS.add(protocol);
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

	public record ValidationResult(Object2IntMap<Identifier> supportedProtocols, List<ModProtocol> missingLocal, List<ModProtocol> missingRemote) {
		public boolean isSuccess() {
			return missingLocal.isEmpty() && missingRemote.isEmpty();
		}

		public List<ModProtocol> missing() {
			var arr = new ArrayList<ModProtocol>();
			arr.addAll(missingLocal);
			arr.addAll(missingRemote);
			return arr;
		}
	}
}
