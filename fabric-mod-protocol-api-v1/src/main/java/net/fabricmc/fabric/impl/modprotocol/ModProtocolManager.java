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
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerConfigurationTask;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.impl.modprotocol.payload.ModProtocolRequestS2CPayload;
import net.fabricmc.loader.api.ModContainer;

public final class ModProtocolManager {
	public static final List<String> NAMESPACE_PRIORITY = new ArrayList<>(List.of("special", "mod", "feature"));
	public static final Comparator<ModProtocolImpl> MOD_PROTOCOL_COMPARATOR = Comparator.<ModProtocolImpl>comparingInt(x -> {
		int out = NAMESPACE_PRIORITY.indexOf(x.id().getNamespace());
		return out == -1 ? NAMESPACE_PRIORITY.size() : out;
	}).thenComparing(ModProtocolImpl::id);

	public static final Map<Identifier, ModProtocolImpl> LOCAL_MOD_PROTOCOLS_BY_ID = new HashMap<>();
	public static final List<ModProtocolImpl> LOCAL_MOD_PROTOCOLS = new ArrayList<>();
	public static final List<ModProtocolImpl> PING_SYNCED_PROTOCOLS = new ArrayList<>();
	public static final List<ModProtocolImpl> CLIENT_REQUIRED = new ArrayList<>();
	public static final List<ModProtocolImpl> SERVER_REQUIRED = new ArrayList<>();

	public static void setupClient(ServerConfigurationNetworkHandler handler, MinecraftServer server) {
		if (!ServerConfigurationNetworking.canSend(handler, ModProtocolRequestS2CPayload.ID)) {
			if (CLIENT_REQUIRED.isEmpty()) {
				return;
			} else {
				handler.disconnect(constructMessage(new ArrayList<>(CLIENT_REQUIRED), Map.of()));
			}
		}

		handler.addTask(new SyncConfigurationTask());
	}

	public static Text constructMessage(List<ModProtocolImpl> missingProtocols, Map<Identifier, ModProtocolImpl> localProtocols) {
		MutableText text = Text.empty();
		text.append(TextUtil.translatable("text.fabric.mod_protocol.mismatched.title").formatted(Formatting.GOLD)).append("\n");
		text.append(TextUtil.translatable("text.fabric.mod_protocol.mismatched.desc").formatted(Formatting.YELLOW)).append("\n\n");
		text.append(TextUtil.translatable("text.fabric.mod_protocol.mismatched.entries.title").formatted(Formatting.RED)).append("\n");
		appendTextEntries(missingProtocols, localProtocols, 6, text::append);
		return text;
	}

	public static void appendTextEntries(List<ModProtocolImpl> missingProtocols, Map<Identifier, ModProtocolImpl> localProtocols, int limit, Consumer<Text> consumer) {
		missingProtocols.sort(MOD_PROTOCOL_COMPARATOR);

		if (limit == -1) {
			limit = missingProtocols.size();
		}

		int size = Math.min(limit, missingProtocols.size());

		for (int i = 0; i < size; i++) {
			ModProtocolImpl protocol = missingProtocols.get(i);
			ModProtocolImpl local = localProtocols.get(protocol.id());
			Text localVersion = local == null ? TextUtil.translatable("text.fabric.mod_protocol.missing").formatted(Formatting.DARK_RED)
					: Text.literal(local.version()).formatted(Formatting.YELLOW);
			Text remoteVersion = local == protocol ? TextUtil.translatable("text.fabric.mod_protocol.missing").formatted(Formatting.DARK_RED)
					: Text.literal(protocol.version()).formatted(Formatting.YELLOW);

			MutableText text = TextUtil.translatable("text.fabric.mod_protocol.entry",
					Text.literal(protocol.name()).formatted(Formatting.WHITE), localVersion, remoteVersion).formatted(Formatting.GRAY);

			if (i + 1 < size) {
				text.append("\n");
			}

			consumer.accept(text);
		}

		if (limit < missingProtocols.size()) {
			consumer.accept(Text.literal("\n").append(TextUtil.translatable("text.fabric.mod_protocol.and_x_more", missingProtocols.size() - size).formatted(Formatting.GRAY, Formatting.ITALIC)));
		}
	}

