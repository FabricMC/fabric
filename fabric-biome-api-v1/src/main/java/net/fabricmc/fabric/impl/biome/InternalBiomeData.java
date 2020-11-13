/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.biome;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;

import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.fabricmc.fabric.mixin.biome.VanillaLayeredBiomeSourceAccessor;

/**
 * Lists and maps for internal use only! Stores data that is used by the various mixins into the world generation
 */
public final class InternalBiomeData {
	private InternalBiomeData() {
	}

	private static final EnumMap<OverworldClimate, WeightedBiomePicker> OVERWORLD_MODDED_CONTINENTAL_BIOME_PICKERS = new EnumMap<>(OverworldClimate.class);
	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> OVERWORLD_HILLS_MAP = new IdentityHashMap<>();
	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> OVERWORLD_SHORE_MAP = new IdentityHashMap<>();
	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> OVERWORLD_EDGE_MAP = new IdentityHashMap<>();
	private static final Map<RegistryKey<Biome>, VariantTransformer> OVERWORLD_VARIANT_TRANSFORMERS = new IdentityHashMap<>();
	private static final Map<RegistryKey<Biome>, RegistryKey<Biome>> OVERWORLD_RIVER_MAP = new IdentityHashMap<>();

	private static final Set<RegistryKey<Biome>> NETHER_BIOMES = new HashSet<>();
	private static final Map<RegistryKey<Biome>, Biome.MixedNoisePoint> NETHER_BIOME_NOISE_POINTS = new HashMap<>();

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

