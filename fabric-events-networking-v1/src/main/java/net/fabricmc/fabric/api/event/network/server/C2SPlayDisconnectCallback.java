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

package net.fabricmc.fabric.api.event.network.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ServerPlayPacketListener;
import org.apache.logging.log4j.LogManager;

/**
 * Called on the server on the network thread when the client is disconnected
 * from the server.
 */
public interface C2SPlayDisconnectCallback {

	Event<C2SPlayDisconnectCallback> EVENT = EventFactory.createArrayBacked(C2SPlayDisconnectCallback.class, listeners -> (connection, packetListener) -> {
		for (C2SPlayDisconnectCallback event : listeners) {
			try {
				event.onLeave(connection, packetListener);
			} catch (Throwable t) {
				// netty swallows exceptions
				String name = EventFactory.getHandlerName(event);
				LogManager.getLogger(event).error("Exception caught while handling leave event from {}.", name, t);
			}
		}
	});

	void onLeave(ClientConnection connection, ServerPlayPacketListener packetListener);
}
