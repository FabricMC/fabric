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

package net.fabricmc.fabric.test.client.event.interaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;

public class ClientPlayerBlockBreakTests implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(ClientPlayerBlockBreakTests.class);

	@Override
	public void onInitializeClient() {
		ClientPlayerBlockBreakEvents.AFTER.register(((world, player, pos, state, entity) -> LOGGER.info("Block broken at {}, {}, {} (client-side = {})", pos.getX(), pos.getY(), pos.getZ(), world.isClient())));
	}
}
