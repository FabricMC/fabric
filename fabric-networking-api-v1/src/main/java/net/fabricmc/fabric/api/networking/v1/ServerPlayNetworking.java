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

import java.util.Collection;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.networking.server.ServerNetworkingImpl;
import net.fabricmc.fabric.impl.networking.server.ServerPlayNetworkHandlerExtensions;

/**
 * Offers access to play stage server-side networking functionalities.
 *
 * <p>Server-side networking functionalities include receiving serverbound packets, sending clientbound packets, and events related to server-side network handlers.
 *
 * <p>This class should be only used for the logical server.
 *
 * @see ServerLoginNetworking
 * @see ClientPlayNetworking
 */
public final class ServerPlayNetworking {
	/**
	 * Registers a handler to a channel.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregister(ServerPlayNetworkHandler, Identifier)} to unregister the existing handler.</p>
	 *
	 * @param channelName the id of the channel
	 * @param channelHandler the handler
	 * @return false if a handler is already registered to the channel
	 * @see ServerPlayNetworking#unregisterGlobalReceiver(Identifier)
	 * @see ServerPlayNetworking#register(ServerPlayNetworkHandler, Identifier, PlayChannelHandler)
	 */
	public static boolean registerGlobalReceiver(Identifier channelName, PlayChannelHandler channelHandler) {
		return ServerNetworkingImpl.PLAY.registerGlobalReceiver(channelName, channelHandler);
	}

	/**
	 * Removes the handler of a channel.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The {@code channel} is guaranteed not to have a handler after this call.</p>
	 *
	 * @param channelName the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 * @see ServerPlayNetworking#registerGlobalReceiver(Identifier, PlayChannelHandler)
	 * @see ServerPlayNetworking#unregister(ServerPlayNetworkHandler, Identifier)
	 */
	@Nullable
	public static PlayChannelHandler unregisterGlobalReceiver(Identifier channelName) {
		return ServerNetworkingImpl.PLAY.unregisterGlobalReceiver(channelName);
	}

	/**
	 * Gets all channel names which global receivers are registered for.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * @return all channel names which global receivers are registered for.
	 */
	public static Collection<Identifier> getGlobalReceivers() {
		return ServerNetworkingImpl.PLAY.getChannels();
	}

	/**
	 * Checks if a channel of the specified name has a global receiver registered.
	 *
	 *
	 * @param channelName the channel name
	 * @return True if a channel of the specified name has a global receiver registered
	 */
	public static boolean hasGlobalReceiver(Identifier channelName) {
		return ServerNetworkingImpl.PLAY.hasChannel(channelName);
	}

