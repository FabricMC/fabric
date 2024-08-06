package net.fabricmc.fabric.impl.modprotocol;

import net.minecraft.server.ServerMetadata;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ServerMetadataExtension {
	static ServerMetadataExtension of(ServerMetadata input) {
		return (ServerMetadataExtension) (Object) input;
	}

	@Nullable
	List<ModProtocol> fabric$getModProtocol();
	void fabric$setModProtocol(List<ModProtocol> protocol);

}
