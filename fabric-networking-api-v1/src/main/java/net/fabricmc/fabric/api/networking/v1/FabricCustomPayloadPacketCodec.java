package net.fabricmc.fabric.api.networking.v1;

import net.fabricmc.fabric.impl.networking.CustomPayloadTypeProvider;

import net.minecraft.network.PacketByteBuf;

public interface FabricCustomPayloadPacketCodec<B extends PacketByteBuf> {
	void fabric_setPacketCodecProvider(CustomPayloadTypeProvider<B> customPayloadTypeProvider);
}
