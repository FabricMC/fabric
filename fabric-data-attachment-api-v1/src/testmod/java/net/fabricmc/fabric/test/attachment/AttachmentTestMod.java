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
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class AttachmentTestMod implements ModInitializer {
	public static final String MOD_ID = "fabric-data-attachment-api-v1-testmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final AttachmentType<String> PERSISTENT = AttachmentRegistry.createPersistent(
			new Identifier(MOD_ID, "persistent"),
			Codec.STRING
	);
	public static final ChunkPos FAR_CHUNK_POS = new ChunkPos(30, 0);

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

				ProtoChunk protoChunk = (ProtoChunk) overworld.getChunkManager().getChunk(FAR_CHUNK_POS.x, FAR_CHUNK_POS.z, ChunkStatus.STRUCTURE_STARTS, true);
				protoChunk.setAttached(PERSISTENT, "protochunk_data");
			} else {
				LOGGER.info("Second launch, testing");

				overworld = server.getOverworld();
				if (!"world_data".equals(overworld.getAttached(PERSISTENT))) throw new AssertionError("World attachement did not persist");

				chunk = overworld.getChunk(0, 0);
				if (!"chunk_data".equals(chunk.getAttached(PERSISTENT))) throw new AssertionError("WorldChunk attachement did not persist");

				WrapperProtoChunk wrapperProtoChunk = (WrapperProtoChunk) overworld.getChunkManager().getChunk(0, 0, ChunkStatus.EMPTY, true);
				if (!"chunk_data".equals(wrapperProtoChunk.getAttached(PERSISTENT))) throw new AssertionError("Attachement is not accessible through WrapperProtoChunk");

				Chunk farChunk = overworld.getChunkManager().getChunk(FAR_CHUNK_POS.x, FAR_CHUNK_POS.z, ChunkStatus.EMPTY, true);
				if (farChunk instanceof WrapperProtoChunk){
					LOGGER.warn("Far chunk alread generated, can't test persistence in ProtoChunk.");
				} else {
					if (!"protochunk_data".equals(farChunk.getAttached(PERSISTENT))) throw new AssertionError("ProtoChunk attachement did not persist");
				}
			}
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> firstLaunch = false);

		// Testing hint: load far chunk by running /tp @s 480 ~ 0
		ServerChunkEvents.CHUNK_LOAD.register(((world, chunk) -> {
			if (!chunk.getPos().equals(FAR_CHUNK_POS)) return;

			LOGGER.info("Loaded chunk {}, testing", FAR_CHUNK_POS);

			if (!"protochunk_data".equals(chunk.getAttached(PERSISTENT))) throw new AssertionError("ProtoChunk attachement was not transfered to WorldChunk");
		}));
	}
}
