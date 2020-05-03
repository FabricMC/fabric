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

package net.fabricmc.fabric.api.networking.v1.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PacketChannelCallback;
import net.fabricmc.fabric.api.networking.v1.PacketListenerCallback;
import net.fabricmc.fabric.api.networking.v1.PacketReceiver;
import net.fabricmc.fabric.api.networking.v1.PlayPacketSender;
import net.fabricmc.fabric.api.networking.v1.server.ServerNetworking;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingDetails;

/**
 * Offers access to client-side networking functionalities.
 *
 * <p>Client-side networking functionalities include receiving clientbound packets,
 * sending serverbound packets, and events related to client-side network handlers.</p>
 *
 * <p>This class should be only used on the physical client and for the logical client.</p>
 *
 * @see ServerNetworking
 */
@Environment(EnvType.CLIENT)
public final class ClientNetworking {
	/**
	 * An event for the initialization of the client play network handler.
	 *
	 * <p>At this stage, the network handler is ready to send packets to the server. Use
	 * {@link #getPlaySender(ClientPlayNetworkHandler)} to obtain the packet sender in
	 * the callback.</p>
	 */
	public static final Event<PacketListenerCallback<ClientPlayNetworkHandler>> PLAY_INITIALIZED = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ClientPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	/**
	 * An event for the disconnection of the client play network handler.
	 *
	 * <p>No packets should be sent when this event is invoked.</p>
	 */
	public static final Event<PacketListenerCallback<ClientPlayNetworkHandler>> PLAY_DISCONNECTED = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ClientPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	/**
	 * An event for the client play network handler receiving an update indicating
	 * the connected server's ability to receive packets in certain channels.
	 *
	 * @see PlayPacketSender#hasChannel(Identifier)
	 */
	public static final Event<PacketChannelCallback<ClientPlayNetworkHandler>> CHANNEL_REGISTERED = EventFactory
			.createArrayBacked(PacketChannelCallback.class, callbacks -> (handler, channels) -> {
				for (PacketChannelCallback<ClientPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler, channels);
				}
			});
	/**
	 * An event for the client play network handler receiving an update indicating
	 * the connected server's lack of ability to receive packets in certain channels.
	 *
	 * @see PlayPacketSender#hasChannel(Identifier)
	 */
	public static final Event<PacketChannelCallback<ClientPlayNetworkHandler>> CHANNEL_UNREGISTERED = EventFactory
			.createArrayBacked(PacketChannelCallback.class, callbacks -> (handler, channels) -> {
				for (PacketChannelCallback<ClientPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler, channels);
				}
			});

	/**
	 * Returns the packet sender for the current client player.
	 *
	 * <p>This is a shortcut method for getting a sender. When a client play
	 * network handler is available, {@link #getPlaySender(ClientPlayNetworkHandler)}
	 * is preferred.</p>
	 *
	 * @return the packet sender for the current client player
	 * @throws IllegalStateException if the client's player is {@code null}
	 */
	public static PlayPacketSender getPlaySender() throws IllegalStateException {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;

		if (player == null) {
			throw new IllegalStateException("Cannot get packet sender when not in game!");
		}

		return getPlaySender(player.networkHandler);
	}

	/**
	 * Returns the packet sender for a client play network handler.
	 *
	 * @param handler a client play network handler
	 * @return the associated packet sender
	 */
	public static PlayPacketSender getPlaySender(ClientPlayNetworkHandler handler) {
		return ClientNetworkingDetails.getAddon(handler);
	}

	/**
	 * Returns the packet receiver for channel handler registration on client play network
	 * handlers, receiving {@link net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket
	 * server to client custom payload packets}.
	 */
	public static PacketReceiver<ClientPlayContext> getPlayReceiver() {
		return ClientNetworkingDetails.PLAY;
	}

	/**
	 * Returns the packet receiver for channel handler registration on client login network
	 * handlers, receiving {@link net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket
	 * login query request packets}.
	 */
	public static PacketReceiver<ClientLoginContext> getLoginReceiver() {
		return ClientNetworkingDetails.LOGIN;
	}
}
