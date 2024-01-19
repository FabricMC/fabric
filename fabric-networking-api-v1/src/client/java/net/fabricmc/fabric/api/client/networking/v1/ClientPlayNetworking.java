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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerCommonPacketListener;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import net.fabricmc.fabric.impl.networking.payload.ResolvablePayload;

/**
 * Offers access to play stage client-side networking functionalities.
 *
 * <p>Client-side networking functionalities include receiving clientbound packets,
 * sending serverbound packets, and events related to client-side network handlers.
 *
 * <p>This class should be only used on the physical client and for the logical client.
 *
 * <p>See {@link ServerPlayNetworking} for information on how to use the payload
 * object-based API.
 *
 * @see ClientLoginNetworking
 * @see ClientConfigurationNetworking
 * @see ServerPlayNetworking
 */
public final class ClientPlayNetworking {
	/**
	 * Registers a handler for a payload type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterGlobalReceiver(PacketType)} to unregister the existing handler.
	 *
	 * @param type the payload type
	 * @param handler the handler
	 * @return false if a handler is already registered to the channel
	 * @see ClientPlayNetworking#unregisterGlobalReceiver(PacketType)
	 * @see ClientPlayNetworking#registerReceiver(PacketType, PlayPacketHandler)
	 */
	public static <T extends CustomPayload> boolean registerGlobalReceiver(CustomPayload.Id<T> type, PlayPacketHandler<T> handler) {
		return ClientNetworkingImpl.PLAY.registerGlobalReceiver(type.id(), wrapTyped(type, handler));
	}

	/**
	 * Removes the handler for a payload type.
	 * A global receiver is registered to all connections, in the present and future.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param type the payload type
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerGlobalReceiver(PacketType, PlayPacketHandler)}
	 * @see ClientPlayNetworking#registerGlobalReceiver(PacketType, PlayPacketHandler)
	 * @see ClientPlayNetworking#unregisterReceiver(PacketType)
	 */
	@Nullable
	public static <T extends CustomPayload> ClientPlayNetworking.PlayPacketHandler<T> unregisterGlobalReceiver(CustomPayload.Id<T> type) {
		return ClientNetworkingImpl.PLAY.unregisterGlobalReceiver(type.id());
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
	 * Registers a handler for a payload type.
	 *
	 * <p>If a handler is already registered for the {@code type}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterReceiver(PacketType)} to unregister the existing handler.
	 *
	 * <p>For example, if you only register a receiver using this method when a {@linkplain ClientLoginNetworking#registerGlobalReceiver(Identifier, ClientLoginNetworking.LoginQueryRequestHandler)}
	 * login query has been received, you should use {@link ClientPlayConnectionEvents#INIT} to register the channel handler.
	 *
	 * @param type the payload type
	 * @param handler the handler
	 * @return {@code false} if a handler is already registered for the type
	 * @throws IllegalStateException if the client is not connected to a server
	 * @see ClientPlayConnectionEvents#INIT
	 */
	public static <T extends CustomPayload> boolean registerReceiver(CustomPayload.Id<T> type, PlayPacketHandler<T> handler) {
		final ClientPlayNetworkAddon addon = ClientNetworkingImpl.getClientPlayAddon();

		if (addon != null) {
			return addon.registerChannel(type.id(), wrapTyped(type, handler));
		}

		throw new IllegalStateException("Cannot register receiver while not in game!");
	}

	/**
	 * Removes the handler for a payload type.
	 *
	 * <p>The {@code type} is guaranteed not to have an associated handler after this call.
	 *
	 * @param type the payload type
	 * @return the previous handler, or {@code null} if no handler was bound to the channel,
	 * or it was not registered using {@link #registerReceiver(PacketType, PlayPacketHandler)}
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	@Nullable
	public static <T extends CustomPayload> ClientPlayNetworking.PlayPacketHandler<T> unregisterReceiver(CustomPayload.Id<T> type) {
		final ClientPlayNetworkAddon addon = ClientNetworkingImpl.getClientPlayAddon();

		if (addon != null) {
			return unwrapTyped(addon.unregisterChannel(type.id()));
		}

		throw new IllegalStateException("Cannot unregister receiver while not in game!");
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
	 * Checks if the connected server declared the ability to receive a payload on a specified channel name.
	 *
	 * @param channelName the channel name
	 * @return {@code true} if the connected server has declared the ability to receive a payload on the specified channel.
	 * False if the client is not in game.
	 */
	public static boolean canSend(Identifier channelName) throws IllegalArgumentException {
		// You cant send without a client player, so this is fine
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			return ClientNetworkingImpl.getAddon(MinecraftClient.getInstance().getNetworkHandler()).getSendableChannels().contains(channelName);
		}

		return false;
	}

	/**
	 * Checks if the connected server declared the ability to receive a payload on a specified channel name.
	 * This returns {@code false} if the client is not in game.
	 *
	 * @param type the payload type
	 * @return {@code true} if the connected server has declared the ability to receive a payload on the specified channel
	 */
	public static boolean canSend(CustomPayload.Id<?> type) {
		return canSend(type.id());
	}

	/**
	 * Creates a payload which may be sent to the connected server.
	 *
	 * @param packet the fabric payload
	 * @return a new payload
	 */
	public static <T extends CustomPayload> Packet<ServerCommonPacketListener> createC2SPacket(T packet) {
		return ClientNetworkingImpl.createC2SPacket(packet);
	}

	/**
	 * Gets the payload sender which sends packets to the connected server.
	 *
	 * @return the client's payload sender
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static PacketSender getSender() throws IllegalStateException {
		// You cant send without a client player, so this is fine
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			return ClientNetworkingImpl.getAddon(MinecraftClient.getInstance().getNetworkHandler());
		}

		throw new IllegalStateException("Cannot get payload sender when not in game!");
	}

	/**
	 * Sends a payload to the connected server.
	 *
	 * @param packet the payload
	 * @throws IllegalStateException if the client is not connected to a server
	 */
	public static <T extends CustomPayload> void send(T packet) {
		Objects.requireNonNull(packet, "Packet cannot be null");
		Objects.requireNonNull(packet.getId(), "Packet#getType cannot return null");

		// You cant send without a client player, so this is fine
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			MinecraftClient.getInstance().getNetworkHandler().sendPacket(createC2SPacket(packet));
			return;
		}

		throw new IllegalStateException("Cannot send packets when not in game!");
	}

	private ClientPlayNetworking() {
	}

	/**
	 * A thread-safe payload handler utilizing {@link CustomPayload}.
	 * @param <T> the type of the payload
	 */
	@FunctionalInterface
	public interface PlayPacketHandler<T extends CustomPayload> {
		/**
		 * Handles the incoming payload. This is called on the render thread, and can safely
		 * call client methods.
		 *
		 * <p>An example usage of this is to display an overlay message:
		 * <pre>{@code
		 * // See FabricPacket for creating the payload
		 * ClientPlayNetworking.registerReceiver(OVERLAY_PACKET_TYPE, (player, payload, responseSender) -> {
		 * 	MinecraftClient.getInstance().inGameHud.setOverlayMessage(payload.message(), true);
		 * });
		 * }</pre>
		 *
		 * <p>The network handler can be accessed via {@link ClientPlayerEntity#networkHandler}.
		 *
		 * @param payload the packet payload
		 * @param player the player that received the payload
		 * @param responseSender the payload sender
		 * @see CustomPayload
		 */
		void receive(T payload, ClientPlayerEntity player, PacketSender responseSender);
	}
}
