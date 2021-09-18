package net.fabricmc.fabric.impl.biome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

/**
 * Internal data for modding Vanilla's {@link MultiNoiseBiomeSource.Preset#NETHER}.
 */
@ApiStatus.Internal
public final class NetherBiomeData {
	// Cached sets of the biomes that would generate from Vanilla's default biome source without consideration
	// for data packs (as those would be distinct biome sources).
	private static final Set<RegistryKey<Biome>> NETHER_BIOMES = new HashSet<>();

	private static final Map<RegistryKey<Biome>, MultiNoiseUtil.NoiseHypercube> NETHER_BIOME_NOISE_POINTS = new HashMap<>();

	private NetherBiomeData() {
	}

	public static void addNetherBiome(RegistryKey<Biome> biome, MultiNoiseUtil.NoiseHypercube spawnNoisePoint) {
		Preconditions.checkArgument(biome != null, "Biome is null");
		Preconditions.checkArgument(spawnNoisePoint != null, "MultiNoiseUtil.NoiseValuePoint is null");
		NETHER_BIOME_NOISE_POINTS.put(biome, spawnNoisePoint);
		clearBiomeSourceCache();
	}

	public static Map<RegistryKey<Biome>, MultiNoiseUtil.NoiseHypercube> getNetherBiomeNoisePoints() {
		return NETHER_BIOME_NOISE_POINTS;
	}

	public static boolean canGenerateInNether(RegistryKey<Biome> biome) {
		if (NETHER_BIOMES.isEmpty()) {
			MultiNoiseBiomeSource source = MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(BuiltinRegistries.BIOME);

			for (Biome netherBiome : source.getBiomes()) {
				BuiltinRegistries.BIOME.getKey(netherBiome).ifPresent(NETHER_BIOMES::add);
			}
		}

		return NETHER_BIOMES.contains(biome);
	}

	private static void clearBiomeSourceCache() {
		NETHER_BIOMES.clear(); // Clear cached biome source data
	}
}
