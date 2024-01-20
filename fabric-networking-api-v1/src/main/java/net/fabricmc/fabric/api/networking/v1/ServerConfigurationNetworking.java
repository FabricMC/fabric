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

package net.fabricmc.fabric.api.networking.v1;

import java.util.Objects;
import java.util.Set;

import net.minecraft.network.packet.CustomPayload;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.listener.ClientCommonPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

import net.fabricmc.fabric.impl.networking.server.ServerNetworkingImpl;
import net.fabricmc.fabric.mixin.networking.accessor.ServerCommonNetworkHandlerAccessor;

/**
 * Offers access to configuration stage server-side networking functionalities.
 *
 * <p>Server-side networking functionalities include receiving serverbound packets, sending clientbound packets, and events related to server-side network handlers.
 *
 * <p>This class should be only used for the logical server.
 *
 * <p>See {@link ServerPlayNetworking} for information on how to use the packet
 * object-based API.
 *
 * <p>See the documentation on each class for more information.
 *
 * @see ServerLoginNetworking
 * @see ServerConfigurationNetworking
 */
public final class ServerConfigurationNetworking {
	/**
	 * Registers a handler for a packet type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterReceiver(ServerConfigurationNetworkHandler, PacketType)} to unregister the existing handler.
	 *
	 * @param type the packet type
	 * @param handler the handler
	 * @return {@code false} if a handler is already registered to the channel
	 * @see ServerConfigurationNetworking#unregisterGlobalReceiver(PacketType)
	 * @see ServerConfigurationNetworking#registerReceiver(ServerConfigurationNetworkHandler, PacketType, ConfigurationPacketHandler)
	 */
	public static <T extends CustomPayload> boolean registerGlobalReceiver(CustomPayload.Id<T> type, ConfigurationPacketHandler<T> handler) {
		return ServerNetworkingImpl.CONFIGURATION.registerGlobalReceiver(type.id(), handler);
	}

	/**
	 * Removes the handler for a packet type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param type the packet type
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerGlobalReceiver(PacketType, ConfigurationPacketHandler)}
	 * @see ServerConfigurationNetworking#registerGlobalReceiver(PacketType, ConfigurationPacketHandler)
	 * @see ServerConfigurationNetworking#unregisterReceiver(ServerConfigurationNetworkHandler, PacketType)
	 */
	@Nullable
	public static <T extends CustomPayload> ServerConfigurationNetworking.ConfigurationPacketHandler<T> unregisterGlobalReceiver(CustomPayload.Id<T> type) {
		return (ConfigurationPacketHandler<T>) ServerNetworkingImpl.CONFIGURATION.unregisterGlobalReceiver(type.id());
	}

	/**
	 * Gets all channel names which global receivers are registered for.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * @return all channel names which global receivers are registered for.
	 */
	public static Set<Identifier> getGlobalReceivers() {
		return ServerNetworkingImpl.CONFIGURATION.getChannels();
	}

	/**
	 * Registers a handler for a packet type.
	 * This method differs from {@link ServerConfigurationNetworking#registerGlobalReceiver(PacketType, ConfigurationPacketHandler)} since
	 * the channel handler will only be applied to the client represented by the {@link ServerConfigurationNetworkHandler}.
	 *
	 * <p>For example, if you only register a receiver using this method when a {@linkplain ServerLoginNetworking#registerGlobalReceiver(Identifier, ServerLoginNetworking.LoginQueryResponseHandler)}
	 * login response has been received, you should use {@link ServerPlayConnectionEvents#INIT} to register the channel handler.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterReceiver(ServerConfigurationNetworkHandler, PacketType)} to unregister the existing handler.
	 *
	 * @param networkHandler the network handler
	 * @param type the packet type
	 * @param handler the handler
	 * @return {@code false} if a handler is already registered to the channel name
	 * @see ServerPlayConnectionEvents#INIT
	 */
	public static <T extends CustomPayload> boolean registerReceiver(ServerConfigurationNetworkHandler networkHandler, CustomPayload.Id<T> type, ConfigurationPacketHandler<T> handler) {
		return ServerNetworkingImpl.getAddon(networkHandler).registerChannel(type.id(), handler);
	}

	/**
	 * Removes the handler for a packet type.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param type the type of the packet
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerReceiver(ServerConfigurationNetworkHandler, PacketType, ConfigurationPacketHandler)}
	 */
	@Nullable
	public static <T extends CustomPayload> ServerConfigurationNetworking.ConfigurationPacketHandler<T> unregisterReceiver(ServerConfigurationNetworkHandler networkHandler, CustomPayload.Id<T> type) {
		return (ConfigurationPacketHandler<T>) ServerNetworkingImpl.getAddon(networkHandler).unregisterChannel(type.id());
	}

