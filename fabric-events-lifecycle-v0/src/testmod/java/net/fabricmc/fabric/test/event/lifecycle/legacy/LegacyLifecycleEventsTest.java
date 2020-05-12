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

package net.fabricmc.fabric.test.event.lifecycle.legacy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.fabric.api.event.server.ServerStopCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;

public class LegacyLifecycleEventsTest implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("LegacyLifecycleEventsTest");

	@Override
	public void onInitialize() {
		ServerTickCallback.EVENT.register(server -> {
			if (server.getTicks() % 200 == 0) { // Log every 200 ticks to verify the tick callback works on the server
				LOGGER.info("Ticked Server at " + server.getTicks() + " ticks.");
			}
		});

		ServerStartCallback.EVENT.register(server -> {
			LOGGER.info("Started Server! (Legacy)");
		});

		ServerStopCallback.EVENT.register(server -> {
			LOGGER.info("Stopped Server! (Legacy)");
		});

		WorldTickCallback.EVENT.register(world -> {
			LOGGER.info("Ticked " + world.dimension.getType() + ": " + world.getClass().toString());
		});
	}
}
