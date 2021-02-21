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

package net.fabricmc.fabric.impl.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.text.TranslatableText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.config.v1.FabricSaveTypes;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.config.SaveType;
import net.fabricmc.loader.config.ValueContainerProviders;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.config.v1.DataTypes;
import net.fabricmc.fabric.api.config.v1.SyncType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.config.ConfigDefinition;
import net.fabricmc.loader.api.config.ConfigManager;
import net.fabricmc.loader.api.config.ConfigSerializer;
import net.fabricmc.loader.api.config.value.ValueContainer;
import net.fabricmc.loader.api.config.value.ValueContainerProvider;
import net.fabricmc.loader.api.config.value.ValueKey;

public class SyncConfigValuesImpl implements ModInitializer, ClientModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final Identifier USER_CONFIG_VALUES = new Identifier("fabric", "packet/sync_values/user");
	public static final Identifier LEVEL_CONFIG_VALUES = new Identifier("fabric", "packet/sync_values/level");

	@Override
	@Environment(EnvType.CLIENT)
	public void onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register(SyncConfigValuesImpl::sendConfigValues);
		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ClientPlayNetworking.registerReceiver(USER_CONFIG_VALUES, SyncConfigValuesImpl::receiveUserConfigValues);
			ClientPlayNetworking.registerReceiver(LEVEL_CONFIG_VALUES, SyncConfigValuesImpl::receiveLevelConfigValues);
		});
	}

	@Override
	public void onInitialize() {
		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
			ServerPlayNetworking.registerReceiver(handler, USER_CONFIG_VALUES, SyncConfigValuesImpl::receiveUserConfigValues);
			((ConfigValueSender) server).sendCached(handler.player);
			sendConfigValues(handler.player);
		}));

		ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
			((ConfigValueSender) server).drop(handler.player);
		}));
	}

	private static void sendConfigValues(ServerPlayerEntity player) {
		for (ConfigDefinition<?> configDefinition : ConfigManager.getConfigKeys()) {
			sendConfigValues(configDefinition, player);
		}
	}

	private static <R> void sendConfigValues(ConfigDefinition<R> configDefinition, ServerPlayerEntity player) {
		SaveType saveType = configDefinition.getSaveType();

		if (saveType == FabricSaveTypes.LEVEL || saveType == SaveType.ROOT) {
			ConfigSerializer<R> serializer = configDefinition.getSerializer();
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

			ValueContainer valueContainer = ValueContainerProviders.getInstance(saveType).getValueContainer();

			buf.writeString(configDefinition.toString());

			try {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				serializer.serialize(configDefinition, stream, valueContainer, configValue -> {
					for (SyncType syncType : configValue.getData(DataTypes.SYNC_TYPE)) {
						if (syncType == SyncType.INFO || player.hasPermissionLevel(4)) return true;
					}

					return false;
				}, true);

				byte[] bytes = stream.toByteArray();

				if (bytes.length > 0) {
					buf.writeByteArray(bytes);
					ServerPlayNetworking.send(player, LEVEL_CONFIG_VALUES, buf);
				}
			} catch (IOException e) {
				LOGGER.error("Failed to sync config '{}': {}", configDefinition, e.getMessage());
			}
		}
	}

	@Environment(EnvType.CLIENT)
	private static void sendConfigValues(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient client) {
		sendConfigValues();
	}

	@Environment(EnvType.CLIENT)
	public static void sendConfigValues() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (!client.isIntegratedServerRunning() && client.getCurrentServerEntry() == null) return;

		for (ConfigDefinition<?> configDefinition : ConfigManager.getConfigKeys()) {
			sendConfigValues(configDefinition);
		}
	}

	@Environment(EnvType.CLIENT)
	public static <R> void sendConfigValues(ConfigDefinition<R> configDefinition) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (!client.isIntegratedServerRunning() && client.getCurrentServerEntry() == null) return;

		ValueContainer valueContainer = ValueContainer.ROOT;

		Collection<SyncType> syncTypes = new HashSet<>();

		for (ValueKey<?> value : configDefinition) {
			for (SyncType syncType : value.getData(DataTypes.SYNC_TYPE)) {
				if (syncType != SyncType.NONE) {
					syncTypes.add(syncType);
				}
			}
		}

		for (SyncType syncType : configDefinition.getData(DataTypes.SYNC_TYPE)) {
			if (syncType != SyncType.NONE) {
				syncTypes.add(syncType);
			}
		}

		if (syncTypes.size() > 0) {
			ConfigSerializer<R> serializer = configDefinition.getSerializer();
			PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

			buf.writeString(configDefinition.toString());
			buf.writeString(configDefinition.getVersion().toString());
			buf.writeVarInt(syncTypes.size());

			for (SyncType syncType : syncTypes) {
				buf.writeEnumConstant(syncType);
			}

			try {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				serializer.serialize(configDefinition, stream, valueContainer, configValue -> {
					for (SyncType syncType : configValue.getData(DataTypes.SYNC_TYPE)) {
						if (syncTypes.contains(syncType)) return true;
					}

					return false;
				}, true);

				byte[] bytes = stream.toByteArray();

				if (bytes.length > 0) {
					buf.writeByteArray(bytes);
					ClientPlayNetworking.send(USER_CONFIG_VALUES, buf);
				}
			} catch (IOException e) {
				LOGGER.error("Failed to sync config '{}': {}", configDefinition, e.getMessage());
			}
		}
	}

	private static <R> void receiveUserConfigValues(MinecraftServer server, ServerPlayerEntity sender, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
		UUID playerUuid = sender.getUuid();

		buf.markReaderIndex();

		ConfigDefinition<R> configDefinition = ConfigManager.getDefinition(buf.readString(32767));

		String userVersionString = buf.readString(32767);

		Collection<SyncType> syncTypes = new HashSet<>();

		int n = buf.readVarInt();

		for (int i = 0; i < n; ++i) {
			syncTypes.add(buf.readEnumConstant(SyncType.class));
		}

		// We won't save the config values on the server if the definition is null, but we'll still forward it to other
		// players in the case of peer to peer syncing.
		if (configDefinition != null && syncTypes.contains(SyncType.INFO)) {
			try {
				SemanticVersion userVersion = SemanticVersion.parse(userVersionString);
				SemanticVersion serverVersion = configDefinition.getVersion();

				if (userVersion.getVersionComponent(0) != serverVersion.getVersionComponent(0)) {
					// We require that the sides have matching config versions at least in the major component
					handler.disconnect(new TranslatableText("fabric.config.invalid_version",
							configDefinition.toString(),
							userVersionString,
							serverVersion.toString(),
							serverVersion.getVersionComponent(0)));

					// We'll also abort here to avoid sending useless info to other connected clients
					return;
				}
			} catch (VersionParsingException e) {
				handler.disconnect(new TranslatableText("fabric.config.version_parse", configDefinition.toString(), userVersionString));
				// We'll also abort here to avoid sending useless info to other connected clients
				// Ideally we'd be able to do this during the login phase so that clients don't finish connecting at all
				return;
			}

			InputStream inputStream = new ByteArrayInputStream(buf.readByteArray());
			ValueContainer valueContainer = ((ValueContainerProvider) server).getPlayerValueContainer(playerUuid);

			try {
				configDefinition.getSerializer().deserialize(configDefinition, inputStream, valueContainer);
			} catch (IOException e) {
				LOGGER.error("Failed to sync config '{}': {}", configDefinition, e.getMessage());
			}
		}

		buf.resetReaderIndex();

		if (syncTypes.contains(SyncType.P2P)) {
			sendConfigValues(configDefinition, sender, server, buf);
		}
	}

	@Environment(EnvType.CLIENT)
	private static <R> void receiveLevelConfigValues(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
		ConfigDefinition<R> configDefinition = ConfigManager.getDefinition(buf.readString(32767));

		if (configDefinition != null && client.getCurrentServerEntry() != null) {
			InputStream inputStream = new ByteArrayInputStream(buf.readByteArray());
			ValueContainer valueContainer = ((ValueContainerProvider) client.getCurrentServerEntry()).getValueContainer();

			try {
				configDefinition.getSerializer().deserialize(configDefinition, inputStream, valueContainer);
			} catch (IOException e) {
				LOGGER.error("Failed to sync config '{}': {}", configDefinition, e.getMessage());
			}
		}
	}

	public static <R> void sendConfigValues(ConfigDefinition<R> configDefinition, ServerPlayerEntity except, MinecraftServer server, PacketByteBuf buf) {
		PacketByteBuf peerBuf = new PacketByteBuf(Unpooled.buffer());

		peerBuf.writeUuid(except.getUuid());

		peerBuf.writeString(buf.readString(32767));

		int n = buf.readVarInt();

		for (int i = 0; i < n; ++i) {
			buf.readEnumConstant(SyncType.class);
		}

		byte[] bytes = buf.readByteArray();
		peerBuf.writeByteArray(bytes);

		((ConfigValueSender) server).send(configDefinition, except, peerBuf);
	}

	@Environment(EnvType.CLIENT)
	private static <R> void receiveUserConfigValues(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
		UUID playerId = buf.readUuid();

		ConfigDefinition<R> configDefinition = ConfigManager.getDefinition(buf.readString());

		// We won't save the config values on the server if the definition is null, but we'll still forward it to other
		// players in the case of peer to peer syncing.
		if (configDefinition != null && client.getCurrentServerEntry() != null) {
			byte[] bytes = buf.readByteArray();
			InputStream inputStream = new ByteArrayInputStream(bytes);
			ValueContainer valueContainer = ((ValueContainerProvider) client.getCurrentServerEntry()).getPlayerValueContainer(playerId);

			try {
				configDefinition.getSerializer().deserialize(configDefinition, inputStream, valueContainer);
			} catch (IOException e) {
				LOGGER.error("Failed to sync config '{}': {}", configDefinition, e.getMessage());
			}
		}
	}
}
