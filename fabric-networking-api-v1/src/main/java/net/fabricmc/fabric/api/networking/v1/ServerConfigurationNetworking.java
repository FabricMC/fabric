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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.PacketByteBuf;
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
@ApiStatus.Experimental
public final class ServerConfigurationNetworking {
	/**
	 * Registers a handler to a channel.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The handler runs on the network thread. After reading the buffer there, the server
	 * must be modified in the server thread by calling {@link ThreadExecutor#execute(Runnable)}.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterReceiver(ServerConfigurationNetworkHandler, Identifier)} to unregister the existing handler.
	 *
	 * <p>For new code, {@link #registerGlobalReceiver(PacketType, ConfigurationPacketHandler)}
	 * is preferred, as it is designed in a way that prevents thread safety issues.
	 *
	 * @param channelName the id of the channel
	 * @param channelHandler the handler
	 * @return false if a handler is already registered to the channel
	 * @see ServerConfigurationNetworking#unregisterGlobalReceiver(Identifier)
	 * @see ServerConfigurationNetworking#registerReceiver(ServerConfigurationNetworkHandler, Identifier, ConfigurationChannelHandler)
	 */
	public static boolean registerGlobalReceiver(Identifier channelName, ConfigurationChannelHandler channelHandler) {
		return ServerNetworkingImpl.CONFIGURATION.registerGlobalReceiver(channelName, channelHandler);
	}

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
	public static <T extends FabricPacket> boolean registerGlobalReceiver(PacketType<T> type, ConfigurationPacketHandler<T> handler) {
		return registerGlobalReceiver(type.getId(), new ConfigurationChannelHandlerProxy<T>() {
			@Override
			public ConfigurationPacketHandler<T> getOriginalHandler() {
				return handler;
			}

			@Override
			public void receive(MinecraftServer server, ServerConfigurationNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
				T packet = type.read(buf);
				handler.receive(packet, networkHandler, sender);
			}
		});
	}

