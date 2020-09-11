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

package net.fabricmc.fabric.test.event.interaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Blocks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientBlockPlaceEvent;

public class ClientBlockPlaceEvents implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("InteractionEventsTest");

	@Override
	public void onInitialize() {
		ClientBlockPlaceEvent.BEFORE.register((world, player, pos, state) -> {
			LOGGER.info("Client Block place event started at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
			return state.getBlock() != Blocks.SPONGE;
		});

		ClientBlockPlaceEvent.CANCELED.register((world, player, pos, state) -> {
			LOGGER.info("Client Block place event canceled at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
		});

		ClientBlockPlaceEvent.AFTER.register((world, player, pos, state) -> {
			LOGGER.info("Client Block placed at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
		});
	}
}
