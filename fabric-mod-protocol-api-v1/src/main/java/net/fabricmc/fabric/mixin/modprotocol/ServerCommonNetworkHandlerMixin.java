package net.fabricmc.fabric.mixin.modprotocol;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import net.fabricmc.fabric.impl.modprotocol.RemoteProtocolStorage;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerCommonNetworkHandler;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerCommonNetworkHandler.class)
public class ServerCommonNetworkHandlerMixin implements RemoteProtocolStorage {
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
