package net.fabricmc.fabric.api.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.impl.network.ClientPacketRegistryImpl;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

/**
 * The client->server packet registry.
 */
public interface ClientPacketRegistry extends PacketRegistry {
	static final ClientPacketRegistry INSTANCE = new ClientPacketRegistryImpl();

	boolean canServerReceive(Identifier id);

	void sendToServer(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener);

	default void sendToServer(Identifier id, PacketByteBuf buf, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		sendToServer(toPacket(id, buf), completionListener);
	}

	default void sendToServer(Packet<?> packet) {
		sendToServer(packet, null);
	}

	default void sendToServer(Identifier id, PacketByteBuf buf) {
		sendToServer(id, buf, null);
	}
}
