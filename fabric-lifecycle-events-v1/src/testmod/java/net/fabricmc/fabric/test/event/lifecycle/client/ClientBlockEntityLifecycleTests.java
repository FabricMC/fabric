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

package net.fabricmc.fabric.test.event.lifecycle.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.test.event.lifecycle.ServerLifecycleTests;

public class ClientBlockEntityLifecycleTests implements ClientModInitializer {
	private static boolean PRINT_CLIENT_BLOCKENTITY_MESSAGES = System.getProperty("fabric-lifecycle-events-testmod.printClientBlockEntityMessages") != null;
	private List<BlockEntity> clientBlockEntities = new ArrayList<>();
	private int clientTicks;

	@Override
	public void onInitializeClient() {
		final Logger logger = ServerLifecycleTests.LOGGER;

		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
			this.clientBlockEntities.add(blockEntity);

			if (PRINT_CLIENT_BLOCKENTITY_MESSAGES) {
				logger.info("[CLIENT]" + " LOADED " + Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType()).toString() + " - BlockEntities: " + this.clientBlockEntities.size());
			}
		});

		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			this.clientBlockEntities.remove(blockEntity);

			if (PRINT_CLIENT_BLOCKENTITY_MESSAGES) {
				logger.info("[CLIENT]" + " UNLOADED " + Registry.BLOCK_ENTITY_TYPE.getId(blockEntity.getType()).toString() + " - BlockEntities: " + this.clientBlockEntities.size());
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (this.clientTicks++ % 200 == 0 && client.world != null) {
				final int blockEntities = client.world.blockEntities.size();

				if (PRINT_CLIENT_BLOCKENTITY_MESSAGES) {
					logger.info("[CLIENT] Tracked BlockEntities:" + this.clientBlockEntities.size() + " Ticked at: " + this.clientTicks + "ticks");
					logger.info("[CLIENT] Actual BlockEntities: " + client.world.blockEntities.size());
				}

				if (blockEntities != this.clientBlockEntities.size()) {
					if (PRINT_CLIENT_BLOCKENTITY_MESSAGES) {
						logger.error("[CLIENT] Mismatch in tracked blockentities and actual blockentities");
					}
				}
			}
		});

		ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> {
			if (!minecraftServer.isDedicated()) { // fixme: Use ClientNetworking#PLAY_DISCONNECTED instead of the server stop callback for testing.
				logger.info("[CLIENT] Disconnected. Tracking: " + this.clientBlockEntities.size() + " blockentities");

				if (this.clientBlockEntities.size() != 0) {
					logger.error("[CLIENT] Mismatch in tracked blockentities, expected 0");
				}
			}
		});
	}
}