	public static void addOverworldContinentalBiome(OverworldClimate climate, RegistryKey<Biome> biome, double weight) {
		Preconditions.checkArgument(climate != null, "Climate is null");
		Preconditions.checkArgument(biome != null, "Biome is null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
		InternalBiomeUtils.ensureIdMapping(biome);
		OVERWORLD_MODDED_CONTINENTAL_BIOME_PICKERS.computeIfAbsent(climate, k -> new WeightedBiomePicker()).addBiome(biome, weight);
		injectOverworldBiome(biome);
	}

	public static void addOverworldHillsBiome(RegistryKey<Biome> primary, RegistryKey<Biome> hills, double weight) {
		Preconditions.checkArgument(primary != null, "Primary biome is null");
		Preconditions.checkArgument(hills != null, "Hills biome is null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
		InternalBiomeUtils.ensureIdMapping(primary);
		InternalBiomeUtils.ensureIdMapping(hills);
		OVERWORLD_HILLS_MAP.computeIfAbsent(primary, biome -> DefaultHillsData.injectDefaultHills(primary, new WeightedBiomePicker())).addBiome(hills, weight);
		injectOverworldBiome(hills);
	}

	public static void addOverworldShoreBiome(RegistryKey<Biome> primary, RegistryKey<Biome> shore, double weight) {
		Preconditions.checkArgument(primary != null, "Primary biome is null");
		Preconditions.checkArgument(shore != null, "Shore biome is null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
		InternalBiomeUtils.ensureIdMapping(primary);
		InternalBiomeUtils.ensureIdMapping(shore);
		OVERWORLD_SHORE_MAP.computeIfAbsent(primary, biome -> new WeightedBiomePicker()).addBiome(shore, weight);
		injectOverworldBiome(shore);
	}

	public static void addOverworldEdgeBiome(RegistryKey<Biome> primary, RegistryKey<Biome> edge, double weight) {
		Preconditions.checkArgument(primary != null, "Primary biome is null");
		Preconditions.checkArgument(edge != null, "Edge biome is null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
		InternalBiomeUtils.ensureIdMapping(primary);
		InternalBiomeUtils.ensureIdMapping(edge);
		OVERWORLD_EDGE_MAP.computeIfAbsent(primary, biome -> new WeightedBiomePicker()).addBiome(edge, weight);
		injectOverworldBiome(edge);
	}

	public static void addOverworldBiomeReplacement(RegistryKey<Biome> replaced, RegistryKey<Biome> variant, double chance, OverworldClimate[] climates) {
		Preconditions.checkArgument(replaced != null, "Replaced biome is null");
		Preconditions.checkArgument(variant != null, "Variant biome is null");
		Preconditions.checkArgument(chance > 0 && chance <= 1, "Chance is not greater than 0 or less than or equal to 1");
		InternalBiomeUtils.ensureIdMapping(replaced);
		InternalBiomeUtils.ensureIdMapping(variant);
		OVERWORLD_VARIANT_TRANSFORMERS.computeIfAbsent(replaced, biome -> new VariantTransformer()).addBiome(variant, chance, climates);
		injectOverworldBiome(variant);
	}

	public static void setOverworldRiverBiome(RegistryKey<Biome> primary, RegistryKey<Biome> river) {
		Preconditions.checkArgument(primary != null, "Primary biome is null");
		InternalBiomeUtils.ensureIdMapping(primary);
		InternalBiomeUtils.ensureIdMapping(river);
		OVERWORLD_RIVER_MAP.put(primary, river);

		if (river != null) {
			injectOverworldBiome(river);
		}
	}

	/**
	 * Adds the biomes in world gen to the array for the vanilla layered biome source.
	 * This helps with {@link VanillaLayeredBiomeSource#hasStructureFeature(StructureFeature)} returning correctly for modded biomes as well as in {@link VanillaLayeredBiomeSource#getTopMaterials()}}
	 */
	private static void injectOverworldBiome(RegistryKey<Biome> biome) {
		List<RegistryKey<Biome>> biomes = VanillaLayeredBiomeSourceAccessor.getBIOMES();

		if (biomes instanceof ImmutableList) {
			biomes = new ArrayList<>(biomes);
			VanillaLayeredBiomeSourceAccessor.setBIOMES(biomes);
		}

		biomes.add(biome);
	}

	public static void addNetherBiome(RegistryKey<Biome> biome, Biome.MixedNoisePoint spawnNoisePoint) {
		Preconditions.checkArgument(biome != null, "Biome is null");
		Preconditions.checkArgument(spawnNoisePoint != null, "Biome.MixedNoisePoint is null");
		InternalBiomeUtils.ensureIdMapping(biome);
		NETHER_BIOME_NOISE_POINTS.put(biome, spawnNoisePoint);
		NETHER_BIOMES.clear(); // Reset cached overall biome list
	}

	public static void addEndBiomeReplacement(RegistryKey<Biome> replaced, RegistryKey<Biome> variant, double weight) {
		Preconditions.checkNotNull(replaced, "replaced biome is null");
		Preconditions.checkNotNull(variant, "variant biome is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_BIOMES_MAP.computeIfAbsent(replaced, key -> new WeightedBiomePicker()).addBiome(variant, weight);
	}

	public static void addEndMidlandsReplacement(RegistryKey<Biome> highlands, RegistryKey<Biome> midlands, double weight) {
		Preconditions.checkNotNull(highlands, "highlands biome is null");
		Preconditions.checkNotNull(midlands, "midlands biome is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_MIDLANDS_MAP.computeIfAbsent(highlands, key -> new WeightedBiomePicker()).addBiome(midlands, weight);
	}

	public static void addEndBarrensReplacement(RegistryKey<Biome> highlands, RegistryKey<Biome> barrens, double weight) {
		Preconditions.checkNotNull(highlands, "highlands biome is null");
		Preconditions.checkNotNull(barrens, "midlands biome is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_BARRENS_MAP.computeIfAbsent(highlands, key -> new WeightedBiomePicker()).addBiome(barrens, weight);
	}

	public static Map<RegistryKey<Biome>, WeightedBiomePicker> getOverworldHills() {
		return OVERWORLD_HILLS_MAP;
	}

	public static Map<RegistryKey<Biome>, WeightedBiomePicker> getOverworldShores() {
		return OVERWORLD_SHORE_MAP;
	}

	public static Map<RegistryKey<Biome>, WeightedBiomePicker> getOverworldEdges() {
		return OVERWORLD_EDGE_MAP;
	}

	public static Map<RegistryKey<Biome>, RegistryKey<Biome>> getOverworldRivers() {
		return OVERWORLD_RIVER_MAP;
	}

	public static EnumMap<OverworldClimate, WeightedBiomePicker> getOverworldModdedContinentalBiomePickers() {
		return OVERWORLD_MODDED_CONTINENTAL_BIOME_PICKERS;
	}

	public static Map<RegistryKey<Biome>, VariantTransformer> getOverworldVariantTransformers() {
		return OVERWORLD_VARIANT_TRANSFORMERS;
	}

	public static Map<RegistryKey<Biome>, Biome.MixedNoisePoint> getNetherBiomeNoisePoints() {
		return NETHER_BIOME_NOISE_POINTS;
	}

	public static boolean canGenerateInNether(RegistryKey<Biome> biome) {
		if (NETHER_BIOMES.isEmpty()) {
			MultiNoiseBiomeSource source = MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(BuiltinRegistries.BIOME, 0L);

			for (Biome netherBiome : source.getBiomes()) {
				BuiltinRegistries.BIOME.getKey(netherBiome).ifPresent(NETHER_BIOMES::add);
			}
		}

		return NETHER_BIOMES.contains(biome);
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

	private static class DefaultHillsData {
		private static final ImmutableMap<RegistryKey<Biome>, RegistryKey<Biome>> DEFAULT_HILLS;

		static WeightedBiomePicker injectDefaultHills(RegistryKey<Biome> base, WeightedBiomePicker picker) {
			RegistryKey<Biome> defaultHill = DEFAULT_HILLS.get(base);

			if (defaultHill != null) {
				picker.addBiome(defaultHill, 1);
			} else if (BiomeLayers.areSimilar(InternalBiomeUtils.getRawId(base), InternalBiomeUtils.getRawId(BiomeKeys.WOODED_BADLANDS_PLATEAU))) {
				picker.addBiome(BiomeKeys.BADLANDS, 1);
			} else if (base == BiomeKeys.DEEP_OCEAN || base == BiomeKeys.DEEP_LUKEWARM_OCEAN || base == BiomeKeys.DEEP_COLD_OCEAN) {
				picker.addBiome(BiomeKeys.PLAINS, 1);
				picker.addBiome(BiomeKeys.FOREST, 1);
			} else if (base == BiomeKeys.DEEP_FROZEN_OCEAN) {
				// Note: Vanilla Deep Frozen Oceans only have a 1/3 chance of having default hills.
				// This is a clever trick that ensures that when a mod adds hills with a weight of 1, the 1/3 chance is fulfilled.
				// 0.5 + 1.0 = 1.5, and 0.5 / 1.5 = 1/3.

				picker.addBiome(BiomeKeys.PLAINS, 0.25);
				picker.addBiome(BiomeKeys.FOREST, 0.25);
			} else if (base == BiomeKeys.PLAINS) {
				picker.addBiome(BiomeKeys.WOODED_HILLS, 1);
				picker.addBiome(BiomeKeys.FOREST, 2);
			}

			return picker;
		}

		static {
			// This map mirrors the hardcoded logic in AddHillsLayer#sample
			ImmutableMap.Builder<RegistryKey<Biome>, RegistryKey<Biome>> builder = ImmutableMap.builder();
			builder.put(BiomeKeys.DESERT, BiomeKeys.DESERT_HILLS);
			builder.put(BiomeKeys.FOREST, BiomeKeys.WOODED_HILLS);
			builder.put(BiomeKeys.BIRCH_FOREST, BiomeKeys.BIRCH_FOREST_HILLS);
			builder.put(BiomeKeys.DARK_FOREST, BiomeKeys.PLAINS);
			builder.put(BiomeKeys.TAIGA, BiomeKeys.TAIGA_HILLS);
			builder.put(BiomeKeys.GIANT_TREE_TAIGA, BiomeKeys.GIANT_TREE_TAIGA_HILLS);
			builder.put(BiomeKeys.SNOWY_TAIGA, BiomeKeys.SNOWY_TAIGA_HILLS);
			builder.put(BiomeKeys.SNOWY_TUNDRA, BiomeKeys.SNOWY_MOUNTAINS);
			builder.put(BiomeKeys.JUNGLE, BiomeKeys.JUNGLE_HILLS);
			builder.put(BiomeKeys.BAMBOO_JUNGLE, BiomeKeys.BAMBOO_JUNGLE_HILLS);
			builder.put(BiomeKeys.OCEAN, BiomeKeys.DEEP_OCEAN);
			builder.put(BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN);
			builder.put(BiomeKeys.COLD_OCEAN, BiomeKeys.DEEP_COLD_OCEAN);
			builder.put(BiomeKeys.FROZEN_OCEAN, BiomeKeys.DEEP_FROZEN_OCEAN);
			builder.put(BiomeKeys.MOUNTAINS, BiomeKeys.WOODED_MOUNTAINS);
			builder.put(BiomeKeys.SAVANNA, BiomeKeys.SAVANNA_PLATEAU);
			DEFAULT_HILLS = builder.build();
		}
	}
}
