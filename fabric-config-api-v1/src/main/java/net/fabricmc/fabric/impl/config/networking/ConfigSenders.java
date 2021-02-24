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

package net.fabricmc.fabric.impl.config.networking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

import io.netty.buffer.Unpooled;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.config.v1.FabricDataTypes;
import net.fabricmc.fabric.api.config.v1.FabricSaveTypes;
import net.fabricmc.fabric.api.config.v1.SyncType;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.config.ConfigValueSender;
import net.fabricmc.loader.api.config.ConfigDefinition;
import net.fabricmc.loader.api.config.ConfigManager;
import net.fabricmc.loader.api.config.data.SaveType;
import net.fabricmc.loader.api.config.value.ValueContainer;
import net.fabricmc.loader.api.config.value.ValueKey;
import net.fabricmc.loader.api.config.serialization.ConfigSerializer;

public class ConfigSenders {
	private static final Logger LOGGER = LogManager.getLogger();

	@Environment(EnvType.CLIENT)
	public static <R> void sendToServer(ConfigDefinition<R> configDefinition, ValueContainer valueContainer) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (!client.isIntegratedServerRunning() && client.getCurrentServerEntry() == null) return;
		ClientPlayerEntity player = client.player;
		SaveType saveType = configDefinition.getSaveType();

		// If the player doesn't exist we can't check their permission level
		// and if their permission level isn't high enough, we don't want them sending config values
		if (player == null || (saveType == FabricSaveTypes.LEVEL && !player.hasPermissionLevel(4))
				// Also don't try and sync save types other than Fabric's builtin save types.
				|| saveType != FabricSaveTypes.LEVEL && saveType != FabricSaveTypes.USER) return;

		PacketByteBuf buf = toPacket(configDefinition, valueContainer);

		if (buf != null) {
			ClientPlayNetworking.send(ConfigNetworking.SYNC_CONFIG, buf);
		}
	}

	public static <R> void sendToPlayer(ConfigDefinition<R> configDefinition, ServerPlayerEntity player, ValueContainer valueContainer) {
		PacketByteBuf buf = toPacket(configDefinition, valueContainer);

		if (buf != null) {
			ServerPlayNetworking.send(player, ConfigNetworking.SYNC_CONFIG, buf);
		}
	}

	public static <R> void sendToPlayers(MinecraftServer server, ConfigDefinition<R> configDefinition, ValueContainer valueContainer) {
		PacketByteBuf buf = toPacket(configDefinition, valueContainer);

		if (buf != null) {
			PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, ConfigNetworking.SYNC_CONFIG, buf));
		}
	}

	private static <R> @Nullable PacketByteBuf toPacket(ConfigDefinition<R> configDefinition, ValueContainer valueContainer) {
		SaveType saveType = configDefinition.getSaveType();

		Predicate<ValueKey<?>> predicate = key -> true;
		boolean forward = false;

		// We only want to construct and send the packet if it's actually gonna contain values
		if (saveType == FabricSaveTypes.USER) {
			Collection<SyncType> syncTypes = new HashSet<>();

			// Checks each config key
			for (ValueKey<?> value : configDefinition) {
				for (SyncType syncType : value.getData(FabricDataTypes.SYNC_TYPE)) {
					if (syncType != SyncType.NONE) {
						syncTypes.add(syncType);
					}
				}
			}

			// Checks the config definition itself
			for (SyncType syncType : configDefinition.getData(FabricDataTypes.SYNC_TYPE)) {
				if (syncType != SyncType.NONE) {
					syncTypes.add(syncType);
				}
			}

			if (syncTypes.isEmpty()) return null;

			forward = true;
			predicate = key -> !key.getData(FabricDataTypes.SYNC_TYPE).isEmpty();
		}

		ConfigSerializer<R> serializer = configDefinition.getSerializer();
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

		buf.writeString(configDefinition.toString());
		buf.writeString(configDefinition.getVersion().toString());
		buf.writeBoolean(forward);

		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			serializer.serialize(configDefinition, stream, valueContainer, predicate, true);
			byte[] bytes = stream.toByteArray();

			// If we didn't actually serialize anything, don't bother sending
			if (bytes.length > 0) {
				buf.writeByteArray(bytes);
				return buf;
			}
		} catch (IOException e) {
			LOGGER.error("Failed to sync config '{}': {}", configDefinition, e.getMessage());
		}

		return null;
	}

	public static <R> void sendConfigValues(String configDefinition, ServerPlayerEntity except, PacketByteBuf buf) {
		PacketByteBuf peerBuf = new PacketByteBuf(Unpooled.buffer());

		peerBuf.writeUuid(except.getUuid());
		peerBuf.writeBytes(buf);

		((ConfigValueSender) except.server).send(configDefinition, except, peerBuf);
	}

	public static void sendConfigValues(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient client) {
		if (!client.isIntegratedServerRunning() && client.getCurrentServerEntry() == null) return;
		ValueContainer valueContainer = ValueContainer.ROOT;

		for (ConfigDefinition<?> configDefinition : ConfigManager.getConfigKeys()) {
			sendToServer(configDefinition, valueContainer);
		}
	}
}
