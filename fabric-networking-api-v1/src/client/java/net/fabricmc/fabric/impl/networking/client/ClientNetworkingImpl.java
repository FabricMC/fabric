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

package net.fabricmc.fabric.impl.networking.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.fabricmc.fabric.impl.networking.GlobalReceiverRegistry;
import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.payload.PacketByteBufPayload;
import net.fabricmc.fabric.mixin.networking.client.accessor.ClientLoginNetworkHandlerAccessor;
import net.fabricmc.fabric.mixin.networking.client.accessor.ConnectScreenAccessor;
import net.fabricmc.fabric.mixin.networking.client.accessor.MinecraftClientAccessor;

public final class ClientNetworkingImpl {
	public static final GlobalReceiverRegistry<ClientLoginNetworking.LoginQueryRequestHandler> LOGIN = new GlobalReceiverRegistry<>();
	public static final GlobalReceiverRegistry<ClientConfigurationNetworking.ConfigurationChannelHandler> CONFIGURATION = new GlobalReceiverRegistry<>();
	public static final GlobalReceiverRegistry<ClientPlayNetworking.PlayChannelHandler> PLAY = new GlobalReceiverRegistry<>();
	private static ClientPlayNetworkAddon currentPlayAddon;
	private static ClientConfigurationNetworkAddon currentConfigurationAddon;

	public static ClientPlayNetworkAddon getAddon(ClientPlayNetworkHandler handler) {
		return (ClientPlayNetworkAddon) ((NetworkHandlerExtensions) handler).getAddon();
	}

	public static ClientLoginNetworkAddon getAddon(ClientLoginNetworkHandler handler) {
		return (ClientLoginNetworkAddon) ((NetworkHandlerExtensions) handler).getAddon();
	}

	public static Packet<ServerCommonPacketListener> createC2SPacket(Identifier channelName, PacketByteBuf buf) {
		return new CustomPayloadC2SPacket(new PacketByteBufPayload(channelName, buf));
	}

	/**
	 * Due to the way logging into a integrated or remote dedicated server will differ, we need to obtain the login client connection differently.
	 */
	@Nullable
	public static ClientConnection getLoginConnection() {
		final ClientConnection connection = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getConnection();

		// Check if we are connecting to an integrated server. This will set the field on MinecraftClient
		if (connection != null) {
			return connection;
		} else {
			// We are probably connecting to a remote server.
			// Check if the ConnectScreen is the currentScreen to determine that:
			if (MinecraftClient.getInstance().currentScreen instanceof ConnectScreen) {
				return ((ConnectScreenAccessor) MinecraftClient.getInstance().currentScreen).getConnection();
			}
		}

		// We are not connected to a server at all.
		return null;
	}

	@Nullable
	public static ClientConfigurationNetworkAddon getClientConfigurationAddon() {
		return currentConfigurationAddon;
	}

	@Nullable
	public static ClientPlayNetworkAddon getClientPlayAddon() {
		// Since Minecraft can be a bit weird, we need to check for the play addon in a few ways:
		// If the client's player is set this will work
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			currentPlayAddon = null; // Shouldn't need this anymore
			return getAddon(MinecraftClient.getInstance().getNetworkHandler());
		}

		// We haven't hit the end of onGameJoin yet, use our backing field here to access the network handler
		if (currentPlayAddon != null) {
			return currentPlayAddon;
		}

		// We are not in play stage
		return null;
	}

	public static void setClientPlayAddon(ClientPlayNetworkAddon addon) {
		assert addon == null || currentConfigurationAddon == null;
		currentPlayAddon = addon;
	}

	public static void setClientConfigurationAddon(ClientConfigurationNetworkAddon addon) {
		assert addon == null || currentPlayAddon == null;
		currentConfigurationAddon = addon;
	}

	public static void clientInit() {
		// Reference cleanup for the locally stored addon if we are disconnected
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			currentPlayAddon = null;
		});

		ClientConfigurationConnectionEvents.DISCONNECT.register((handler, client) -> {
			currentConfigurationAddon = null;
		});

		// Register a login query handler for early channel registration.
		ClientLoginNetworking.registerGlobalReceiver(NetworkingImpl.EARLY_REGISTRATION_CHANNEL, (client, handler, buf, listenerAdder) -> {
			int n = buf.readVarInt();
			List<Identifier> ids = new ArrayList<>(n);

			for (int i = 0; i < n; i++) {
				ids.add(buf.readIdentifier());
			}

			ClientConnection connection = ((ClientLoginNetworkHandlerAccessor) handler).getConnection();
			((ChannelInfoHolder) connection).getPendingChannelsNames().addAll(ids);
			NetworkingImpl.LOGGER.debug("Received accepted channels from the server");

			PacketByteBuf response = PacketByteBufs.create();
			Collection<Identifier> channels = ClientPlayNetworking.getGlobalReceivers();
			response.writeVarInt(channels.size());

			for (Identifier id : channels) {
				response.writeIdentifier(id);
			}

			NetworkingImpl.LOGGER.debug("Sent accepted channels to the server");
			return CompletableFuture.completedFuture(response);
		});
	}
}
