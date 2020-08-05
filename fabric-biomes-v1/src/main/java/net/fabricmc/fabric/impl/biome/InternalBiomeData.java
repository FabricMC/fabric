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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;

import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.fabricmc.fabric.mixin.biome.VanillaLayeredBiomeSourceAccessor;

/*
 * A general TODO for biomes API
 *
 * `addSpawnBiomes` is gone as spawn biomes are per biome (see Biomes#method_31082)
 *
 * Biomes#getMutated and Biome#has/getParent is gone
 *
 * Some of the constant int ids are gone around the place
 */
/**
 * Lists and maps for internal use only! Stores data that is used by the various mixins into the world generation
 */
public final class InternalBiomeData {
	private InternalBiomeData() { }

	private static final EnumMap<OverworldClimate, WeightedBiomePicker> OVERWORLD_MODDED_CONTINENTAL_BIOME_PICKERS = new EnumMap<>(OverworldClimate.class);
	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> OVERWORLD_HILLS_MAP = new HashMap<>();
	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> OVERWORLD_SHORE_MAP = new HashMap<>();
	private static final Map<RegistryKey<Biome>, WeightedBiomePicker> OVERWORLD_EDGE_MAP = new HashMap<>();
	private static final Map<RegistryKey<Biome>, VariantTransformer> OVERWORLD_VARIANT_TRANSFORMERS = new HashMap<>();
	private static final Map<RegistryKey<Biome>, RegistryKey<Biome>> OVERWORLD_RIVER_MAP = new HashMap<>();

	private static final Set<RegistryKey<Biome>> NETHER_BIOMES = new HashSet<>();
	private static final Map<RegistryKey<Biome>, Biome.MixedNoisePoint> NETHER_BIOME_NOISE_POINTS = new HashMap<>();

	private static Method injectBiomeMethod = null;

