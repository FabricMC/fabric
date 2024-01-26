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

package net.fabricmc.fabric.test.attachment;

import com.mojang.serialization.Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class AttachmentTestMod implements ModInitializer {
	public static final String MOD_ID = "fabric-data-attachment-api-v1-testmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final AttachmentType<String> PERSISTENT = AttachmentRegistry.createPersistent(
			new Identifier(MOD_ID, "persistent"),
			Codec.STRING
	);

	private boolean firstLaunch = true;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			ServerWorld overworld;
			WorldChunk chunk;

			if (firstLaunch) {
				LOGGER.info("First launch, setting up");

				overworld = server.getOverworld();
				overworld.setAttached(PERSISTENT, "world_data");

				chunk = overworld.getChunk(0, 0);
				chunk.setAttached(PERSISTENT, "chunk_data");
			} else {
				LOGGER.info("Second launch, testing");

				overworld = server.getOverworld();
				if (!"world_data".equals(overworld.getAttached(PERSISTENT))) throw new AssertionError();

				chunk = overworld.getChunk(0, 0);
				if (!"chunk_data".equals(chunk.getAttached(PERSISTENT))) throw new AssertionError();
			}
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> firstLaunch = false);
	}
}
