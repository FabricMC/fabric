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
 * A callback for server/game saving.
 */
public interface ServerSaveCallback {
	/**
	 * This event is triggered when the server finished saving all its worlds and {@code level.dat} file.
	 *
	 * @see MinecraftServer#save(boolean, boolean, boolean)
	 */
	Event<ServerSaveCallback> EVENT = EventFactory.createArrayBacked(ServerSaveCallback.class,
		listeners -> (server, silent, flush, enforced) -> {
			for (ServerSaveCallback event : listeners) {
				event.onSave(server, silent, flush, enforced);
			}
		}
	);

	/**
	 * Perform a custom saving logic.
	 *
	 * @param server the server
	 * @param silent true if the saving produces visible log messages
	 * @param flush true if the anvil chunk storage should flush its output
	 * @param enforced false if the saving should respect save-on/save-off command's settings
	 */
	void onSave(MinecraftServer server, boolean silent, boolean flush, boolean enforced);
}
