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

import org.slf4j.Logger;

import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

/**
 * Tests related to the lifecycle of chunks.
 */
public class ServerChunkLifecycleTests implements ModInitializer {
	private static final boolean PRINT_SERVER_CHUNK_MESSAGES = System.getProperty("fabric-lifecycle-events-testmod.printServerChunkMessages") != null;

	private final List<WorldChunk> chunks = new ArrayList<>();

	@Override
	public void onInitialize() {
		final Logger logger = ServerLifecycleTests.LOGGER;

		ServerChunkEvents.CHUNK_TICK.register((world, chunk) -> {
			if (PRINT_SERVER_CHUNK_MESSAGES) {
				logger.info("[SERVER] TICKING CHUNK AT x={} z={} - Tracking {} Chunks", chunk.getPos().x, chunk.getPos().z, this.chunks.size());
			}
		});

		ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
			this.chunks.add(chunk);

			if (PRINT_SERVER_CHUNK_MESSAGES) {
				logger.info("[SERVER] LOADED CHUNK AT x={} z={} - Tracking {} Chunks", chunk.getPos().x, chunk.getPos().z, this.chunks.size());
			}
		});

		ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
			this.chunks.remove(chunk);

			if (PRINT_SERVER_CHUNK_MESSAGES) {
				logger.info("[SERVER] UNLOADED CHUNK AT x={} z={} - Tracking {} Chunks", chunk.getPos().x, chunk.getPos().z, this.chunks.size());
			}
		});
	}
}
