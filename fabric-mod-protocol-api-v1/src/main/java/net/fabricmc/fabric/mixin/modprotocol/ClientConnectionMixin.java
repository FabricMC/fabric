package net.fabricmc.fabric.mixin.modprotocol;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import it.unimi.dsi.fastutil.objects.Object2IntMaps;

import net.fabricmc.fabric.impl.modprotocol.RemoteProtocolStorage;

import net.minecraft.network.ClientConnection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin implements RemoteProtocolStorage {
	@Unique
	private Object2IntMap<String> remoteProtocol = Object2IntMaps.emptyMap();

	@Override
	public Object2IntMap<String> fabric$getRemoteProtocol() {
		return this.remoteProtocol;
	}

	@Override
	public void fabric$setRemoteProtocol(Object2IntMap<String> protocol) {
		this.remoteProtocol = protocol;
	}
}
