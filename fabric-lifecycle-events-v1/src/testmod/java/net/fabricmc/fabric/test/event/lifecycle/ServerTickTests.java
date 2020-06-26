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

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

/**
 * Test related to ticking events on the server.
 */
public class ServerTickTests implements ModInitializer {
	private Map<RegistryKey<World>, Integer> tickTracker = new HashMap<>();

	@Override
	public void onInitialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTicks() % 200 == 0) { // Log every 200 ticks to verify the tick callback works on the server
				ServerLifecycleTests.LOGGER.info("Ticked Server at " + server.getTicks() + " ticks.");
			}
		});

		ServerTickEvents.START_WORLD_TICK.register(world -> {
			// Verify we are inside the tick
			if (!world.isInBlockTick()) {
				throw new AssertionError("Start tick event should be fired while ServerWorld is inside of block tick");
			}
		});

		ServerTickEvents.END_WORLD_TICK.register(world -> {
			final int worldTicks = tickTracker.computeIfAbsent(world.getRegistryKey(), k -> 0);

			if (worldTicks % 200 == 0) { // Log every 200 ticks to verify the tick callback works on the server world
				ServerLifecycleTests.LOGGER.info("Ticked Server World - " + worldTicks + " ticks:" + world.getRegistryKey().getValue());
			}

			this.tickTracker.put(world.getRegistryKey(), worldTicks + 1);
		});
	}
}
