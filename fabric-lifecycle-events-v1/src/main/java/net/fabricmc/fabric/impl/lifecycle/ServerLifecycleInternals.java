package net.fabricmc.fabric.impl.lifecycle;

import java.util.function.Supplier;

import net.minecraft.server.MinecraftServer;

public abstract class ServerLifecycleInternals {
	private static Supplier<MinecraftServer> SERVER_SUPPLIER;

	/* @Nullable */
	public static MinecraftServer getServer() {
		return SERVER_SUPPLIER.get();
	}

	protected ServerLifecycleInternals(Supplier<MinecraftServer> supplier) {
		SERVER_SUPPLIER = supplier;
	}
}
