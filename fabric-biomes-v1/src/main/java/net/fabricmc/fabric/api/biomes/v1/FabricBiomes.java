package net.fabricmc.fabric.api.biomes.v1;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.biome.FabricBiomesInternal;

public final class FabricBiomes {
	private FabricBiomes() { }

	public static RegistryKey<Biome> register(RegistryKey<Biome> key, Biome biome) {
		return FabricBiomesInternal.register(key, biome);
	}

	public static void addToOverworld(RegistryKey<Biome> key) {
		FabricBiomesInternal.addToOverworld(key);
	}

	public static void addToNether(RegistryKey<Biome> key, Biome.MixedNoisePoint mixedNoisePoint) {
		FabricBiomesInternal.addToNether(key, mixedNoisePoint);
	}
}
