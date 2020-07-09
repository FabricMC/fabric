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

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ServerBlockEntityLifecycleTests implements ModInitializer {
	private static boolean PRINT_SERVER_BLOCKENTITY_MESSAGES = System.getProperty("fabric-lifecycle-events-testmod.printServerBlockEntityMessages") != null;
	private List<BlockEntity> serverBlockEntities = new ArrayList<>();

	@Override
	public void onInitialize() {
		final Logger logger = ServerLifecycleTests.LOGGER;

		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
			this.serverBlockEntities.add(blockEntity);

			if (PRINT_SERVER_BLOCKENTITY_MESSAGES) {
				logger.info("[SERVER] LOADED " + Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType()).toString() + " - BlockEntities: " + this.serverBlockEntities.size());
			}
		});

		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			this.serverBlockEntities.remove(blockEntity);

			if (PRINT_SERVER_BLOCKENTITY_MESSAGES) {
				logger.info("[SERVER] UNLOADED " + Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType()).toString() + " - BlockEntities: " + this.serverBlockEntities.size());
			}
		});

		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
			if (minecraftServer.getTicks() % 200 == 0) {
				int entities = 0;

				if (PRINT_SERVER_BLOCKENTITY_MESSAGES) {
					logger.info("[SERVER] Tracked BlockEntities:" + this.serverBlockEntities.size() + " Ticked at: " + minecraftServer.getTicks() + "ticks");
				}

				for (ServerWorld world : minecraftServer.getWorlds()) {
					int worldEntities = world.blockEntities.size();

					if (PRINT_SERVER_BLOCKENTITY_MESSAGES) {
						logger.info("[SERVER] Tracked BlockEntities in " + world.getRegistryKey().toString() + " - " + worldEntities);
					}

					entities += worldEntities;
				}

				if (PRINT_SERVER_BLOCKENTITY_MESSAGES) {
					logger.info("[SERVER] Actual Total BlockEntities: " + entities);
				}

				if (entities != this.serverBlockEntities.size()) {
					// Always print mismatches
					logger.error("[SERVER] Mismatch in tracked blockentities and actual blockentities");
				}
			}
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> {
			logger.info("[SERVER] Disconnected. Tracking: " + this.serverBlockEntities.size() + " blockentities");

			if (this.serverBlockEntities.size() != 0) {
				logger.error("[SERVER] Mismatch in tracked blockentities, expected 0");
			}
		});
	}
}
