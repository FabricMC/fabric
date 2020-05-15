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
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Tests related to the lifecycle of entities.
 */
public class ServerEntityLifecycleTests implements ModInitializer {
	private List<Entity> serverEntities = new ArrayList<>();

	@Override
	public void onInitialize() {
		final Logger logger = ServerLifecycleTests.LOGGER;

		ServerLifecycleEvents.ENTITY_LOAD.register((entity, world) -> {
			this.serverEntities.add(entity);
			logger.info("[SERVER] LOADED " + entity.toString() + " - Entities: " + this.serverEntities.size());
		});

		ServerLifecycleEvents.ENTITY_UNLOAD.register((entity, world) -> {
			this.serverEntities.remove(entity);
			logger.info("[SERVER] UNLOADED " + entity.toString() + " - Entities: " + this.serverEntities.size());
		});

		ServerLifecycleEvents.SERVER_TICK.register(minecraftServer -> {
			if (minecraftServer.getTicks() % 200 == 0) {
				final List<Entity> entities = new ArrayList<>();

				for (ServerWorld world : minecraftServer.getWorlds()) {
					List<Entity> worldEntities = world.getEntities(null, entity -> true);
					logger.info("[SERVER] Tracked Entities in " + world.dimension.getType().toString() + " - " + worldEntities.size());
					entities.addAll(worldEntities);
				}

				logger.info("[SERVER] Tracked Entities: " + this.serverEntities.size() + " Ticked at: " + minecraftServer.getTicks() + "ticks");
				logger.info("[SERVER] Actual Total Entities: " + entities.size());

				if (entities.size() != this.serverEntities.size()) {
					logger.error("[SERVER] Mismatch in tracked entities and actual entities");
					//
					List<Entity> temp = new ArrayList<>(this.serverEntities);
					temp.removeAll(entities);

					for (Entity entity : temp) {
						logger.error(entity.toString());
					}
				}
			}
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> {
			logger.info("[SERVER] Disconnected. Tracking: " + this.serverEntities.size() + " entities");

			if (this.serverEntities.size() != 0) {
				logger.error("[SERVER] Mismatch in tracked entities, expected 0");

				for (Entity serverEntity : this.serverEntities) {
					logger.error(serverEntity.toString());
				}
			}
		});
	}
}
