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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.config.v1.FabricSaveTypes;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.config.ConfigDefinition;
import net.fabricmc.loader.api.config.ConfigManager;
import net.fabricmc.loader.api.config.data.SaveType;
import net.fabricmc.loader.api.config.value.ValueContainer;
import net.fabricmc.loader.api.config.value.ValueContainerProvider;
import net.fabricmc.loader.config.ValueContainerProviders;

public class ConfigReceivers {
	private static final Logger LOGGER = LogManager.getLogger();

	@Environment(EnvType.CLIENT)
	static <R> void receiveUserConfigValues(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
		UUID user = buf.readUuid();
		ValueContainer container = ValueContainerProviders.getInstance(FabricSaveTypes.USER).getPlayerValueContainer(user);

		read(buf, s -> container, (Disconnector) clientPlayNetworkHandler);
	}

	@Environment(EnvType.CLIENT)
	static <R> void receiveLevelConfigValues(MinecraftClient client, ClientPlayNetworkHandler clientPlayNetworkHandler, PacketByteBuf buf, PacketSender packetSender) {
		ValueContainer container = ValueContainerProviders.getInstance(FabricSaveTypes.LEVEL).getValueContainer();
		read(buf, s -> container, (Disconnector) clientPlayNetworkHandler);
	}

	@Environment(EnvType.CLIENT)
	static <R> void receiveConfigValues(MinecraftServer server, ServerPlayerEntity sender, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
		buf.markReaderIndex();

		Result result = read(buf, saveType -> {
			ValueContainerProvider provider = ValueContainerProviders.getInstance(saveType);
			return saveType == FabricSaveTypes.USER
					? provider.getPlayerValueContainer(sender.getUuid())
					: provider.getValueContainer();
		}, ((Disconnector) handler));

		ConfigDefinition<R> configDefinition = ConfigManager.getDefinition(result.configDefinitionString);

		if (configDefinition != null && configDefinition.getSaveType() == FabricSaveTypes.LEVEL) {
			ConfigSenders.sendToPlayers(server, configDefinition, result.valueContainer);
		}

		if (result.forward) {
			buf.resetReaderIndex();
			ConfigSenders.sendConfigValues(result.configDefinitionString, sender, buf);
		}
	}

	private static <R> Result read(PacketByteBuf buf, Function<SaveType, ValueContainer> provider, Disconnector disconnector) {
		String configDefinitionString = buf.readString(32767);
		ConfigDefinition<R> configDefinition = ConfigManager.getDefinition(configDefinitionString);
		String userVersionString = buf.readString(32767);
		boolean forward = buf.readBoolean();

		if (configDefinition != null) {
			try {
				SemanticVersion userVersion = SemanticVersion.parse(userVersionString);
				SemanticVersion sentVersion = configDefinition.getVersion();

				if (userVersion.getVersionComponent(0) != sentVersion.getVersionComponent(0)) {
					// We require that the sides have matching config versions at least in the major component
					disconnector.config_disconnect(new TranslatableText("fabric.config.invalid_version",
							configDefinition.toString(),
							userVersionString,
							sentVersion.toString(),
							sentVersion.getVersionComponent(0)));

					// We'll also abort here to avoid sending useless info to other connected clients
					return new Result(forward, configDefinitionString, null);
				}
			} catch (VersionParsingException e) {
				disconnector.config_disconnect(new TranslatableText("fabric.config.version_parse", configDefinition.toString(), userVersionString));
				// We'll also abort here to avoid sending useless info to other connected clients
				// Ideally we'd be able to do this during the login phase so that clients don't finish connecting at all
				return new Result(forward, configDefinitionString, null);
			}

			SaveType saveType = configDefinition.getSaveType();

			if (saveType == FabricSaveTypes.USER || saveType == FabricSaveTypes.LEVEL) {
				InputStream inputStream = new ByteArrayInputStream(buf.readByteArray());
				ValueContainer valueContainer = provider.apply(saveType);

				try {
					configDefinition.getSerializer().deserialize(configDefinition, inputStream, valueContainer);
					return new Result(forward, configDefinitionString, valueContainer);
				} catch (IOException e) {
					LOGGER.error("Failed to sync config '{}': {}", configDefinition, e.getMessage());
				}
			}
		}

		return new Result(forward, configDefinitionString, null);
	}

	public static final class Result {
		public final boolean forward;
		public final String configDefinitionString;
		public final @Nullable ValueContainer valueContainer;

		public Result(boolean forward, String configDefinitionString, @Nullable ValueContainer valueContainer) {
			this.forward = forward;
			this.configDefinitionString = configDefinitionString;
			this.valueContainer = valueContainer;
		}
	}
}