	public static void addOverworldContinentalBiome(OverworldClimate climate, RegistryKey<Biome> biome, double weight) {
		Preconditions.checkArgument(climate != null, "Climate is null");
		Preconditions.checkArgument(biome != null, "Biome is null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
		OVERWORLD_MODDED_CONTINENTAL_BIOME_PICKERS.computeIfAbsent(climate, k -> new WeightedBiomePicker()).addBiome(BuiltinRegistries.BIOME.get(biome), weight);
		injectOverworldBiome(biome);
	}

	public static void addOverworldHillsBiome(RegistryKey<Biome> primary, RegistryKey<Biome> hills, double weight) {
		Preconditions.checkArgument(primary != null, "Primary biome is null");
		Preconditions.checkArgument(hills != null, "Hills biome is null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
		OVERWORLD_HILLS_MAP.computeIfAbsent(primary, biome -> DefaultHillsData.injectDefaultHills(primary, new WeightedBiomePicker())).addBiome(BuiltinRegistries.BIOME.get(hills), weight);
		injectOverworldBiome(hills);
	}

	public static void addOverworldShoreBiome(RegistryKey<Biome> primary, RegistryKey<Biome> shore, double weight) {
		Preconditions.checkArgument(primary != null, "Primary biome is null");
		Preconditions.checkArgument(shore != null, "Shore biome is null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
		OVERWORLD_SHORE_MAP.computeIfAbsent(primary, biome -> new WeightedBiomePicker()).addBiome(BuiltinRegistries.BIOME.get(shore), weight);
		injectOverworldBiome(shore);
	}

	public static void addOverworldEdgeBiome(RegistryKey<Biome> primary, RegistryKey<Biome> edge, double weight) {
		Preconditions.checkArgument(primary != null, "Primary biome is null");
		Preconditions.checkArgument(edge != null, "Edge biome is null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (%s)", weight);
		OVERWORLD_EDGE_MAP.computeIfAbsent(primary, biome -> new WeightedBiomePicker()).addBiome(BuiltinRegistries.BIOME.get(edge), weight);
		injectOverworldBiome(edge);
	}

	public static void addOverworldBiomeReplacement(RegistryKey<Biome> replaced, RegistryKey<Biome> variant, double chance, OverworldClimate[] climates) {
		Preconditions.checkArgument(replaced != null, "Replaced biome is null");
		Preconditions.checkArgument(variant != null, "Variant biome is null");
		Preconditions.checkArgument(chance > 0 && chance <= 1, "Chance is not greater than 0 or less than or equal to 1");
		OVERWORLD_VARIANT_TRANSFORMERS.computeIfAbsent(replaced, biome -> new VariantTransformer()).addBiome(BuiltinRegistries.BIOME.get(variant), chance, climates);
		injectOverworldBiome(variant);
	}

	public static void setOverworldRiverBiome(RegistryKey<Biome> primary, RegistryKey<Biome> river) {
		Preconditions.checkArgument(primary != null, "Primary biome is null");
		OVERWORLD_RIVER_MAP.put(primary, river);

		if (river != null) {
			injectOverworldBiome(river);
		}
	}

	private static void injectOverworldBiome(RegistryKey<Biome> biome) {
		VanillaLayeredBiomeSourceAccessor.getBiomes().add(biome);
	}

	public static void addNetherBiome(RegistryKey<Biome> biome, Biome.MixedNoisePoint spawnNoisePoint) {
		Preconditions.checkArgument(biome != null, "Biome is null");
		Preconditions.checkArgument(spawnNoisePoint != null, "Biome.MixedNoisePoint is null");
		NETHER_BIOMES.add(biome);
		NETHER_BIOME_NOISE_POINTS.put(biome, spawnNoisePoint);
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

	public static Set<RegistryKey<Biome>> getNetherBiomes() {
		return Collections.unmodifiableSet(NETHER_BIOMES);
	}

	public static Map<RegistryKey<Biome>, Biome.MixedNoisePoint> getNetherBiomeNoisePoints() {
		return NETHER_BIOME_NOISE_POINTS;
	}

	private static class DefaultHillsData {
		private static final ImmutableMap<RegistryKey<Biome>, RegistryKey<Biome>> DEFAULT_HILLS;

		static WeightedBiomePicker injectDefaultHills(RegistryKey<Biome> base, WeightedBiomePicker picker) {
			RegistryKey<Biome> defaultHill = DEFAULT_HILLS.get(base);

			if (defaultHill != null) {
				picker.addBiome(BuiltinRegistries.BIOME.get(defaultHill), 1);
			} else if (BiomeLayers.areSimilar(BuiltinRegistries.BIOME.getRawId(BuiltinRegistries.BIOME.get(base)), BuiltinRegistries.BIOME.getRawId(BuiltinRegistries.BIOME.get(Biomes.WOODED_BADLANDS_PLATEAU)))) {
				picker.addBiome(BuiltinRegistries.BIOME.get(Biomes.BADLANDS), 1);
			} else if (base == Biomes.DEEP_OCEAN || base == Biomes.DEEP_LUKEWARM_OCEAN || base == Biomes.DEEP_COLD_OCEAN) {
				picker.addBiome(BuiltinRegistries.BIOME.get(Biomes.PLAINS), 1);
				picker.addBiome(BuiltinRegistries.BIOME.get(Biomes.FOREST), 1);
			} else if (base == Biomes.DEEP_FROZEN_OCEAN) {
				// Note: Vanilla Deep Frozen Oceans only have a 1/3 chance of having default hills.
				// This is a clever trick that ensures that when a mod adds hills with a weight of 1, the 1/3 chance is fulfilled.
				// 0.5 + 1.0 = 1.5, and 0.5 / 1.5 = 1/3.

				picker.addBiome(BuiltinRegistries.BIOME.get(Biomes.PLAINS), 0.25);
				picker.addBiome(BuiltinRegistries.BIOME.get(Biomes.FOREST), 0.25);
			} else if (base == Biomes.PLAINS) {
				picker.addBiome(BuiltinRegistries.BIOME.get(Biomes.WOODED_HILLS), 1);
				picker.addBiome(BuiltinRegistries.BIOME.get(Biomes.FOREST), 2);
			}

			return picker;
		}

		static {
			ImmutableMap.Builder<RegistryKey<Biome>, RegistryKey<Biome>> builder = ImmutableMap.builder();
			builder.put(Biomes.DESERT, Biomes.DESERT_HILLS);
			builder.put(Biomes.FOREST, Biomes.WOODED_HILLS);
			builder.put(Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS);
			builder.put(Biomes.DARK_FOREST, Biomes.PLAINS);
			builder.put(Biomes.TAIGA, Biomes.TAIGA_HILLS);
			builder.put(Biomes.GIANT_TREE_TAIGA, Biomes.GIANT_TREE_TAIGA_HILLS);
			builder.put(Biomes.SNOWY_TAIGA, Biomes.SNOWY_TAIGA_HILLS);
			builder.put(Biomes.SNOWY_TUNDRA, Biomes.SNOWY_MOUNTAINS);
			builder.put(Biomes.JUNGLE, Biomes.JUNGLE_HILLS);
			builder.put(Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS);
			builder.put(Biomes.OCEAN, Biomes.DEEP_OCEAN);
			builder.put(Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN);
			builder.put(Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN);
			builder.put(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
			builder.put(Biomes.MOUNTAINS, Biomes.WOODED_MOUNTAINS);
			builder.put(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU);
			DEFAULT_HILLS = builder.build();
		}
	}
}
