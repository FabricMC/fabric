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

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Offers access to events related to the connection to a client on a logical server while a client is configuring.
 */
@ApiStatus.Experimental
public class ServerConfigurationConnectionEvents {
	/**
	 * Event fired before any vanilla configuration has taken place.
	 *
	 * <p>Task queued during this event will complete before vanilla configuration starts.
	 */
	public static final Event<Configure> PRE_CONFIGURE = EventFactory.createArrayBacked(Configure.class, callbacks -> (handler, server) -> {
		for (Configure callback : callbacks) {
			callback.onSendConfiguration(handler, server);
		}
	});

	/**
	 * Event fired during vanilla configuration.
	 */
	public static final Event<Configure> CONFIGURE = EventFactory.createArrayBacked(Configure.class, callbacks -> (handler, server) -> {
		for (Configure callback : callbacks) {
			callback.onSendConfiguration(handler, server);
		}
	});

	/**
	 * An event for the disconnection of the server configuration network handler.
	 *
	 * <p>No packets should be sent when this event is invoked.
	 */
	public static final Event<ServerConfigurationConnectionEvents.Disconnect> DISCONNECT = EventFactory.createArrayBacked(ServerConfigurationConnectionEvents.Disconnect.class, callbacks -> (handler, server) -> {
		for (ServerConfigurationConnectionEvents.Disconnect callback : callbacks) {
			callback.onConfigureDisconnect(handler, server);
		}
	});

	@FunctionalInterface
	public interface Configure {
		void onSendConfiguration(ServerConfigurationNetworkHandler handler, MinecraftServer server);
	}

	@FunctionalInterface
	public interface Disconnect {
		void onConfigureDisconnect(ServerConfigurationNetworkHandler handler, MinecraftServer server);
	}
}