	/**
	 * Gets all the channel names that the server can receive packets on.
	 *
	 * @param handler the network handler
	 * @return All the channel names that the server can receive packets on
	 */
	public static Set<Identifier> getReceived(ServerConfigurationNetworkHandler handler) {
		Objects.requireNonNull(handler, "Server configuration network handler cannot be null");

		return ServerNetworkingImpl.getAddon(handler).getReceivableChannels();
	}

	/**
	 * Gets all channel names that a connected client declared the ability to receive a packets on.
	 *
	 * @param handler the network handler
	 * @return {@code true} if the connected client has declared the ability to receive a packet on the specified channel
	 */
	public static Set<Identifier> getSendable(ServerConfigurationNetworkHandler handler) {
		Objects.requireNonNull(handler, "Server configuration network handler cannot be null");

		return ServerNetworkingImpl.getAddon(handler).getSendableChannels();
	}

	/**
	 * Checks if the connected client declared the ability to receive a packet on a specified channel name.
	 *
	 * @param handler the network handler
	 * @param channelName the channel name
	 * @return {@code true} if the connected client has declared the ability to receive a packet on the specified channel
	 */
	public static boolean canSend(ServerConfigurationNetworkHandler handler, Identifier channelName) {
		Objects.requireNonNull(handler, "Server configuration network handler cannot be null");
		Objects.requireNonNull(channelName, "Channel name cannot be null");

		return ServerNetworkingImpl.getAddon(handler).getSendableChannels().contains(channelName);
	}

	/**
	 * Checks if the connected client declared the ability to receive a specific type of packet.
	 *
	 * @param handler the network handler
	 * @param type the packet type
	 * @return {@code true} if the connected client has declared the ability to receive a specific type of packet
	 */
	public static boolean canSend(ServerConfigurationNetworkHandler handler, CustomPayload.Id<?> type) {
		Objects.requireNonNull(handler, "Server configuration network handler cannot be null");
		Objects.requireNonNull(type, "Packet type cannot be null");

		return ServerNetworkingImpl.getAddon(handler).getSendableChannels().contains(type.id());
	}

	/**
	 * Creates a packet which may be sent to a connected client.
	 *
	 * @param packet the fabric packet
	 * @return a new packet
	 */
	public static <T extends CustomPayload> Packet<ClientCommonPacketListener> createS2CPacket(T packet) {
		Objects.requireNonNull(packet, "Packet cannot be null");
		Objects.requireNonNull(packet.getId(), "Packet#getType cannot return null");

		return ServerNetworkingImpl.createS2CPacket(packet);
	}

	/**
	 * Gets the packet sender which sends packets to the connected client.
	 *
	 * @param handler the network handler, representing the connection to the player/client
	 * @return the packet sender
	 */
	public static PacketSender getSender(ServerConfigurationNetworkHandler handler) {
		Objects.requireNonNull(handler, "Server configuration network handler cannot be null");

		return ServerNetworkingImpl.getAddon(handler);
	}

	/**
	 * Sends a packet to a configuring player.
	 *
	 * @param handler the network handler to send the packet to
	 * @param packet the packet
	 */
	public static <T extends CustomPayload> void send(ServerConfigurationNetworkHandler handler, T packet) {
		Objects.requireNonNull(handler, "Server configuration handler cannot be null");
		Objects.requireNonNull(packet, "Packet cannot be null");
		Objects.requireNonNull(packet.getId(), "Packet#getType cannot return null");

		handler.sendPacket(createS2CPacket(packet));
	}

	// Helper methods

	/**
	 * Returns the <i>Minecraft</i> Server of a server configuration network handler.
	 *
	 * @param handler the server configuration network handler
	 */
	public static MinecraftServer getServer(ServerConfigurationNetworkHandler handler) {
		Objects.requireNonNull(handler, "Network handler cannot be null");

		return ((ServerCommonNetworkHandlerAccessor) handler).getServer();
	}

	private ServerConfigurationNetworking() {
	}

	/**
	 * A thread-safe packet handler utilizing {@link CustomPayload}.
	 * @param <T> the type of the packet
	 */
	@FunctionalInterface
	public interface ConfigurationPacketHandler<T extends CustomPayload> {
		/**
		 * Handles an incoming packet.
		 *
		 * <p>Unlike {@link ServerPlayNetworking.PlayPayloadHandler} this method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
		 * Modification to the game should be {@linkplain ThreadExecutor#submit(Runnable) scheduled} using the Minecraft server instance from {@link ServerConfigurationNetworking#getServer(ServerConfigurationNetworkHandler)}.
		 *
		 * <p>An example usage of this:
		 * <pre>{@code
		 * // See FabricPacket for creating the packet
		 * ServerConfigurationNetworking.registerReceiver(BOOM_PACKET_TYPE, (packet, responseSender) -> {
		 *
		 * });
		 * }</pre>
		 *
		 *
		 * @param payload the packet payload
		 * @param networkHandler the network handler
		 * @param responseSender the packet sender
		 * @see CustomPayload
		 */
		void receive(T payload, ServerConfigurationNetworkHandler networkHandler, PacketSender responseSender);
	}
}
