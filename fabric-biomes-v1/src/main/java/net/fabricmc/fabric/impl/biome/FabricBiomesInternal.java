package net.fabricmc.fabric.impl.biome;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Lifecycle;

import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.api.biomes.v1.FabricBiomeBuilder;
import net.fabricmc.fabric.api.biomes.v1.event.BiomeLoadingCallback;
import net.fabricmc.fabric.mixin.biome.DynamicRegistryManagerAccessor;

public class FabricBiomesInternal {
	private static final Set<RegistryKey<Biome>> OVERWORLD_BIOMES = Sets.newHashSet();
	private static final Map<RegistryKey<Biome>, Biome.MixedNoisePoint> NETHER_BIOMES = Maps.newHashMap();

	/**
	 * Intended only for use within {@link net.minecraft.world.biome.layer.BiomeLayers}.
	 */
	public static Registry<Biome> lastBiomeRegistry;

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

	// Flag to temporarily disable triggering the BiomeLoadingCallback
	private static boolean disableBiomeLoadingCallback = false;

	public static void onBiomeRegistered(int rawId, RegistryKey<Biome> key, Biome oldBiome, MutableRegistry<Biome> registry, DynamicRegistryManager registryManager) {
		if (!disableBiomeLoadingCallback) {
			// Create builder, pass to event and rebuild biome
			FabricBiomeBuilder builder = FabricBiomeBuilder.of(oldBiome, registryManager);
			BiomeLoadingCallback.EVENT.invoker().onBiomeLoading(key, builder);
			Biome newBiome = builder.build();

			// Prevent re-triggering
			disableBiomeLoadingCallback = true;
			registry.set(rawId, key, newBiome, Lifecycle.stable());
			disableBiomeLoadingCallback = false;
		}
	}
}
