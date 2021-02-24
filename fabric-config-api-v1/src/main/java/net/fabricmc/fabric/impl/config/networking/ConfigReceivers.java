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
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
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
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;

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
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.mixin.config.GameProfileAccessor;

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
			ConfigSenders.sendConfigValues(server, result.configDefinitionString, sender.getUuid(), buf);
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
				SemanticVersion localVersion = configDefinition.getVersion();

				if (userVersion.getVersionComponent(0) != localVersion.getVersionComponent(0)) {
					// We require that the sides have matching config versions at least in the major component
					disconnector.config_disconnect(new TranslatableText("fabric.config.invalid_version",
							configDefinition.toString(),
							userVersionString,
							localVersion.toString()));

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

	public static <R> void receiveConfigValues(MinecraftServer server, ServerLoginNetworkHandler handler, boolean b, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer loginSynchronizer, PacketSender packetSender) {
		UUID senderId = ((GameProfileAccessor) handler).getProfile().getId();
		receiveConfigValues(server, (Disconnector) handler, senderId, buf);
	}

	private static <R> void receiveConfigValues(MinecraftServer server, Disconnector disconnector, UUID senderId, PacketByteBuf buf) {
		buf.markReaderIndex();

		Result result = read(buf, saveType -> {
			ValueContainerProvider provider = ValueContainerProviders.getInstance(saveType);
			return saveType == FabricSaveTypes.USER
					? provider.getPlayerValueContainer(senderId)
					: provider.getValueContainer();
		}, disconnector);

		ConfigDefinition<R> configDefinition = ConfigManager.getDefinition(result.configDefinitionString);

		if (configDefinition != null && configDefinition.getSaveType() == FabricSaveTypes.LEVEL) {
			ConfigSenders.sendToPlayers(server, configDefinition, result.valueContainer);
		}

		if (result.forward) {
			buf.resetReaderIndex();
			ConfigSenders.sendConfigValues(server, result.configDefinitionString, senderId, buf);
		}
	}

	@Environment(EnvType.CLIENT)
	public static CompletableFuture<PacketByteBuf> receiveLevelConfigValues(MinecraftClient client, ClientLoginNetworkHandler handler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> genericFutureListenerConsumer) {
		ValueContainer container = ValueContainerProviders.getInstance(FabricSaveTypes.LEVEL).getValueContainer();

		Mutable<Text> text = new MutableObject<>();

		read(buf, s -> container, text::setValue);

		PacketByteBuf response = new PacketByteBuf(Unpooled.buffer());
		response.writeBoolean(text.getValue() == null);

		if (text.getValue() != null) {
			response.writeText(text.getValue());
		}

		return CompletableFuture.completedFuture(response);
	}

	public static <R> CompletableFuture<PacketByteBuf> receiveUserConfigValues(MinecraftClient client, ClientLoginNetworkHandler handler, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> genericFutureListenerConsumer) {
		ConfigDefinition<R> configDefinition = ConfigManager.getDefinition(buf.readString());

		if (configDefinition == null) return CompletableFuture.completedFuture(null);

		PacketByteBuf response = new PacketByteBuf(Unpooled.buffer());

		try {
			SemanticVersion version = SemanticVersion.parse(buf.readString());

			if (version.getVersionComponent(0) != configDefinition.getVersion().getVersionComponent(0)) {
				response.writeBoolean(false);
				response.writeText(new TranslatableText("fabric.config.invalid_version",
						configDefinition.toString(),
						configDefinition.getVersion(),
						version.toString()));
			} else {
				response.writeBoolean(true);
				response.writeBytes(ConfigSenders.toPacket(configDefinition, ValueContainer.ROOT));
			}
		} catch (VersionParsingException e) {
			return CompletableFuture.completedFuture(null);
		}

		return CompletableFuture.completedFuture(response);
	}

	public static void handleLevelSyncResponse(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer loginSynchronizer, PacketSender packetSender) {
		if (!understood) return;
		checkSuccess(server, buf, handler);
	}

	public static void handleUserSyncResponse(MinecraftServer server, ServerLoginNetworkHandler handler, boolean understood, PacketByteBuf buf, ServerLoginNetworking.LoginSynchronizer loginSynchronizer, PacketSender packetSender) {
		if (!understood || !checkSuccess(server, buf, handler)) return;

		UUID senderId = ((GameProfileAccessor) handler).getProfile().getId();
		ValueContainer container = ValueContainerProviders.getInstance(FabricSaveTypes.USER).getPlayerValueContainer(senderId);

		read(buf, s -> container, (Disconnector) handler);
	}

	private static boolean checkSuccess(MinecraftServer server, PacketByteBuf buf, ServerLoginNetworkHandler handler) {
		boolean success = buf.readBoolean();

		if (!success) {
			Text text = buf.readText();

			// Disconnect the user if they request it.
			// This will happen if the config versions major component don't match.
			server.execute(() -> handler.disconnect(text));
		}

		return success;
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
