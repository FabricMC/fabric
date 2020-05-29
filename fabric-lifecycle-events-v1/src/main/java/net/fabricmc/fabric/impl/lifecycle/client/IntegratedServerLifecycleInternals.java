package net.fabricmc.fabric.impl.lifecycle.client;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.lifecycle.ServerLifecycleInternals;
import net.fabricmc.loader.api.FabricLoader;

@Environment(EnvType.CLIENT)
public final class IntegratedServerLifecycleInternals extends ServerLifecycleInternals implements ClientModInitializer {
	public IntegratedServerLifecycleInternals() {
		// On an a client, the server is in the client
		super(() -> {
			// Get the client
			final MinecraftClient client = (MinecraftClient) FabricLoader.getInstance().getGameInstance();

			// And return the client's integrated server
			return client.getServer();
		});
	}

	@Override
	public final void onInitializeClient() {
	}
}
