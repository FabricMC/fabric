package net.fabricmc.fabric.impl.networking.payload;

import net.minecraft.network.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

public class PayloadHelper {
	public static void write(PacketByteBuf byteBuf, PacketByteBuf data) {
		byteBuf.writeBytes(data.copy());
	}

	public static PacketByteBuf read(PacketByteBuf byteBuf) {
		PacketByteBuf newBuf = PacketByteBufs.create();
		newBuf.writeBytes(byteBuf.copy());
		return newBuf;
	}

	public static PacketByteBuf reset(PacketByteBuf byteBuf) {
//		byteBuf.resetReaderIndex();
////		byteBuf.release();
		return byteBuf;
	}
}
