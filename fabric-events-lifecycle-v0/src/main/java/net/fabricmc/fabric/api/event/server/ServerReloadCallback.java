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

import net.minecraft.server.MinecraftServer;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A callback for the server's reloading, as of {@code /reload} command is typed.
 */
public interface ServerReloadCallback {
	/**
	 * An event invoked immediately before the server starts reloading.
	 */
	Event<ServerReloadCallback> PRE = EventFactory.createArrayBacked(ServerReloadCallback.class, listeners -> server -> {
		for (ServerReloadCallback event : listeners) {
			event.onServerReload(server);
		}
	});

	/**
	 * An event invoked after the server just finished reloading or its initial loading.
	 */
	Event<ServerReloadCallback> POST = EventFactory.createArrayBacked(ServerReloadCallback.class, listeners -> server -> {
		for (ServerReloadCallback event : listeners) {
			event.onServerReload(server);
		}
	});

	/**
	 * Handles the event.
	 *
	 * @param server the server
	 */
	void onServerReload(MinecraftServer server);
}
