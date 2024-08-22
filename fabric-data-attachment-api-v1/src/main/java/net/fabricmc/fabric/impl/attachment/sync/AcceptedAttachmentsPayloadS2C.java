package net.fabricmc.fabric.impl.attachment.sync;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class AcceptedAttachmentsPayloadS2C implements CustomPayload {
	public static final AcceptedAttachmentsPayloadS2C INSTANCE = new AcceptedAttachmentsPayloadS2C();
	public static final Id<AcceptedAttachmentsPayloadS2C> ID = new Id<>(AttachmentSync.CONFIG_PACKET_ID);
	public static final PacketCodec<PacketByteBuf, AcceptedAttachmentsPayloadS2C> CODEC = PacketCodec.unit(INSTANCE);

	private AcceptedAttachmentsPayloadS2C() {
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
