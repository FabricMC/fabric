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
import net.minecraft.world.level.LevelProperties;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A callback for the server's saving.
 */
public interface ServerSaveCallback {
	/**
	 * An event invoked when the server's vanilla saving logic has completed.
	 */
	Event<ServerSaveCallback> EVENT = EventFactory.createArrayBacked(ServerSaveCallback.class, listeners -> (server, levelProperties) -> {
		for (ServerSaveCallback event : listeners) {
			event.onServerSave(server, levelProperties);
		}
	});

	void onServerSave(MinecraftServer server, LevelProperties levelProperties);
}
