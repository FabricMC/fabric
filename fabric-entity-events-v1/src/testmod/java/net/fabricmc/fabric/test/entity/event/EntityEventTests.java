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

package net.fabricmc.fabric.test.entity.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public final class EntityEventTests implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger(EntityEventTests.class);

	@Override
	public void onInitialize() {
		ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killed) -> {
			LOGGER.info("Entity Killed: {}", killed);
		});

		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
			LOGGER.info("Moved player {}: [{} -> {}]", player, origin.getRegistryKey().getValue(), destination.getRegistryKey().getValue());
		});

		ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register((originalEntity, newEntity, origin, destination) -> {
			LOGGER.info("Moved entity {} -> {}: [({} -> {}]", originalEntity, newEntity, origin.getRegistryKey().getValue(), destination.getRegistryKey().getValue());
		});

		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			LOGGER.info("Copied data for {} from {} to {}", oldPlayer.getGameProfile().getName(), oldPlayer, newPlayer);
		});

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			LOGGER.info("Respawned {}, [{}, {}]", oldPlayer.getGameProfile().getName(), oldPlayer.getServerWorld().getRegistryKey().getValue(), newPlayer.getServerWorld().getRegistryKey().getValue());
		});
	}
}