	public static ValidationResult validateClient(Map<Identifier, ModProtocolImpl> received) {
		return validate(received, LOCAL_MOD_PROTOCOLS_BY_ID, SERVER_REQUIRED);
	}

	public static ValidationResult validate(Map<Identifier, ModProtocolImpl> received, Map<Identifier, ModProtocolImpl> localById, List<ModProtocolImpl> requiredRemote) {
		var supported = new Object2IntOpenHashMap<Identifier>();
		var missingLocal = new ArrayList<ModProtocolImpl>();
		var missingRemote = new ArrayList<ModProtocolImpl>();

		for (ModProtocolImpl modProtocol : received.values()) {
			ModProtocolImpl local = localById.get(modProtocol.id());

			if (local != null) {
				int version = local.getHighestVersion(modProtocol.protocol());

				if (version != -1) {
					supported.put(modProtocol.id(), version);
				} else if (modProtocol.requireClient()) {
					missingLocal.add(modProtocol);
				}
			} else if (modProtocol.requireClient()) {
				missingLocal.add(modProtocol);
			}
		}

		for (ModProtocolImpl modProtocol : requiredRemote) {
			ModProtocolImpl remote = received.get(modProtocol.id());

			if (remote == null) {
				missingRemote.add(modProtocol);
			}
		}

		return new ValidationResult(supported, missingLocal, missingRemote);
	}

	public static void collectModProtocols() {
		ModProtocolLocator.provide(ModProtocolManager::add);
	}

	public static ModProtocolImpl add(@Nullable ModContainer container, ModProtocolImpl protocol) {
		if (LOCAL_MOD_PROTOCOLS_BY_ID.containsKey(protocol.id())) {
			if (container != null) {
				ModProtocolInit.LOGGER.warn("Found duplicate protocol id '{}' provided by mod '{}'", protocol.id(), container.getMetadata().getId());
			} else {
				ModProtocolInit.LOGGER.warn("Found duplicate protocol id '{}' registered by a mod!'", protocol.id(), new RuntimeException());
			}

			return LOCAL_MOD_PROTOCOLS_BY_ID.get(protocol.id());
		}

		LOCAL_MOD_PROTOCOLS_BY_ID.put(protocol.id(), protocol);
		LOCAL_MOD_PROTOCOLS.add(protocol);

		if (protocol.requireClient()) {
			CLIENT_REQUIRED.add(protocol);
		}

		if (protocol.requireServer()) {
			SERVER_REQUIRED.add(protocol);
		}

		if (protocol.syncWithServerMetadata()) {
			PING_SYNCED_PROTOCOLS.add(protocol);
		}

		return null;
	}

	@SuppressWarnings("ConstantValue")
	public static boolean registerOrder(String firstNamespace, String secondNamespace) {
		if (firstNamespace.equals(secondNamespace)) {
			return false;
		}

		int firstIndex = NAMESPACE_PRIORITY.indexOf(firstNamespace);
		int secondIndex = NAMESPACE_PRIORITY.indexOf(secondNamespace);

		if (firstIndex != -1 && secondIndex != -1) {
			if (firstIndex > secondIndex) {
				ModProtocolInit.LOGGER.warn("Protocol '{}' is already set to display after '{}'!", firstNamespace, secondNamespace);
				return false;
			}

			return true;
		} else if (firstIndex == -1) {
			NAMESPACE_PRIORITY.add(secondIndex, firstNamespace);
		} else if (secondIndex == -1) {
			NAMESPACE_PRIORITY.add(firstIndex + 1, secondNamespace);
		} else {
			NAMESPACE_PRIORITY.add(firstNamespace);
			NAMESPACE_PRIORITY.add(secondNamespace);
		}

		return true;
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

	public record ValidationResult(Object2IntMap<Identifier> supportedProtocols, List<ModProtocolImpl> missingLocal, List<ModProtocolImpl> missingRemote) {
		public boolean isSuccess() {
			return missingLocal.isEmpty() && missingRemote.isEmpty();
		}

		public List<ModProtocolImpl> missing() {
			var arr = new ArrayList<ModProtocolImpl>();
			arr.addAll(missingLocal);
			arr.addAll(missingRemote);
			return arr;
		}
	}
}
