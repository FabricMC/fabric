package net.fabricmc.fabric.impl.attachment.sync;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public record WorldAttachmentSyncPayloadS2C(RegistryKey<World> dimension, AttachmentChanges data) {
	public static final PacketCodec<PacketByteBuf, WorldAttachmentSyncPayloadS2C> CODEC = PacketCodec.tuple(
			RegistryKey.createPacketCodec(RegistryKeys.WORLD), WorldAttachmentSyncPayloadS2C::dimension,
			AttachmentChanges.PACKET_CODEC, WorldAttachmentSyncPayloadS2C::data,
			WorldAttachmentSyncPayloadS2C::new
	);
}
