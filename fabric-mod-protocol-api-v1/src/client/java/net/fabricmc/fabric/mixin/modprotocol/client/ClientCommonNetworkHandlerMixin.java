package net.fabricmc.fabric.mixin.modprotocol.client;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import net.minecraft.client.network.ClientCommonNetworkHandler;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerCommonNetworkHandler;

import net.fabricmc.fabric.impl.modprotocol.RemoteProtocolStorage;

@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin implements RemoteProtocolStorage {
	@Shadow
	@Final
	protected ClientConnection connection;

	@Override
	public Object2IntMap<String> fabric$getRemoteProtocol() {
		return ((RemoteProtocolStorage) this.connection).fabric$getRemoteProtocol();
	}

	@Override
	public void fabric$setRemoteProtocol(Object2IntMap<String> protocol) {
		((RemoteProtocolStorage) this.connection).fabric$setRemoteProtocol(protocol);
	}
}
