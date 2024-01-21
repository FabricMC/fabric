package net.fabricmc.fabric.impl.registry.sync;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SyncCompletePayload() implements CustomPayload {
	public static final CustomPayload.Id<SyncCompletePayload> ID = new CustomPayload.Id<>(new Identifier("fabric", "registry/sync/complete"));
	public static final PacketCodec<PacketByteBuf, SyncCompletePayload> CODEC = CustomPayload.codecOf((value, buf) -> {}, buf -> new SyncCompletePayload());

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
