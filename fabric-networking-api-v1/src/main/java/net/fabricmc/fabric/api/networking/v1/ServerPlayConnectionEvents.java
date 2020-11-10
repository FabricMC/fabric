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

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Offers access to events related to the connection to a client on a logical server.
 */
public final class ServerPlayConnectionEvents {
	/**
	 * An event for the initialization of the server play network handler.
	 *
	 * <p>At this stage, the network handler is ready to send packets to the client.
	 */
	public static final Event<PlayInit> PLAY_INIT = EventFactory.createArrayBacked(PlayInit.class, callbacks -> (handler, sender, server) -> {
		for (PlayInit callback : callbacks) {
			callback.onPlayInit(handler, sender, server);
		}
	});
	/**
	 * An event for the disconnection of the server play network handler.
	 *
	 * <p>No packets should be sent when this event is invoked.</p>
	 */
	public static final Event<PlayDisconnect> PLAY_DISCONNECT = EventFactory.createArrayBacked(PlayDisconnect.class, callbacks -> (handler, server) -> {
		for (PlayDisconnect callback : callbacks) {
			callback.onPlayDisconnect(handler, server);
		}
	});

	private ServerPlayConnectionEvents() {
	}

	@FunctionalInterface
	public interface PlayInit {
		void onPlayInit(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server);
	}

	@FunctionalInterface
	public interface PlayDisconnect {
		void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server);
	}
}
