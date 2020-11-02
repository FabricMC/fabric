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
	public static boolean registerGlobalReceiver(Identifier channel, PlayChannelHandler channelHandler) {
		// FIXME: Temp stuff
		ServerPlayConnectionEvents.PLAY_INIT.register((networkHandler, sender, server) -> {
			ServerPlayNetworking.register(networkHandler, channel, channelHandler);
		});

		// TODO: Temp
		return true;
	}

	@Nullable
	public static PlayChannelHandler unregisterGlobalReceiver(Identifier channel) {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	/**
	 * Registers a handler to a channel.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregister(ServerPlayNetworkHandler, Identifier)} to unregister the existing handler.</p>
	 *
	 * @param channel the id of the channel
	 * @param networkHandler the handler
	 * @return false if a handler is already registered to the channel
	 */
	public static boolean register(ServerPlayNetworkHandler networkHandler, Identifier channel, PlayChannelHandler channelHandler) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ((ServerPlayNetworkHandlerExtensions) networkHandler).getAddon().registerChannel(channel, channelHandler);
	}

	/**
	 * Removes the handler of a channel.
	 *
	 * <p>The {@code channel} is guaranteed not to have a handler after this call.</p>
	 *
	 * @param channel the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 */
	@Nullable
	public static PlayChannelHandler unregister(ServerPlayNetworkHandler networkHandler, Identifier channel) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ServerNetworkingImpl.getAddon(networkHandler).unregisterChannel(channel);
	}

	public static Collection<Identifier> getGlobalReceivers() {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	public static boolean hasGlobalReceiver(Identifier id) {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	public static Collection<Identifier> getReceivers(ServerPlayerEntity player) {
		return getReceivers(player.networkHandler);
	}

	public static Collection<Identifier> getReceivers(ServerPlayNetworkHandler handler) {
		return ServerNetworkingImpl.getAddon(handler).getChannels();
	}

	public static boolean canReceive(ServerPlayerEntity player, Identifier channel) {
		Objects.requireNonNull(player, "Server player entity cannot be null");

		return canReceive(player.networkHandler, channel);
	}

	public static boolean canReceive(ServerPlayNetworkHandler handler, Identifier channel) {
		return ServerNetworkingImpl.getAddon(handler).hasChannel(channel);
	}

	public static Packet<?> createS2CPacket(Identifier channel, PacketByteBuf buf) {
		Objects.requireNonNull(channel, "Channel cannot be null");
		Objects.requireNonNull(buf, "Buf cannot be null");

		return ServerNetworkingImpl.createPlayC2SPacket(channel, buf);
	}

	/**
	 * Sends a packet to a player.
	 *
	 * @param player the player to send the packet to
	 * @param channel the channel of the packet
	 * @param buf the payload of the packet.
	 */
	public static void send(ServerPlayerEntity player, Identifier channel, PacketByteBuf buf) {
		player.networkHandler.sendPacket(createS2CPacket(channel, buf));
	}

	// Util methods

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
