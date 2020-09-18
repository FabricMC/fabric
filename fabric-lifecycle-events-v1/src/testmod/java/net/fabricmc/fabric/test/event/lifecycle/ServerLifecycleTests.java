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

package net.fabricmc.fabric.test.event.lifecycle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

/**
 * Tests related to the lifecycle of a server.
 */
public class ServerLifecycleTests implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("LifecycleEventsTest");

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			LOGGER.info("Started Server!");
		});

		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			LOGGER.info("Stopping Server!");
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			LOGGER.info("Stopped Server!");
		});

		ServerWorldEvents.LOAD.register((server, world) -> {
			LOGGER.info("Loaded world " + world.getRegistryKey().getValue().toString());
		});

		ServerWorldEvents.UNLOAD.register((server, world) -> {
			LOGGER.info("Unloaded world " + world.getRegistryKey().getValue().toString());
		});
	}
}
