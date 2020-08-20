package net.fabricmc.fabric.impl.biome;

import com.mojang.serialization.Lifecycle;

import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biomes.v1.FabricBiomeBuilder;
import net.fabricmc.fabric.api.biomes.v1.event.BiomeLoadingCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistryEntryAddedCallback;

public class FabricBiomesEntrypoint implements ModInitializer, DynamicRegistryEntryAddedCallback<Biome> {
	@Override
	public void onInitialize() {
		DynamicRegistryEntryAddedCallback.event(Registry.BIOME_KEY).register(this);
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	public void onEntryAdded(int rawId, RegistryKey<Biome> key, Biome oldBiome, MutableRegistry<Biome> registry) {
		if (!((HasBeenProcessedProvider) (Object) oldBiome).hasBeenProcessed()) {
			// Create builder, pass to event and rebuild biome
			FabricBiomeBuilder builder = FabricBiomeBuilder.of(oldBiome);
			BiomeLoadingCallback.EVENT.invoker().onBiomeLoading(key, builder);
			Biome newBiome = builder.build();

			// Mark as processed to prevent re-triggering
			((HasBeenProcessedProvider) (Object) newBiome).setProcessed();
			registry.set(rawId, key, newBiome, Lifecycle.stable());
		}
	}
}
