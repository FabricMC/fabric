package net.fabricmc.fabric.impl.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerPacketRegistry;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.Collection;
import java.util.HashSet;
import java.util.WeakHashMap;

public class ServerPacketRegistryImpl extends PacketRegistryImpl implements ServerPacketRegistry {
	private static final WeakHashMap<PlayerEntity, Collection<Identifier>> playerPayloadIds = new WeakHashMap<>();

	@Override
	public boolean canPlayerReceive(PlayerEntity player, Identifier id) {
		Collection<Identifier> ids = playerPayloadIds.get(player);
		if (ids != null) {
			return ids.contains(id);
		} else {
			return false;
		}
	}

	@Override
	public void sendToPlayer(PlayerEntity player, Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		if (!(player instanceof ServerPlayerEntity)) {
			throw new RuntimeException("Can only send to ServerPlayerEntities!");
		} else {
			((ServerPlayerEntity) player).networkHandler.sendPacket(packet, completionListener);
		}
	}

	@Override
	public Packet<?> toPacket(Identifier id, PacketByteBuf buf) {
		return new CustomPayloadClientPacket(id, buf);
	}

	@Override
	protected void onRegister(Identifier id) {
		// TODO: allow dynamic
	}

	@Override
	protected void onUnregister(Identifier id) {
		// TODO: allow dynamic
	}

	@Override
	protected Collection<Identifier> getIdCollectionFor(PacketContext context) {
		return playerPayloadIds.computeIfAbsent(context.getPlayer(), (p) -> new HashSet<>());
	}
}
