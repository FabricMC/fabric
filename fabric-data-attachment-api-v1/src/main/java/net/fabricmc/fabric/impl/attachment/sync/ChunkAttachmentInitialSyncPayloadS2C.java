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

package net.fabricmc.fabric.impl.attachment.sync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

/*
 * Packet used for syncing attachment data in a single chunk. Includes data attached to it as well as BE data.
 * Entities and worlds are handled separately.
 *
 * This is only for *initial* syncing of BE attachments, i.e. when the player is logging on and loading chunks,
 * where sending 1 packet per BE would be terrible in terms of performance.
 *
 * For later BE data, see BlockEntityAttachmentChangePayloadS2C.
 */
public record ChunkAttachmentInitialSyncPayloadS2C(
		ChunkPos chunkPos,
		List<AttachmentChange> chunkData,
		Map<BlockPos, List<AttachmentChange>> initialBlockEntityData
) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, ChunkAttachmentInitialSyncPayloadS2C> CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_LONG.xmap(ChunkPos::new, ChunkPos::toLong), ChunkAttachmentInitialSyncPayloadS2C::chunkPos,
			AttachmentChange.LIST_PACKET_CODEC, ChunkAttachmentInitialSyncPayloadS2C::chunkData,
			PacketCodecs.map(HashMap::new, BlockPos.PACKET_CODEC, AttachmentChange.LIST_PACKET_CODEC), ChunkAttachmentInitialSyncPayloadS2C::initialBlockEntityData,
			ChunkAttachmentInitialSyncPayloadS2C::new
	);
	public static final Id<ChunkAttachmentInitialSyncPayloadS2C> ID = new Id<>(AttachmentSync.CHUNK_INITIAL_PACKET_ID);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
