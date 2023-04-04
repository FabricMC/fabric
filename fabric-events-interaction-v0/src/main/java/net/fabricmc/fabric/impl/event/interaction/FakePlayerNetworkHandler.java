package net.fabricmc.fabric.impl.event.interaction;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class FakePlayerNetworkHandler extends ServerPlayNetworkHandler {
	private static final ClientConnection FAKE_CONNECTION = new ClientConnection(NetworkSide.CLIENTBOUND);

	public FakePlayerNetworkHandler(ServerPlayerEntity player) {
		super(player.getServer(), FAKE_CONNECTION, player);
	}
}