	/**
	 * Registers a handler to a channel.
	 *
	 * <p>If a handler is already registered to the {@code channelName}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregister(ServerPlayNetworkHandler, Identifier)} to unregister the existing handler.</p>
	 *
	 * @param networkHandler the handler
	 * @param channelName the id of the channel
	 * @param channelHandler the handler
	 * @return false if a handler is already registered to the channel name
	 */
	public static boolean register(ServerPlayNetworkHandler networkHandler, Identifier channelName, PlayChannelHandler channelHandler) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ((ServerPlayNetworkHandlerExtensions) networkHandler).getAddon().registerChannel(channelName, channelHandler);
	}

	/**
	 * Removes the handler of a channel.
	 *
	 * <p>The {@code channelName} is guaranteed not to have a handler after this call.</p>
	 *
	 * @param channelName the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel name
	 */
	@Nullable
	public static PlayChannelHandler unregister(ServerPlayNetworkHandler networkHandler, Identifier channelName) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ServerNetworkingImpl.getAddon(networkHandler).unregisterChannel(channelName);
	}

	public static Collection<Identifier> getC2SReceivers(ServerPlayerEntity player) {
		Objects.requireNonNull(player, "Server player entity cannot be null");

		return getC2SReceivers(player.networkHandler);
	}

	public static Collection<Identifier> getC2SReceivers(ServerPlayNetworkHandler handler) {
		Objects.requireNonNull(handler, "Server play network handler cannot be null");

		return ServerNetworkingImpl.getAddon(handler).getReceivableChannels();
	}

	/**
	 * Checks if the server can receive packets on a specified channel name.
	 *
	 * @param player the network handler
	 * @param channelName the channel name
	 * @return True if the server can receive packets on a specified channel name.
	 */
	public static boolean canReceiveC2S(ServerPlayerEntity player, Identifier channelName) {
		Objects.requireNonNull(player, "Server player entity cannot be null");

		return canReceiveC2S(player.networkHandler, channelName);
	}

	/**
	 * Checks if the server can receive packets on a specified channel name.
	 *
	 * @param handler the network handler
	 * @param channelName the channel name
	 * @return True if the server can receive packets on a specified channel name.
	 */
	public static boolean canReceiveC2S(ServerPlayNetworkHandler handler, Identifier channelName) {
		Objects.requireNonNull(handler, "Server play network handler cannot be null");
		Objects.requireNonNull(channelName, "Channel name cannot be null");

		return ServerNetworkingImpl.getAddon(handler).hasReceivableChannel(channelName);
	}

	/**
	 * Gets all channel names that the connected client declared the ability to receive a packets on.
	 *
	 * @param player the player
	 * @return All the channel names the connected client declared the ability to receive a packets on
	 */
	public static Collection<Identifier> getS2CReceivers(ServerPlayerEntity player) {
		Objects.requireNonNull(player, "Server player entity cannot be null");

		return getS2CReceivers(player.networkHandler);
	}

	/**
	 * Gets all channel names that a the connected client declared the ability to receive a packets on.
	 *
	 * @param handler the network handler
	 * @return True if the connected client has declared the ability to receive a packet on the specified channel
	 */
	public static Collection<Identifier> getS2CReceivers(ServerPlayNetworkHandler handler) {
		Objects.requireNonNull(handler, "Server play network handler cannot be null");

		return ServerNetworkingImpl.getAddon(handler).getSendableChannels();
	}

	/**
	 * Checks if the connected client declared the ability to receive a packet on a specified channel name.
	 *
	 * @param player the player
	 * @param channelName the channel name
	 * @return True if the connected client has declared the ability to receive a packet on the specified channel
	 */
	public static boolean canReceiveS2C(ServerPlayerEntity player, Identifier channelName) {
		Objects.requireNonNull(player, "Server player entity cannot be null");

		return canReceiveS2C(player.networkHandler, channelName);
	}

	/**
	 * Checks if the connected client declared the ability to receive a packet on a specified channel name.
	 *
	 * @param handler the network handler
	 * @param channelName the channel name
	 * @return True if the connected client has declared the ability to receive a packet on the specified channel
	 */
	public static boolean canReceiveS2C(ServerPlayNetworkHandler handler, Identifier channelName) {
		Objects.requireNonNull(handler, "Server play network handler cannot be null");
		Objects.requireNonNull(channelName, "Channel name cannot be null");

		return ServerNetworkingImpl.getAddon(handler).hasSendableChannel(channelName);
	}

	/**
	 * Creates a packet which may be sent to a the connected client.
	 *
	 * @param channelName the channel name
	 * @param buf the packet byte buf which represents the payload of the packet
	 * @return a new packet
	 */
	public static Packet<?> createS2CPacket(Identifier channelName, PacketByteBuf buf) {
		Objects.requireNonNull(channelName, "Channel cannot be null");
		Objects.requireNonNull(buf, "Buf cannot be null");

		return ServerNetworkingImpl.createPlayC2SPacket(channelName, buf);
	}

	/**
	 * Sends a packet to a player.
	 *
	 * @param player the player to send the packet to
	 * @param channelName the channel of the packet
	 * @param buf the payload of the packet.
	 */
	public static void send(ServerPlayerEntity player, Identifier channelName, PacketByteBuf buf) {
		Objects.requireNonNull(player, "Server player entity cannot be null");
		Objects.requireNonNull(channelName, "Channel name cannot be null");
		Objects.requireNonNull(buf, "Packet byte buf cannot be null");

		player.networkHandler.sendPacket(createS2CPacket(channelName, buf));
	}

	// Helper methods

	/**
	 * Returns the <i>Minecraft</i> Server of a server play network handler.
	 *
	 * @param handler the server play network handler
	 */
	public static MinecraftServer getServer(ServerPlayNetworkHandler handler) {
		Objects.requireNonNull(handler, "Network handler cannot be null");

		return handler.player.server;
	}

	@FunctionalInterface
	public interface PlayChannelHandler {
		/**
		 * Handles an incoming packet.
		 *
		 * <p>This method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
		 * Modification to the game should be {@linkplain net.minecraft.util.thread.ThreadExecutor#submit(Runnable) scheduled} using the provided Minecraft server instance.
		 *
		 * <p>An example usage of this is to create an explosion where the player is looking:
		 * <pre>{@code
		 * ServerPlayNetworking.getPlayReceivers().register(new Identifier("mymod", "boom"), (handler, server, sender, buf) -&rt; {
		 * 	boolean fire = buf.readBoolean();
		 *
		 * 	// All operations on the server or world must be executed on the server thread
		 * 	server.execute(() -&rt; {
		 * 		ModPacketHandler.createExplosion(handler.player, fire);
		 * 	});
		 * });
		 * }</pre>
		 *
		 * @param handler the network handler that received this packet
		 * @param sender the packet sender
		 * @param server the server
		 * @param buf the payload of the packet
		 */
		void receive(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server, PacketByteBuf buf);
	}
}
