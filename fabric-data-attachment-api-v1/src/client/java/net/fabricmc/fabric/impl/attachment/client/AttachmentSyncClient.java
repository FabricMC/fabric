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

package net.fabricmc.fabric.impl.attachment.client;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.chunk.Chunk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.attachment.sync.AcceptedAttachmentsPayloadS2C;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSync;
import net.fabricmc.fabric.impl.attachment.sync.BlockEntityAttachmentChangePayloadS2C;
import net.fabricmc.fabric.impl.attachment.sync.ChunkAttachmentInitialSyncPayloadS2C;
import net.fabricmc.fabric.impl.attachment.sync.EntityAttachmentChangePayloadS2C;
import net.fabricmc.fabric.impl.attachment.sync.WorldAttachmentChangePayloadS2C;

public class AttachmentSyncClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// config
		ClientConfigurationNetworking.registerGlobalReceiver(AcceptedAttachmentsPayloadS2C.ID, (payload, context) -> {
			context.responseSender().sendPacket(AttachmentSync.createResponsePayload());
		});

		// play
		ClientPlayNetworking.registerGlobalReceiver(WorldAttachmentChangePayloadS2C.ID, (payload, context) -> {
			// after game join packet, so world shouldn't be null
			payload.changes().forEach(change -> change.apply(context.client().world));
		});

		ClientPlayNetworking.registerGlobalReceiver(EntityAttachmentChangePayloadS2C.ID, (payload, context) -> {
			Entity target = context.client().world.getEntityById(payload.entityId());

			if (target != null) {
				payload.changes().forEach(change -> change.apply(target));
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(BlockEntityAttachmentChangePayloadS2C.ID, (payload, context) -> {
			BlockEntity target = context.client().world.getBlockEntity(payload.pos());

			// can it even be null?
			if (target != null) {
				payload.change().apply(target);
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(ChunkAttachmentInitialSyncPayloadS2C.ID, (payload, context) -> {
			Chunk chunk = context.client().world.getChunk(payload.chunkPos().x, payload.chunkPos().z);
			payload.chunkData().forEach(change -> change.apply(chunk));

			payload.initialBlockEntityData()
					.forEach((pos, changes) -> {
						BlockEntity be = chunk.getBlockEntity(pos);

						if (be != null) {
							changes.forEach(change -> change.apply(be));
						}
					});
		});
	}
}
