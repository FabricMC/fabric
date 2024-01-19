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

package net.fabricmc.fabric.api.client.networking.v1;

import java.util.Objects;
import java.util.Set;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;

import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.impl.networking.client.ClientConfigurationNetworkAddon;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;

/**
 * Offers access to configuration stage client-side networking functionalities.
 *
 * <p>Client-side networking functionalities include receiving clientbound packets,
 * sending serverbound packets, and events related to client-side network handlers.
 *
 * <p>This class should be only used on the physical client and for the logical client.
 *
 * <p>See {@link net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking} for information on how to use the packet
 * object-based API.
 *
 * @see ServerConfigurationNetworking
 */
public final class ClientConfigurationNetworking {
	/**
	 * Registers a handler for a packet type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterGlobalReceiver(PacketType)} to unregister the existing handler.
	 *
	 * @param type the packet type
	 * @param handler the handler
	 * @return false if a handler is already registered to the channel
	 * @see ClientConfigurationNetworking#unregisterGlobalReceiver(PacketType)
	 * @see ClientConfigurationNetworking#registerReceiver(PacketType, ConfigurationPayloadHandler)
	 */
	public static <T extends CustomPayload> boolean registerGlobalReceiver(CustomPayload.Id<T> type, ConfigurationPayloadHandler<T> handler) {
		return ClientNetworkingImpl.CONFIGURATION.registerGlobalReceiver(type.id(), handler);
	}

	/**
	 * Removes the handler for a packet type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param type the packet type
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerGlobalReceiver(PacketType, ConfigurationPayloadHandler)}
	 * @see ClientConfigurationNetworking#registerGlobalReceiver(PacketType, ConfigurationPayloadHandler)
	 * @see ClientConfigurationNetworking#unregisterReceiver(PacketType)
	 */
	@Nullable
	public static <T extends CustomPayload> ClientConfigurationNetworking.ConfigurationPayloadHandler<T> unregisterGlobalReceiver(CustomPayload.Type<PacketByteBuf, T> type) {
		return (ConfigurationPayloadHandler<T>) ClientNetworkingImpl.CONFIGURATION.unregisterGlobalReceiver(type.id().id());
	}

	/**
	 * Gets all channel names which global receivers are registered for.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * @return all channel names which global receivers are registered for.
	 */
	public static Set<Identifier> getGlobalReceivers() {
		return ClientNetworkingImpl.CONFIGURATION.getChannels();
	}

	/**
	 * Registers a handler for a packet type.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterReceiver(PacketType)} to unregister the existing handler.
	 *
	 * <p>For example, if you only register a receiver using this method when a {@linkplain ClientLoginNetworking#registerGlobalReceiver(Identifier, ClientLoginNetworking.LoginQueryRequestHandler)}
	 * login query has been received, you should use {@link ClientPlayConnectionEvents#INIT} to register the channel handler.
	 *
	 * @param type the packet type
	 * @param handler the handler
	 * @return {@code false} if a handler is already registered for the type
	 * @throws IllegalStateException if the client is not connected to a server
	 * @see ClientPlayConnectionEvents#INIT
	 */
	public static <T extends CustomPayload> boolean registerReceiver(CustomPayload.Type<PacketByteBuf, T> type, ConfigurationPayloadHandler<T> handler) {
		final ClientConfigurationNetworkAddon addon = ClientNetworkingImpl.getClientConfigurationAddon();

		if (addon != null) {
			return addon.registerChannel(type.id().id(), handler);
		}

		throw new IllegalStateException("Cannot register receiver while not configuring!");
	}

