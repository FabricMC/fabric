package net.fabricmc.fabric.impl.attachment.sync;

import java.util.List;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record AttachmentSyncPayload(List<AttachmentChange> attachments) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, AttachmentSyncPayload> CODEC = PacketCodec.tuple(
			AttachmentChange.PACKET_CODEC.collect(PacketCodecs.toList()), AttachmentSyncPayload::attachments,
			AttachmentSyncPayload::new
	);
	public static final Id<AttachmentSyncPayload> ID = new Id<>(AttachmentSync.PACKET_ID);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
