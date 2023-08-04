package net.fabricmc.fabric.impl.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponse;

public record PacketByteBufLoginQueryResponse(PacketByteBuf data) implements LoginQueryResponse {
	@Override
	public void write(PacketByteBuf buf) {
		buf.writeBytes(this.data());
	}
}
