package net.fabricmc.fabric.api.biomes.v1.event;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.api.biomes.v1.FabricBiomeBuilder;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface BiomeLoadingCallback {
	Event<BiomeLoadingCallback> EVENT = EventFactory.createArrayBacked(
			BiomeLoadingCallback.class,
			(listeners) -> (biomeRegistryKey, biomeBuilder) -> {
				for (BiomeLoadingCallback callback : listeners) {
					callback.onBiomeLoading(biomeRegistryKey, biomeBuilder);
				}
			}
	);

	void onBiomeLoading(RegistryKey<Biome> biomeRegistryKey, FabricBiomeBuilder biomeBuilder);
}
