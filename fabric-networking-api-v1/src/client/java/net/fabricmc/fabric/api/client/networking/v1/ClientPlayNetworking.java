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
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;

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
public final class ClientPlayNetworking {
	/**
	 * Registers a handler to a channel.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterGlobalReceiver(Identifier)} to unregister the existing handler.
	 *
	 * @param channelName the id of the channel
	 * @param channelHandler the handler
	 * @return false if a handler is already registered to the channel
	 * @see ClientPlayNetworking#unregisterGlobalReceiver(Identifier)
	 * @see ClientPlayNetworking#registerReceiver(Identifier, PlayChannelHandler)
	 */
	public static boolean registerGlobalReceiver(Identifier channelName, PlayChannelHandler channelHandler) {
		return ClientNetworkingImpl.PLAY.registerGlobalReceiver(channelName, channelHandler);
	}

	public static <T extends FabricPacket> boolean registerGlobalReceiver(PacketType<T> type, PlayPacketHandler<T> handler) {
		return registerGlobalReceiver(type.getId(), new PlayChannelHandlerProxy<T>() {
			@Override
			public PlayPacketHandler<T> getOriginalHandler() {
				return handler;
			}

			@Override
			public void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
				T packet = type.read(buf);

				if (handler.processOnNetworkThread(client.player, packet, sender)) {
					client.execute(() -> {
						if (networkHandler.getConnection().isOpen()) handler.receive(client.player, packet, sender);
					});
				}
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
	 * @see ClientPlayNetworking#registerGlobalReceiver(Identifier, PlayChannelHandler)
	 * @see ClientPlayNetworking#unregisterReceiver(Identifier)
	 */
	@Nullable
	public static PlayChannelHandler unregisterGlobalReceiver(Identifier channelName) {
		return ClientNetworkingImpl.PLAY.unregisterGlobalReceiver(channelName);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T extends FabricPacket> PlayPacketHandler<T> unregisterGlobalReceiver(PacketType<T> type) {
		PlayChannelHandler handler = ClientNetworkingImpl.PLAY.unregisterGlobalReceiver(type.getId());
		return handler instanceof PlayChannelHandlerProxy<?> proxy ? (PlayPacketHandler<T>) proxy.getOriginalHandler() : null;
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
	 * Use {@link #unregisterReceiver(Identifier)} to unregister the existing handler.
	 *
	 * <p>For example, if you only register a receiver using this method when a {@linkplain ClientLoginNetworking#registerGlobalReceiver(Identifier, ClientLoginNetworking.LoginQueryRequestHandler)}
	 * login query has been received, you should use {@link ClientPlayConnectionEvents#INIT} to register the channel handler.
	 *
	 * @param channelName the id of the channel
	 * @return false if a handler is already registered to the channel
	 * @throws IllegalStateException if the client is not connected to a server
	 * @see ClientPlayConnectionEvents#INIT
	 */
	public static boolean registerReceiver(Identifier channelName, PlayChannelHandler channelHandler) {
		final ClientPlayNetworkAddon addon = ClientNetworkingImpl.getClientPlayAddon();

		if (addon != null) {
			return addon.registerChannel(channelName, channelHandler);
		}

		throw new IllegalStateException("Cannot register receiver while not in game!");
	}

	public static <T extends FabricPacket> boolean registerReceiver(PacketType<T> type, PlayPacketHandler<T> handler) {
		return registerReceiver(type.getId(), new PlayChannelHandlerProxy<T>() {
			@Override
			public PlayPacketHandler<T> getOriginalHandler() {
				return handler;
			}

			@Override
			public void receive(MinecraftClient client, ClientPlayNetworkHandler networkHandler, PacketByteBuf buf, PacketSender sender) {
				T packet = type.read(buf);

				if (handler.processOnNetworkThread(client.player, packet, sender)) {
					client.execute(() -> {
						if (networkHandler.getConnection().isOpen()) handler.receive(client.player, packet, sender);
					});
				}
			}
		});
	}

	/**
	 * Removes the handler of a channel.
	 *
	 * <p>The {@code channelName} is guaranteed not to have a handler after this call.
	 *
	 * @param channelName the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	@Nullable
	public static PlayChannelHandler unregisterReceiver(Identifier channelName) throws IllegalStateException {
		final ClientPlayNetworkAddon addon = ClientNetworkingImpl.getClientPlayAddon();

		if (addon != null) {
			return addon.unregisterChannel(channelName);
		}

		throw new IllegalStateException("Cannot unregister receiver while not in game!");
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T extends FabricPacket> PlayPacketHandler<T> unregisterReceiver(PacketType<T> type) {
		PlayChannelHandler handler = unregisterReceiver(type.getId());
		return handler instanceof PlayChannelHandlerProxy<?> proxy ? (PlayPacketHandler<T>) proxy.getOriginalHandler() : null;
	}

	/**
	 * Gets all the channel names that the client can receive packets on.
	 *
	 * @return All the channel names that the client can receive packets on
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static Set<Identifier> getReceived() throws IllegalStateException {
		final ClientPlayNetworkAddon addon = ClientNetworkingImpl.getClientPlayAddon();

		if (addon != null) {
			return addon.getReceivableChannels();
		}

		throw new IllegalStateException("Cannot get a list of channels the client can receive packets on while not in game!");
	}

	/**
	 * Gets all channel names that the connected server declared the ability to receive a packets on.
	 *
	 * @return All the channel names the connected server declared the ability to receive a packets on
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static Set<Identifier> getSendable() throws IllegalStateException {
		final ClientPlayNetworkAddon addon = ClientNetworkingImpl.getClientPlayAddon();

		if (addon != null) {
			return addon.getSendableChannels();
		}

		throw new IllegalStateException("Cannot get a list of channels the server can receive packets on while not in game!");
	}

	/**
	 * Checks if the connected server declared the ability to receive a packet on a specified channel name.
	 *
	 * @param channelName the channel name
	 * @return True if the connected server has declared the ability to receive a packet on the specified channel.
	 * False if the client is not in game.
	 */
	public static boolean canSend(Identifier channelName) throws IllegalArgumentException {
		// You cant send without a client player, so this is fine
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			return ClientNetworkingImpl.getAddon(MinecraftClient.getInstance().getNetworkHandler()).getSendableChannels().contains(channelName);
		}

		return false;
	}

	public static boolean canSend(PacketType<?> type) {
		return canSend(type.getId());
	}

	/**
	 * Creates a packet which may be sent to the connected server.
	 *
	 * @param channelName the channel name
	 * @param buf the packet byte buf which represents the payload of the packet
	 * @return a new packet
	 */
	public static Packet<ServerPlayPacketListener> createC2SPacket(Identifier channelName, PacketByteBuf buf) {
		Objects.requireNonNull(channelName, "Channel name cannot be null");
		Objects.requireNonNull(buf, "Buf cannot be null");

		return ClientNetworkingImpl.createPlayC2SPacket(channelName, buf);
	}

	/**
	 * Gets the packet sender which sends packets to the connected server.
	 *
	 * @return the client's packet sender
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static PacketSender getSender() throws IllegalStateException {
		// You cant send without a client player, so this is fine
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			return ClientNetworkingImpl.getAddon(MinecraftClient.getInstance().getNetworkHandler());
		}

		throw new IllegalStateException("Cannot get packet sender when not in game!");
	}

	/**
	 * Sends a packet to the connected server.
	 *
	 * @param channelName the channel of the packet
	 * @param buf the payload of the packet
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static void send(Identifier channelName, PacketByteBuf buf) throws IllegalStateException {
		// You cant send without a client player, so this is fine
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			MinecraftClient.getInstance().getNetworkHandler().sendPacket(createC2SPacket(channelName, buf));
			return;
		}

		throw new IllegalStateException("Cannot send packets when not in game!");
	}

	public static <T extends FabricPacket> void send(PacketType<T> type, T packet) {
		Objects.requireNonNull(type, "Packet type cannot be null");
		Objects.requireNonNull(packet, "Packet cannot be null");

		PacketByteBuf buf = PacketByteBufs.create();
		packet.write(buf);
		send(type.getId(), buf);
	}

	private ClientPlayNetworking() {
	}

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
		 * ClientPlayNetworking.registerReceiver(new Identifier("mymod", "overlay"), (client, handler, buf, responseSender) -&rt; {
		 * 	String message = buf.readString(32767);
		 *
		 * 	// All operations on the server or world must be executed on the server thread
		 * 	client.execute(() -&rt; {
		 * 		client.inGameHud.setOverlayMessage(message, true);
		 * 	});
		 * });
		 * }</pre>
		 *  @param client the client
		 * @param handler the network handler that received this packet
		 * @param buf the payload of the packet
		 * @param responseSender the packet sender
		 */
		void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender);
	}

	interface PlayChannelHandlerProxy<T extends FabricPacket> extends PlayChannelHandler {
		PlayPacketHandler<T> getOriginalHandler();
	}

	@FunctionalInterface
	public interface PlayPacketHandler<T extends FabricPacket> {
		void receive(ClientPlayerEntity player, T packet, PacketSender responseSender);

		default boolean processOnNetworkThread(ClientPlayerEntity player, T packet, PacketSender responseSender) {
			return true;
		}
	}
}
