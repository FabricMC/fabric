package net.fabricmc.fabric.impl.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public interface CustomPayloadTypeProvider<B extends PacketByteBuf> {
	 CustomPayload.Type<B, ? extends CustomPayload> get(B packetByteBuf, Identifier identifier);
}
