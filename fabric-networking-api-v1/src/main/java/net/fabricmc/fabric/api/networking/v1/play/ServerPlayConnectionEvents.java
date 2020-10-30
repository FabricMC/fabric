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

package net.fabricmc.fabric.api.networking.v1.play;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PlayPacketSender;

/**
 * Offers access to events related to the connection to a client on a logical server.
 */
public final class ServerPlayConnectionEvents {
	/**
	 * An event for the initialization of the server play network handler.
	 *
	 * <p>At this stage, the network handler is ready to send packets to the client.
	 * Use {@link ServerPlayNetworking#getPlaySender(ServerPlayNetworkHandler)} to obtain the packet sender in the callback.
	 */
	public static final Event<PlayInitialized> PLAY_INITIALIZED = EventFactory.createArrayBacked(PlayInitialized.class, callbacks -> (handler, server, sender) -> {
		for (PlayInitialized callback : callbacks) {
			callback.onPlayInitialized(handler, server, sender);
		}
	});
	/**
	 * An event for the disconnection of the server play network handler.
	 *
	 * <p>No packets should be sent when this event is invoked.</p>
	 */
	public static final Event<PlayDisconnected> PLAY_DISCONNECTED = EventFactory.createArrayBacked(PlayDisconnected.class, callbacks -> (handler, server) -> {
		for (PlayDisconnected callback : callbacks) {
			callback.onPlayDisconnected(handler, server);
		}
	});

	private ServerPlayConnectionEvents() {
	}

	@FunctionalInterface
	public interface PlayInitialized {
		void onPlayInitialized(ServerPlayNetworkHandler handler, MinecraftServer server, PlayPacketSender sender);
	}

	@FunctionalInterface
	public interface PlayDisconnected {
		void onPlayDisconnected(ServerPlayNetworkHandler handler, MinecraftServer server);
	}
}
