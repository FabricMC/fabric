package net.fabricmc.fabric.impl.network.login;

import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.Optional;

public interface ClientLoginQueryResponder {
	Optional<PacketByteBuf> respond(ClientLoginNetworkHandler handler, ClientConnection connection, Identifier id, PacketByteBuf buffer);
}
