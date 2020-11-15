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

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;

/**
 * Offers access to play stage client-side networking functionalities.
 *
 * <p>Client-side networking functionalities include receiving clientbound packets,
 * sending serverbound packets, and events related to client-side network handlers.
 *
 * <p>This class should be only used on the physical client and for the logical client.
 *
 * @see ClientLoginNetworking
 * @see ServerPlayNetworking
 */
@Environment(EnvType.CLIENT)
public final class ClientPlayNetworking {
	/**
	 * Registers a handler to a channel.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregister(ClientPlayNetworkHandler, Identifier)} to unregister the existing handler.</p>
	 *
	 * @param channelName the id of the channel
	 * @param channelHandler the handler
	 * @return false if a handler is already registered to the channel
	 * @see ClientPlayNetworking#unregisterGlobalReceiver(Identifier)
	 * @see ClientPlayNetworking#register(ClientPlayNetworkHandler, Identifier, PlayChannelHandler)
	 */
	public static boolean registerGlobalReceiver(Identifier channelName, PlayChannelHandler channelHandler) {
		return ClientNetworkingImpl.PLAY.registerGlobalReceiver(channelName, channelHandler);
	}

	/**
	 * Removes the handler of a channel.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The {@code channel} is guaranteed not to have a handler after this call.</p>
	 *
	 * @param channelName the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 * @see ClientPlayNetworking#registerGlobalReceiver(Identifier, PlayChannelHandler)
	 * @see ClientPlayNetworking#unregister(ClientPlayNetworkHandler, Identifier)
	 */
	@Nullable
	public static PlayChannelHandler unregisterGlobalReceiver(Identifier channelName) {
		return ClientNetworkingImpl.PLAY.unregisterGlobalReceiver(channelName);
	}

	/**
	 * Gets all channel names which global receivers are registered for.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * @return all channel names which global receivers are registered for.
	 */
	public static Set<Identifier> getGlobalReceivers() {
		return ClientNetworkingImpl.PLAY.getChannels();
	}

