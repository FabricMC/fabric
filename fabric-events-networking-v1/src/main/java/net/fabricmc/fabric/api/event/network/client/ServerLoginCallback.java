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

package net.fabricmc.fabric.api.event.network.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.ClientConnection;
import org.apache.logging.log4j.LogManager;

/**
 * Called on the client on the network thread when the client attempts to
 * connect to the server.
 */
public interface ServerLoginCallback {

	Event<ServerLoginCallback> EVENT = EventFactory.createArrayBacked(ServerLoginCallback.class, listeners -> connection -> {
		for (ServerLoginCallback event : listeners) {
			try {
				event.onLogin(connection);
			} catch (Throwable t) {
				// netty swallows exceptions
				String name = EventFactory.getHandlerName(event);
				LogManager.getLogger(event).error("Exception caught while handling login event from {}.", name, t);
			}
		}
	});

	void onLogin(ClientConnection connection);
}
