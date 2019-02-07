package net.fabricmc.fabric.api.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.impl.network.ClientPacketRegistryImpl;
import net.fabricmc.fabric.impl.network.ServerPacketRegistryImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

/**
 * The client->server packet registry.
 */
public interface ServerPacketRegistry extends PacketRegistry {
	static final ServerPacketRegistry INSTANCE = new ServerPacketRegistryImpl();

	boolean canPlayerReceive(PlayerEntity player, Identifier id);

	void sendToPlayer(PlayerEntity player, Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener);

	default void sendToPlayer(PlayerEntity player, Identifier id, PacketByteBuf buf, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		sendToPlayer(player, toPacket(id, buf), completionListener);
	}

	default void sendToPlayer(PlayerEntity player, Packet<?> packet) {
		sendToPlayer(player, packet, null);
	}

	default void sendToPlayer(PlayerEntity player, Identifier id, PacketByteBuf buf) {
		sendToPlayer(player, id, buf, null);
	}
}
