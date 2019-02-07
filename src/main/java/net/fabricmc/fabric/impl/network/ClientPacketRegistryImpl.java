package net.fabricmc.fabric.impl.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.network.ClientPacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.CustomPayloadServerPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.Collection;
import java.util.HashSet;

public class ClientPacketRegistryImpl extends PacketRegistryImpl implements ClientPacketRegistry {
	private static final Collection<Identifier> serverPayloadIds = new HashSet<>();

	@Override
	public boolean canServerReceive(Identifier id) {
		return serverPayloadIds.contains(id);
	}

	@Override
	public void sendToServer(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		ClientPlayNetworkHandler handler = MinecraftClient.getInstance().getNetworkHandler();
		if (handler != null) {
			if (completionListener == null) {
				// stay closer to the vanilla codepath
				handler.sendPacket(packet);
			} else {
				handler.getClientConnection().sendPacket(packet, completionListener);
			}
		} else {
			// TODO: log warning
		}
	}

	@Override
	public Packet<?> toPacket(Identifier id, PacketByteBuf buf) {
		return new CustomPayloadServerPacket(id, buf);
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
		return serverPayloadIds;
	}
}
