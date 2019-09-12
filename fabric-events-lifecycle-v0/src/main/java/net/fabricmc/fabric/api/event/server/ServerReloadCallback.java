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

package net.fabricmc.fabric.api.event.server;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;

/**
 * A callback for a server's data reload.
 */
public interface ServerReloadCallback {
	/**
	 * An event fired before the data packs are reloaded.
	 *
	 * <p>Mods should register to this event for keeping information across reloads,
	 * e.g. identifiers of advancements obtained before reload.
	 */
	Event<ServerReloadCallback> PRE_EVENT = EventFactory.createArrayBacked(ServerReloadCallback.class,
		listeners -> server -> {
			for (ServerReloadCallback event : listeners) {
				event.onReload(server);
			}
		}
	);

	/**
	 * An event fired after the data packs are reloaded and the updated vanilla advancements, recipes,
	 * etc. have been sent to the clients.
	 *
	 * <p>Mods can retrieve the information they've saved in the pre-event and send necessary contents
	 * to the client, etc.
	 */
	Event<ServerReloadCallback> POST_EVENT = EventFactory.createArrayBacked(ServerReloadCallback.class,
		listeners -> server -> {
			for (ServerReloadCallback event : listeners) {
				event.onReload(server);
			}
		}
	);

	/**
	 * Triggers the callback.
	 *
	 * @param server the server
	 */
	void onReload(MinecraftServer server);
}
