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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;

/**
 * Tests related to the lifecycle of entities.
 */
public class ServerEntityLifecycleTests implements ModInitializer {
	private static boolean PRINT_SERVER_ENTITY_MESSAGES = System.getProperty("fabric-lifecycle-events-testmod.printServerEntityMessages") != null;
	private List<Entity> serverEntities = new ArrayList<>();

	@Override
	public void onInitialize() {
		final Logger logger = ServerLifecycleTests.LOGGER;

		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			this.serverEntities.add(entity);

			if (PRINT_SERVER_ENTITY_MESSAGES) {
				logger.info("[SERVER] LOADED " + entity.toString() + " - Entities: " + this.serverEntities.size());
			}
		});
	}
}
