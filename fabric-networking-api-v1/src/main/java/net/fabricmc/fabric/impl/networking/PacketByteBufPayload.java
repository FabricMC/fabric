package net.fabricmc.fabric.impl.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PacketByteBufPayload(Identifier id, PacketByteBuf data) implements CustomPayload {
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBytes(this.data());
	}
}
