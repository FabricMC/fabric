package net.fabricmc.fabric.api.biomes.v1;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.biome.FabricBiomesInternal;

public final class FabricBiomes {
	private FabricBiomes() { }

	public static RegistryKey<Biome> register(RegistryKey<Biome> key, Biome biome) {
		return FabricBiomesInternal.register(key, biome);
	}

	public static RegistryKey<Biome> addToOverworld(RegistryKey<Biome> key) {
		return FabricBiomesInternal.addToOverworld(key);
	}

	public static RegistryKey<Biome> addToNether(RegistryKey<Biome> key, Biome.MixedNoisePoint mixedNoisePoint) {
		return FabricBiomesInternal.addToNether(key, mixedNoisePoint);
	}
}