	/**
	 * Removes the handler for a packet type.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param type the packet type
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerReceiver(PacketType, ConfigurationPayloadHandler)}
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	@Nullable
	public static <T extends CustomPayload> ClientConfigurationNetworking.ConfigurationPayloadHandler<T> unregisterReceiver(CustomPayload.Id<T> type) {
		final ClientConfigurationNetworkAddon addon = ClientNetworkingImpl.getClientConfigurationAddon();

		if (addon != null) {
			return (ConfigurationPayloadHandler<T>) addon.unregisterChannel(type.id());
		}

		throw new IllegalStateException("Cannot unregister receiver while not configuring!");
	}

	/**
	 * Gets all the channel names that the client can receive packets on.
	 *
	 * @return All the channel names that the client can receive packets on
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static Set<Identifier> getReceived() throws IllegalStateException {
		final ClientConfigurationNetworkAddon addon = ClientNetworkingImpl.getClientConfigurationAddon();

		if (addon != null) {
			return addon.getReceivableChannels();
		}

		throw new IllegalStateException("Cannot get a list of channels the client can receive packets on while not configuring!");
	}

	/**
	 * Gets all channel names that the connected server declared the ability to receive a packets on.
	 *
	 * @return All the channel names the connected server declared the ability to receive a packets on
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static Set<Identifier> getSendable() throws IllegalStateException {
		final ClientConfigurationNetworkAddon addon = ClientNetworkingImpl.getClientConfigurationAddon();

		if (addon != null) {
			return addon.getSendableChannels();
		}

		throw new IllegalStateException("Cannot get a list of channels the server can receive packets on while not configuring!");
	}

	/**
	 * Checks if the connected server declared the ability to receive a packet on a specified channel name.
	 *
	 * @param channelName the channel name
	 * @return {@code true} if the connected server has declared the ability to receive a packet on the specified channel.
	 * False if the client is not in game.
	 */
	public static boolean canSend(Identifier channelName) throws IllegalArgumentException {
		final ClientConfigurationNetworkAddon addon = ClientNetworkingImpl.getClientConfigurationAddon();

		if (addon != null) {
			return addon.getSendableChannels().contains(channelName);
		}

		throw new IllegalStateException("Cannot get a list of channels the server can receive packets on while not configuring!");
	}

	/**
	 * Checks if the connected server declared the ability to receive a packet on a specified channel name.
	 * This returns {@code false} if the client is not in game.
	 *
	 * @param type the packet type
	 * @return {@code true} if the connected server has declared the ability to receive a packet on the specified channel
	 */
	public static boolean canSend(CustomPayload.Id<?> type) {
		return canSend(type.id());
	}


	/**
	 * Gets the packet sender which sends packets to the connected server.
	 *
	 * @return the client's packet sender
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static PacketSender getSender() throws IllegalStateException {
		final ClientConfigurationNetworkAddon addon = ClientNetworkingImpl.getClientConfigurationAddon();

		if (addon != null) {
			return addon;
		}

		throw new IllegalStateException("Cannot get PacketSender while not configuring!");
	}

	/**
	 * Sends a packet to the connected server.
	 *
	 * @param packet the packet
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static <T extends CustomPayload> void send(T packet) {
		Objects.requireNonNull(packet, "Packet cannot be null");
		Objects.requireNonNull(packet.getId(), "Packet#getType cannot return null");

		final ClientConfigurationNetworkAddon addon = ClientNetworkingImpl.getClientConfigurationAddon();

		if (addon != null) {
			addon.sendPacket(packet);
			return;
		}

		throw new IllegalStateException("Cannot send packet while not configuring!");
	}

	private ClientConfigurationNetworking() {
	}

	/**
	 * A thread-safe packet handler utilizing {@link CustomPayload}.
	 * @param <T> the type of the packet
	 */
	@FunctionalInterface
	public interface ConfigurationPayloadHandler<T extends CustomPayload> {
		/**
		 * Handles the incoming packet. This is called on the render thread, and can safely
		 * call client methods.
		 *
		 * <p>An example usage of this is to display an overlay message:
		 * <pre>{@code
		 * // See FabricPacket for creating the packet
		 * ClientConfigurationNetworking.registerReceiver(OVERLAY_PACKET_TYPE, (player, packet, responseSender) -> {
		 * 	MinecraftClient.getInstance().inGameHud.setOverlayMessage(packet.message(), true);
		 * });
		 * }</pre>
		 *
		 *
		 * @param payload the packet payload
		 * @param responseSender the packet sender
		 * @see CustomPayload
		 */
		void receive(T payload, PacketSender responseSender);
	}
}