	/**
	 * Registers a handler to a channel.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregister(ClientPlayNetworkHandler, Identifier)} to unregister the existing handler.</p>
	 *
	 * @param channelName the id of the channel
	 * @param networkHandler the handler
	 * @return false if a handler is already registered to the channel
	 */
	public static boolean register(ClientPlayNetworkHandler networkHandler, Identifier channelName, PlayChannelHandler channelHandler) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ClientNetworkingImpl.getAddon(networkHandler).registerChannel(channelName, channelHandler);
	}

	/**
	 * Removes the handler of a channel.
	 *
	 * <p>The {@code channel} is guaranteed not to have a handler after this call.</p>
	 *
	 * @param channelName the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 */
	@Nullable
	public static PlayChannelHandler unregister(ClientPlayNetworkHandler networkHandler, Identifier channelName) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ClientNetworkingImpl.getAddon(networkHandler).unregisterChannel(channelName);
	}

	/**
	 * Gets all the channel names that the client can receive packets on.
	 *
	 * @return All the channel names that the client can receive packets on
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static Set<Identifier> getReceivers() throws IllegalStateException {
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			return getReceivers(MinecraftClient.getInstance().getNetworkHandler());
		}

		throw new IllegalStateException("Cannot get a list of channels the client can recieve packets on while not in game!");
	}

	/**
	 * Gets all the channel names that the client can receive packets on.
	 *
	 * @param player the player
	 * @return All the channel names that the client can receive packets on
	 */
	public static Set<Identifier> getReceivers(ClientPlayerEntity player) {
		Objects.requireNonNull(player, "Client player entity cannot be null");

		return getReceivers(player.networkHandler);
	}

	/**
	 * Gets all the channel names that the client can receive packets on.
	 *
	 * @param handler the network handler
	 * @return All the channel names that the client can receive packets on
	 */
	public static Set<Identifier> getReceivers(ClientPlayNetworkHandler handler) {
		Objects.requireNonNull(handler, "Client play network handler cannot be null");

		return ClientNetworkingImpl.getAddon(handler).getReceivableChannels();
	}

	/**
	 * Checks if the client can receive packets on a specified channel name.
	 *
	 * @param channelName the channel name
	 * @return True if the client can receive packets on a specified channel name.
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static boolean canReceive(Identifier channelName) throws IllegalStateException {
		Objects.requireNonNull(channelName, "Channel name cannot be null");

		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			return canReceive(MinecraftClient.getInstance().getNetworkHandler(), channelName);
		}

		throw new IllegalStateException("Cannot check if the client can receive packets on specific channels while not in game!");
	}

	/**
	 * Checks if the client can receive packets on a specified channel name.
	 *
	 * @param player the player
	 * @param channelName the channel name
	 * @return True if the client can receive packets on a specified channel name.
	 */
	public static boolean canReceive(ClientPlayerEntity player, Identifier channelName) {
		Objects.requireNonNull(player, "Client player entity cannot be null");

		return canReceive(player.networkHandler, channelName);
	}

	/**
	 * Checks if the client can receive packets on a specified channel name.
	 *
	 * @param handler the network handler
	 * @param channelName the channel name
	 * @return True if the client can receive packets on a specified channel name.
	 */
	public static boolean canReceive(ClientPlayNetworkHandler handler, Identifier channelName) {
		Objects.requireNonNull(handler, "Client play network handler cannot be null");
		Objects.requireNonNull(channelName, "Channel name cannot be null");

		return ClientNetworkingImpl.getAddon(handler).hasReceivableChannel(channelName);
	}

	/**
	 * Gets all channel names that the connected server declared the ability to receive a packets on.
	 *
	 * @return All the channel names the connected server declared the ability to receive a packets on
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static Set<Identifier> getSendable() throws IllegalStateException {
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			return getSendable(MinecraftClient.getInstance().getNetworkHandler());
		}

		throw new IllegalStateException("Cannot get a list of channels the server can receive packets on while not in game!");
	}

	/**
	 * Gets all channel names that the connected server declared the ability to receive a packets on.
	 *
	 * @param player the player
	 * @return All the channel names the connected server declared the ability to receive a packets on
	 */
	public static Set<Identifier> getSendable(ClientPlayerEntity player) {
		Objects.requireNonNull(player, "Client player entity cannot be null");

		return getSendable(player.networkHandler);
	}

	/**
	 * Gets all channel names that the connected server declared the ability to receive a packets on.
	 *
	 * @param handler the network handler
	 * @return All the channel names the connected server declared the ability to receive a packets on
	 */
	public static Set<Identifier> getSendable(ClientPlayNetworkHandler handler) {
		Objects.requireNonNull(handler, "Client play network handler cannot be null");

		return ClientNetworkingImpl.getAddon(handler).getSendableChannels();
	}

	/**
	 * Checks if the connected server declared the ability to receive a packet on a specified channel name.
	 *
	 * @param channelName the channel name
	 * @return True if the connected server has declared the ability to receive a packet on the specified channel
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static boolean canSend(Identifier channelName) throws IllegalArgumentException {
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			return canSend(MinecraftClient.getInstance().getNetworkHandler(), channelName);
		}

		throw new IllegalStateException("Cannot check whether the server can receive a packet while not in game!");
	}

	/**
	 * Checks if the connected server declared the ability to receive a packet on a specified channel name.
	 *
	 * @param player the player
	 * @param channelName the channel name
	 * @return True if the connected server has declared the ability to receive a packet on the specified channel
	 */
	public static boolean canSend(ClientPlayerEntity player, Identifier channelName) {
		Objects.requireNonNull(player, "Client player entity cannot be null");

		return canSend(player.networkHandler, channelName);
	}

	/**
	 * Checks if the connected server declared the ability to receive a packet on a specified channel name.
	 *
	 * @param handler the network handler
	 * @param channelName the channel name
	 * @return True if the connected server has declared the ability to receive a packet on the specified channel
	 */
	public static boolean canSend(ClientPlayNetworkHandler handler, Identifier channelName) {
		Objects.requireNonNull(handler, "Client play network handler cannot be null");
		Objects.requireNonNull(channelName, "Channel name cannot be null");

		return ClientNetworkingImpl.getAddon(handler).hasSendableChannel(channelName);
	}

	/**
	 * Creates a packet which may be sent to a the connected server.
	 *
	 * @param channelName the channel name
	 * @param buf the packet byte buf which represents the payload of the packet
	 * @return a new packet
	 */
	public static Packet<?> createC2SPacket(Identifier channelName, PacketByteBuf buf) {
		Objects.requireNonNull(channelName, "Channel name cannot be null");
		Objects.requireNonNull(buf, "Buf cannot be null");

		return ClientNetworkingImpl.createPlayC2SPacket(channelName, buf);
	}

	/**
	 * Sends a packet to the connected server.
	 *
	 * @param channelName the channel of the packet
	 * @param buf the payload of the packet
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static void send(Identifier channelName, PacketByteBuf buf) throws IllegalStateException {
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			send(MinecraftClient.getInstance().getNetworkHandler(), channelName, buf);
		}

		throw new IllegalStateException("Cannot send packets when not in game!");
	}

	/**
	 * Sends a packet to a server.
	 *
	 * @param handler a client play network handler
	 * @param channelName the channel of the packet
	 * @param buf the payload of the packet
	 * @throws IllegalStateException if the client's player is {@code null}
	 */
	public static void send(ClientPlayNetworkHandler handler, Identifier channelName, PacketByteBuf buf) {
		Objects.requireNonNull(handler, "Client play network handler cannot be null");

		// Channel and buf is null checked by addon impls
		handler.sendPacket(createC2SPacket(channelName, buf));
	}

	private ClientPlayNetworking() {
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface PlayChannelHandler {
		/**
		 * Handles an incoming packet.
		 *
		 * <p>This method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
		 * Modification to the game should be {@linkplain net.minecraft.util.thread.ThreadExecutor#submit(Runnable) scheduled} using the provided Minecraft client instance.
		 *
		 * <p>An example usage of this is to display an overlay message:
		 * <pre>{@code
		 * ClientPlayNetworking.getPlayReceivers().register(new Identifier("mymod", "overlay"), (handler, client, sender, buf) -&rt; {
		 * 	String message = buf.readString(32767);
		 *
		 * 	// All operations on the server or world must be executed on the server thread
		 * 	client.execute(() -&rt; {
		 * 		client.inGameHud.setOverlayMessage(message, true);
		 * 	});
		 * });
		 * }</pre>
		 *
		 * @param handler the network handler that received this packet
		 * @param sender the packet sender
		 * @param client the client
		 * @param buf the payload of the packet
		 */
		void receive(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client, PacketByteBuf buf);
	}
}
