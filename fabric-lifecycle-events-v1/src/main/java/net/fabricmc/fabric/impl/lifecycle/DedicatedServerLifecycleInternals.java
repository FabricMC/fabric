package net.fabricmc.fabric.impl.lifecycle;

import net.minecraft.server.MinecraftServer;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class DedicatedServerLifecycleInternals extends ServerLifecycleInternals implements DedicatedServerModInitializer {
	public DedicatedServerLifecycleInternals() {
		// On a dedicated server, the game instance is the server
		super(() -> (MinecraftServer) FabricLoader.getInstance().getGameInstance());
	}

	@Override
	public void onInitializeServer() {
	}
}
