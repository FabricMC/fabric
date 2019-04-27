package net.fabricmc.fabric.impl.network.login;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.function.Consumer;

public interface S2CLoginQueryQueue {
	@FunctionalInterface
	interface Receiver {
		void onResponse(ServerLoginNetworkHandler handler, ClientConnection connection, Identifier id, PacketByteBuf response);
	}

	void sendPacket(Identifier id, PacketByteBuf packet, Receiver receiver);
}
