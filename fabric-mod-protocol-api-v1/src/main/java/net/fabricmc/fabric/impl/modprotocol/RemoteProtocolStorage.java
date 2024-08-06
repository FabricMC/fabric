package net.fabricmc.fabric.impl.modprotocol;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

public interface RemoteProtocolStorage {
	Object2IntMap<String> fabric$getRemoteProtocol();
	void fabric$setRemoteProtocol(Object2IntMap<String> protocol);
}
