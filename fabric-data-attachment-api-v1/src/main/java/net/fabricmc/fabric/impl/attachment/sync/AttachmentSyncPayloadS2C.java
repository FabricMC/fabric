package net.fabricmc.fabric.impl.attachment.sync;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record AttachmentSyncPayloadS2C(AttachmentChanges changes) {
	public static final PacketCodec<PacketByteBuf, AttachmentSyncPayloadS2C> CODEC = PacketCodec.tuple(
			AttachmentChanges.PACKET_CODEC, AttachmentSyncPayloadS2C::changes, AttachmentSyncPayloadS2C::new
	);
}
