package net.fabricmc.fabric.impl.biome;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.mixin.biome.DynamicRegistryManagerAccessor;

public class FabricBiomesInternal {
	private static final Set<RegistryKey<Biome>> OVERWORLD_BIOMES = Sets.newHashSet();
	private static final Map<RegistryKey<Biome>, Biome.MixedNoisePoint> NETHER_BIOMES = Maps.newHashMap();

	public static RegistryKey<Biome> register(RegistryKey<Biome> key, Biome biome) {
		Registry<Biome> registry = DynamicRegistryManagerAccessor.getBuiltin().get(Registry.BIOME_KEY);
		Registry.register(registry, key.getValue(), biome);
		return key;
	}

	public static RegistryKey<Biome> addToOverworld(RegistryKey<Biome> key) {
		OVERWORLD_BIOMES.add(key);
		return key;
	}

	public static RegistryKey<Biome> addToNether(RegistryKey<Biome> key, Biome.MixedNoisePoint mixedNoisePoint) {
		NETHER_BIOMES.put(key, mixedNoisePoint);
		return key;
	}

	public static Set<RegistryKey<Biome>> getOverworldBiomes() {
		return OVERWORLD_BIOMES;
	}

	public static Map<RegistryKey<Biome>, Biome.MixedNoisePoint> getNetherBiomes() {
		return NETHER_BIOMES;
	}
}