	/**
	 * Removes the handler of a channel.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The {@code channel} is guaranteed not to have a handler after this call.
	 *
	 * @param channelName the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 * @see ServerConfigurationNetworking#registerGlobalReceiver(Identifier, ConfigurationChannelHandler)
	 * @see ServerConfigurationNetworking#unregisterReceiver(ServerConfigurationNetworkHandler, Identifier)
	 */
	@Nullable
	public static ServerConfigurationNetworking.ConfigurationChannelHandler unregisterGlobalReceiver(Identifier channelName) {
		return ServerNetworkingImpl.CONFIGURATION.unregisterGlobalReceiver(channelName);
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
	@SuppressWarnings("unchecked")
	public static <T extends FabricPacket> ServerConfigurationNetworking.ConfigurationPacketHandler<T> unregisterGlobalReceiver(PacketType<T> type) {
		ConfigurationChannelHandler handler = ServerNetworkingImpl.CONFIGURATION.unregisterGlobalReceiver(type.getId());
		return handler instanceof ConfigurationChannelHandlerProxy<?> proxy ? (ConfigurationPacketHandler<T>) proxy.getOriginalHandler() : null;
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
	 * Registers a handler to a channel.
	 * This method differs from {@link ServerConfigurationNetworking#registerGlobalReceiver(Identifier, ConfigurationChannelHandler)} since
	 * the channel handler will only be applied to the client represented by the {@link ServerConfigurationNetworkHandler}.
	 *
	 * <p>The handler runs on the network thread. After reading the buffer there, the world
	 * must be modified in the server thread by calling {@link ThreadExecutor#execute(Runnable)}.
	 *
	 * <p>For example, if you only register a receiver using this method when a {@linkplain ServerLoginNetworking#registerGlobalReceiver(Identifier, ServerLoginNetworking.LoginQueryResponseHandler)}
	 * login response has been received, you should use {@link ServerPlayConnectionEvents#INIT} to register the channel handler.
	 *
	 * <p>If a handler is already registered to the {@code channelName}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterReceiver(ServerConfigurationNetworkHandler, Identifier)} to unregister the existing handler.
	 *
	 * <p>For new code, {@link #registerReceiver(ServerConfigurationNetworkHandler, PacketType, ConfigurationPacketHandler)}
	 * is preferred, as it is designed in a way that prevents thread safety issues.
	 *
	 * @param networkHandler the handler
	 * @param channelName the id of the channel
	 * @param channelHandler the handler
	 * @return false if a handler is already registered to the channel name
	 * @see ServerPlayConnectionEvents#INIT
	 */
	public static boolean registerReceiver(ServerConfigurationNetworkHandler networkHandler, Identifier channelName, ConfigurationChannelHandler channelHandler) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ServerNetworkingImpl.getAddon(networkHandler).registerChannel(channelName, channelHandler);
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
	public static <T extends FabricPacket> boolean registerReceiver(ServerConfigurationNetworkHandler networkHandler, PacketType<T> type, ConfigurationPacketHandler<T> handler) {
		return registerReceiver(networkHandler, type.getId(), new ConfigurationChannelHandlerProxy<T>() {
			@Override
			public ConfigurationPacketHandler<T> getOriginalHandler() {
				return handler;
			}

			@Override
			public void receive(MinecraftServer server, ServerConfigurationNetworkHandler networkHandler2, PacketByteBuf buf, PacketSender sender) {
				T packet = type.read(buf);
				handler.receive(packet, networkHandler2, sender);
			}
		});
	}

	/**
	 * Removes the handler of a channel.
	 *
	 * <p>The {@code channelName} is guaranteed not to have a handler after this call.
	 *
	 * @param channelName the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel name
	 */
	@Nullable
	public static ServerConfigurationNetworking.ConfigurationChannelHandler unregisterReceiver(ServerConfigurationNetworkHandler networkHandler, Identifier channelName) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ServerNetworkingImpl.getAddon(networkHandler).unregisterChannel(channelName);
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
	@SuppressWarnings("unchecked")
	public static <T extends FabricPacket> ServerConfigurationNetworking.ConfigurationPacketHandler<T> unregisterReceiver(ServerConfigurationNetworkHandler networkHandler, PacketType<T> type) {
		ConfigurationChannelHandler handler = unregisterReceiver(networkHandler, type.getId());
		return handler instanceof ConfigurationChannelHandlerProxy<?> proxy ? (ConfigurationPacketHandler<T>) proxy.getOriginalHandler() : null;
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
	public static boolean canSend(ServerConfigurationNetworkHandler handler, PacketType<?> type) {
		Objects.requireNonNull(handler, "Server configuration network handler cannot be null");
		Objects.requireNonNull(type, "Packet type cannot be null");

		return ServerNetworkingImpl.getAddon(handler).getSendableChannels().contains(type.getId());
	}

	/**
	 * Creates a packet which may be sent to a connected client.
	 *
	 * @param channelName the channel name
	 * @param buf the packet byte buf which represents the payload of the packet
	 * @return a new packet
	 */
	public static Packet<ClientCommonPacketListener> createS2CPacket(Identifier channelName, PacketByteBuf buf) {
		Objects.requireNonNull(channelName, "Channel cannot be null");
		Objects.requireNonNull(buf, "Buf cannot be null");

		return ServerNetworkingImpl.createS2CPacket(channelName, buf);
	}

	/**
	 * Creates a packet which may be sent to a connected client.
	 *
	 * @param packet the fabric packet
	 * @return a new packet
	 */
	public static <T extends FabricPacket> Packet<ClientCommonPacketListener> createS2CPacket(T packet) {
		Objects.requireNonNull(packet, "Packet cannot be null");
		Objects.requireNonNull(packet.getType(), "Packet#getType cannot return null");

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
	 * @param handler the handler to send the packet to
	 * @param channelName the channel of the packet
	 * @param buf the payload of the packet.
	 */
	public static void send(ServerConfigurationNetworkHandler handler, Identifier channelName, PacketByteBuf buf) {
		Objects.requireNonNull(handler, "Server configuration entity cannot be null");
		Objects.requireNonNull(channelName, "Channel name cannot be null");
		Objects.requireNonNull(buf, "Packet byte buf cannot be null");

		handler.sendPacket(createS2CPacket(channelName, buf));
	}

	/**
	 * Sends a packet to a configuring player.
	 *
	 * @param handler the network handler to send the packet to
	 * @param packet the packet
	 */
	public static <T extends FabricPacket> void send(ServerConfigurationNetworkHandler handler, T packet) {
		Objects.requireNonNull(handler, "Server configuration handler cannot be null");
		Objects.requireNonNull(packet, "Packet cannot be null");
		Objects.requireNonNull(packet.getType(), "Packet#getType cannot return null");

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

	@FunctionalInterface
	public interface ConfigurationChannelHandler {
		/**
		 * Handles an incoming packet.
		 *
		 * <p>This method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
		 * Modification to the game should be {@linkplain ThreadExecutor#submit(Runnable) scheduled} using the server instance from {@link ServerConfigurationNetworking#getServer(ServerConfigurationNetworkHandler)}.
		 *
		 * <p>An example usage of this is:
		 * <pre>{@code
		 * ServerConfigurationNetworking.registerReceiver(new Identifier("mymod", "boom"), (server, handler, buf, responseSender) -> {
		 * 	boolean fire = buf.readBoolean();
		 *
		 * 	// All operations on the server must be executed on the server thread
		 * 	server.execute(() -> {
		 *
		 * 	});
		 * });
		 * }</pre>
		 * @param server the server
		 * @param handler the network handler that received this packet, representing the client who sent the packet
		 * @param buf the payload of the packet
		 * @param responseSender the packet sender
		 */
		void receive(MinecraftServer server, ServerConfigurationNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
	}

	/**
	 * An internal packet handler that works as a proxy between old and new API.
	 * @param <T> the type of the packet
	 */
	private interface ConfigurationChannelHandlerProxy<T extends FabricPacket> extends ConfigurationChannelHandler {
		ConfigurationPacketHandler<T> getOriginalHandler();
	}

	/**
	 * A thread-safe packet handler utilizing {@link FabricPacket}.
	 * @param <T> the type of the packet
	 */
	@FunctionalInterface
	public interface ConfigurationPacketHandler<T extends FabricPacket> {
		/**
		 * Handles an incoming packet.
		 *
		 * <p>Unlike {@link ServerPlayNetworking.PlayPacketHandler} this method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
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
		 * @param packet the packet
		 * @param networkHandler the network handler
		 * @param responseSender the packet sender
		 * @see FabricPacket
		 */
		void receive(T packet, ServerConfigurationNetworkHandler networkHandler, PacketSender responseSender);
	}
}
