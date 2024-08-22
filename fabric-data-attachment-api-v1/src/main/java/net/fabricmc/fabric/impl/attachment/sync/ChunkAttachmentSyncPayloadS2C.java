package net.fabricmc.fabric.impl.attachment.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

/*
 * Packet used for syncing attachment data for a single *chunk*. Includes data attached to it as well as BE data.
 * Entities and worlds are handled separately.
 */
// TODO Should there be an individual BE version for incremental changes afterwards?
public record ChunkAttachmentSyncPayloadS2C(
		RegistryKey<World> dimension,
		ChunkPos chunkPos,
		AttachmentChanges chunkData,
		Map<BlockPos, AttachmentChanges> blockEntityData
) {
	public static final PacketCodec<PacketByteBuf, ChunkAttachmentSyncPayloadS2C> CODEC = PacketCodec.tuple(
			RegistryKey.createPacketCodec(RegistryKeys.WORLD), ChunkAttachmentSyncPayloadS2C::dimension,
			PacketCodecs.VAR_LONG.xmap(ChunkPos::new, ChunkPos::toLong), ChunkAttachmentSyncPayloadS2C::chunkPos,
			AttachmentChanges.PACKET_CODEC, ChunkAttachmentSyncPayloadS2C::chunkData,
			PacketCodecs.map(HashMap::new, BlockPos.PACKET_CODEC, AttachmentChanges.PACKET_CODEC), ChunkAttachmentSyncPayloadS2C::blockEntityData,
			ChunkAttachmentSyncPayloadS2C::new
	);
}
