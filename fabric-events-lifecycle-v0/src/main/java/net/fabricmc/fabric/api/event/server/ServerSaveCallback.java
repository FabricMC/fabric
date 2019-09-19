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
import net.minecraft.world.level.LevelProperties;

/**
 * A callback for server/game saving. This is called when all worlds have been saved and the level properties
 * have been initialized but not yet written to file.
 */
public interface ServerSaveCallback {
	/**
	 * This event is triggered when the server finished saving all its worlds and preparing level properties
	 * before writing it to file.
	 *
	 * @see MinecraftServer#save(boolean, boolean, boolean)
	 */
	Event<ServerSaveCallback> EVENT = EventFactory.createArrayBacked(ServerSaveCallback.class,
		listeners -> (server, properties) -> {
			for (ServerSaveCallback event : listeners) {
				event.onSave(server, properties);
			}
		}
	);

	/**
	 * Perform a custom saving logic.
	 *
	 * <p>The level properties is provided in case mods need to tweak it.
	 *
	 * @param server the server
	 * @param properties the level properties
	 */
	void onSave(MinecraftServer server, LevelProperties properties);
}
