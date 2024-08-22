package net.fabricmc.fabric.impl.attachment.sync;

import java.util.Map;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class AttachmentDataPacketCodec implements PacketCodec<PacketByteBuf, Map<AttachmentType<?>, Object>> {
	public static final AttachmentDataPacketCodec INSTANCE = new AttachmentDataPacketCodec();

	private AttachmentDataPacketCodec() {
	}

	@Override
	public Map<AttachmentType<?>, Object> decode(PacketByteBuf buf) {
		return Map.of();
	}

	@Override
	public void encode(PacketByteBuf buf, Map<AttachmentType<?>, Object> value) {
	}
}
