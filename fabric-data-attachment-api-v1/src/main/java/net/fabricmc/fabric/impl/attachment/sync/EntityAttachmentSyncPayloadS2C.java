package net.fabricmc.fabric.impl.attachment.sync;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record EntityAttachmentSyncPayloadS2C(int entityId, AttachmentChanges data) {
	public static final PacketCodec<PacketByteBuf, EntityAttachmentSyncPayloadS2C> CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT, EntityAttachmentSyncPayloadS2C::entityId,
			AttachmentChanges.PACKET_CODEC, EntityAttachmentSyncPayloadS2C::data,
			EntityAttachmentSyncPayloadS2C::new
	);
}
