package net.fabricmc.fabric.impl.biome;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

/**
 * Internal data for modding Vanilla's {@link TheEndBiomeSource}.
 */
@ApiStatus.Internal
public final class TheEndBiomeData {
	// Cached sets of the biomes that would generate from Vanilla's default biome source without consideration
	// for data packs (as those would be distinct biome sources).
	private static final Set<RegistryKey<Biome>> THE_END_BIOMES = new HashSet<>();

	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> END_BIOMES_MAP = new IdentityHashMap<>();
	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> END_MIDLANDS_MAP = new IdentityHashMap<>();
	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> END_BARRENS_MAP = new IdentityHashMap<>();

	static {
		END_BIOMES_MAP.computeIfAbsent(BiomeKeys.THE_END, key -> new WeightedBiomePicker()).addBiome(BiomeKeys.THE_END, 1.0);
		END_BIOMES_MAP.computeIfAbsent(BiomeKeys.END_HIGHLANDS, key -> new WeightedBiomePicker()).addBiome(BiomeKeys.END_HIGHLANDS, 1.0);
		END_BIOMES_MAP.computeIfAbsent(BiomeKeys.SMALL_END_ISLANDS, key -> new WeightedBiomePicker()).addBiome(BiomeKeys.SMALL_END_ISLANDS, 1.0);

		END_MIDLANDS_MAP.computeIfAbsent(BiomeKeys.END_HIGHLANDS, key -> new WeightedBiomePicker()).addBiome(BiomeKeys.END_MIDLANDS, 1.0);
		END_BARRENS_MAP.computeIfAbsent(BiomeKeys.END_HIGHLANDS, key -> new WeightedBiomePicker()).addBiome(BiomeKeys.END_BARRENS, 1.0);
	}

	private TheEndBiomeData() {
	}

	public static void addEndBiomeReplacement(RegistryKey<Biome> replaced, RegistryKey<Biome> variant, double weight) {
		Preconditions.checkNotNull(replaced, "replaced biome is null");
		Preconditions.checkNotNull(variant, "variant biome is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_BIOMES_MAP.computeIfAbsent(replaced, key -> new WeightedBiomePicker()).addBiome(variant, weight);
		clearBiomeSourceCache();
	}

	public static void addEndMidlandsReplacement(RegistryKey<Biome> highlands, RegistryKey<Biome> midlands, double weight) {
		Preconditions.checkNotNull(highlands, "highlands biome is null");
		Preconditions.checkNotNull(midlands, "midlands biome is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_MIDLANDS_MAP.computeIfAbsent(highlands, key -> new WeightedBiomePicker()).addBiome(midlands, weight);
		clearBiomeSourceCache();
	}

	public static void addEndBarrensReplacement(RegistryKey<Biome> highlands, RegistryKey<Biome> barrens, double weight) {
		Preconditions.checkNotNull(highlands, "highlands biome is null");
		Preconditions.checkNotNull(barrens, "midlands biome is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_BARRENS_MAP.computeIfAbsent(highlands, key -> new WeightedBiomePicker()).addBiome(barrens, weight);
		clearBiomeSourceCache();
	}

	public static Map<RegistryKey<Biome>, WeightedBiomePicker> getEndBiomesMap() {
		return END_BIOMES_MAP;
	}

	public static Map<RegistryKey<Biome>, WeightedBiomePicker> getEndMidlandsMap() {
		return END_MIDLANDS_MAP;
	}

	public static Map<RegistryKey<Biome>, WeightedBiomePicker> getEndBarrensMap() {
		return END_BARRENS_MAP;
	}

	public static boolean canGenerateInTheEnd(RegistryKey<Biome> biome) {
		if (THE_END_BIOMES.isEmpty()) {
			for (Biome endBiome : new TheEndBiomeSource(BuiltinRegistries.BIOME, 0).getBiomes()) {
				BuiltinRegistries.BIOME.getKey(endBiome).ifPresent(THE_END_BIOMES::add);
			}
		}

		return THE_END_BIOMES.contains(biome);
	}

	private static void clearBiomeSourceCache() {
		THE_END_BIOMES.clear(); // Clear cached biome source data
	}

	public static RegistryKey<Biome> pickEndBiome(int biomeX, int biomeY, int biomeZ, PerlinNoiseSampler sampler, RegistryKey<Biome> vanillaKey) {
		RegistryKey<Biome> replacementKey;

		// The x and z of the biome are divided by 64 to ensure custom biomes are large enough; going larger than this]
		// seems to make custom biomes too hard to find.
		if (vanillaKey == BiomeKeys.END_MIDLANDS || vanillaKey == BiomeKeys.END_BARRENS) {
			// Since the highlands picker is statically populated by InternalBiomeData, picker will never be null.
			WeightedBiomePicker highlandsPicker = TheEndBiomeData.getEndBiomesMap().get(BiomeKeys.END_HIGHLANDS);
			RegistryKey<Biome> highlandsKey = highlandsPicker.pickFromNoise(sampler, biomeX / 64.0, 0, biomeZ / 64.0);

			if (vanillaKey == BiomeKeys.END_MIDLANDS) {
				WeightedBiomePicker midlandsPicker = TheEndBiomeData.getEndMidlandsMap().get(highlandsKey);
				replacementKey = (midlandsPicker == null) ? vanillaKey : midlandsPicker.pickFromNoise(sampler, biomeX / 64.0, 0, biomeZ / 64.0);
			} else {
				WeightedBiomePicker barrensPicker = TheEndBiomeData.getEndBarrensMap().get(highlandsKey);
				replacementKey = (barrensPicker == null) ? vanillaKey : barrensPicker.pickFromNoise(sampler, biomeX / 64.0, 0, biomeZ / 64.0);
			}
		} else {
			// Since the main island and small islands pickers are statically populated by InternalBiomeData, picker will never be null.
			WeightedBiomePicker picker = TheEndBiomeData.getEndBiomesMap().get(vanillaKey);
			replacementKey = picker.pickFromNoise(sampler, biomeX / 64.0, 0, biomeZ / 64.0);
		}
		return replacementKey;
	}
}
