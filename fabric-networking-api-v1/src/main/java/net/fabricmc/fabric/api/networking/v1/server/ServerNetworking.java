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

package net.fabricmc.fabric.api.networking.v1.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PacketChannelCallback;
import net.fabricmc.fabric.api.networking.v1.PacketListenerCallback;
import net.fabricmc.fabric.api.networking.v1.PacketReceiver;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayPacketSender;
import net.fabricmc.fabric.api.networking.v1.client.ClientNetworking;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkHandlerHook;
import net.fabricmc.fabric.impl.networking.server.ServerNetworkingDetails;
import net.fabricmc.fabric.impl.networking.server.ServerPlayNetworkHandlerHook;
import net.fabricmc.fabric.mixin.networking.access.ServerLoginNetworkHandlerAccess;

/**
 * Offers access to server-side networking functionalities.
 *
 * <p>Server-side networking functionalities include receiving serverbound packets,
 * sending clientbound packets, and events related to server-side network handlers.</p>
 *
 * <p>This class should be only used for the logical server.</p>
 *
 * @see ClientNetworking
 */
public final class ServerNetworking {
	/**
	 * An event for the server play network handler receiving an update indicating
	 * the connected client's ability to receive packets in certain channels.
	 *
	 * @see PlayPacketSender#hasChannel(Identifier)
	 */
	public static final Event<PacketChannelCallback<ServerPlayNetworkHandler>> CHANNEL_REGISTERED = EventFactory
			.createArrayBacked(PacketChannelCallback.class, callbacks -> (handler, channels) -> {
				for (PacketChannelCallback<ServerPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler, channels);
				}
			});
	/**
	 * An event for the server play network handler receiving an update indicating
	 * the connected client's lack of ability to receive packets in certain channels.
	 *
	 * @see PlayPacketSender#hasChannel(Identifier)
	 */
	public static final Event<PacketChannelCallback<ServerPlayNetworkHandler>> CHANNEL_UNREGISTERED = EventFactory
			.createArrayBacked(PacketChannelCallback.class, callbacks -> (handler, channels) -> {
				for (PacketChannelCallback<ServerPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler, channels);
				}
			});
	/**
	 * An event for the initialization of the server play network handler.
	 *
	 * <p>At this stage, the network handler is ready to send packets to the client. Use
	 * {@link #getPlaySender(ServerPlayNetworkHandler)} to obtain the packet sender in the
	 * callback.</p>
	 */
	public static final Event<PacketListenerCallback<ServerPlayNetworkHandler>> PLAY_INITIALIZED = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ServerPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	/**
	 * An event for the disconnection of the server play network handler.
	 *
	 * <p>No packets should be sent when this event is invoked.</p>
	 */
	public static final Event<PacketListenerCallback<ServerPlayNetworkHandler>> PLAY_DISCONNECTED = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ServerPlayNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	/**
	 * An event for the start of login queries of the server login network handler.
	 *
	 * <p>Use {@link #getLoginSender(ServerLoginNetworkHandler)} to obtain the query request
	 * packet sender in the callback.</p>
	 */
	public static final Event<PacketListenerCallback<ServerLoginNetworkHandler>> LOGIN_QUERY_START = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ServerLoginNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});
	/**
	 * An event for the disconnection of the server login network handler.
	 *
	 * <p>No packets should be sent when this event is invoked.</p>
	 */
	public static final Event<PacketListenerCallback<ServerLoginNetworkHandler>> LOGIN_DISCONNECTED = EventFactory
			.createArrayBacked(PacketListenerCallback.class, callbacks -> handler -> {
				for (PacketListenerCallback<ServerLoginNetworkHandler> callback : callbacks) {
					callback.handle(handler);
				}
			});

	/**
	 * Returns the packet receiver for channel handler registration on server play network
	 * handlers, receiving {@link net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket
	 * client to server custom payload packets}.
	 */
	public static PacketReceiver<ServerPlayContext> getPlayReceiver() {
		return ServerNetworkingDetails.PLAY;
	}

	/**
	 * Returns the packet receiver for channel handler registration on server play network
	 * handlers, receiving {@link net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket
	 * login query response packets}.
	 */
	public static PacketReceiver<ServerLoginContext> getLoginReceiver() {
		return ServerNetworkingDetails.LOGIN;
	}

	/**
	 * Returns the packet sender for a server play network handler.
	 *
	 * @param handler a server play network handler
	 * @return the associated packet sender
	 */
	public static PlayPacketSender getPlaySender(ServerPlayNetworkHandler handler) {
		return ((ServerPlayNetworkHandlerHook) handler).getAddon();
	}

	/**
	 * Returns the login query packet sender for a server login network handler.
	 *
	 * @param handler the server login network handler
	 * @return the associated login query packet sender
	 */
	public static PacketSender getLoginSender(ServerLoginNetworkHandler handler) {
		return ((ServerLoginNetworkHandlerHook) handler).getAddon();
	}

	/**
	 * Returns the packet sender for a server player.
	 *
	 * <p>This is a shortcut for {@link #getPlaySender(ServerPlayNetworkHandler)}.</p>
	 *
	 * @param player a server player
	 * @return the associated packet sender
	 */
	public static PlayPacketSender getPlaySender(ServerPlayerEntity player) {
		return getPlaySender(player.networkHandler);
	}

	/**
	 * Returns the <i>Minecraft</i> Server of a server play network handler.
	 *
	 * @param handler the server play network handler
	 */
	public static MinecraftServer getServer(ServerPlayNetworkHandler handler) {
		return handler.player.server;
	}

	/**
	 * Returns the <i>Minecraft</i> Server of a server login network handler.
	 *
	 * @param handler the server login network handler
	 */
	public static MinecraftServer getServer(ServerLoginNetworkHandler handler) {
		return ((ServerLoginNetworkHandlerAccess) handler).getServer();
	}
}
