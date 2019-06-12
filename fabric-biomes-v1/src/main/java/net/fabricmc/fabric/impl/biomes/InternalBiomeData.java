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

package net.fabricmc.fabric.impl.biomes;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lists and maps for internal use only! Stores data that is used by the various mixins into the world generation
 */
public final class InternalBiomeData {

	private InternalBiomeData() {
	}

	private static final Map<Biome, WeightedBiomePicker> OVERWORLD_HILLS_MAP = new HashMap<>();
	private static final Map<Biome, WeightedBiomePicker> OVERWORLD_SHORE_MAP = new HashMap<>();
	private static final Map<Biome, WeightedBiomePicker> OVERWORLD_EDGE_MAP = new HashMap<>();
	private static final Map<Biome, Biome> OVERWORLD_RIVER_MAP = new HashMap<>();
	private static final Map<Biome, VariantTransformer> OVERWORLD_VARIANT_TRANSFORMERS = new HashMap<>();
	
	private static final Map<OverworldClimate, Object2DoubleMap<Biome>> OVERWORLD_BIOME_WEIGHT_TOTALS = new HashMap<>();
	private static final Map<OverworldClimate, List<BiomeEntry>> OVERWORLD_BASE_BIOMES = new HashMap<>();
	protected static final EnumMap<OverworldClimate, Double> OVERWORLD_MODDED_WEIGHT_TOTALS = new EnumMap<>(OverworldClimate.class);

	private static final List<Biome> OVERWORLD_INJECTED_BIOMES = new ArrayList<>();

	private static final Set<Biome> SPAWN_BIOMES = new HashSet<>();
	
	public static void addOverworldBaseBiome(OverworldClimate climate, Biome biome, double weight) {
		Preconditions.checkArgument(climate != null && biome != null, "One or both arguments are null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_BASE_BIOMES.computeIfAbsent(climate, k -> new ArrayList<>()).add(new BiomeEntry(biome, weight, climate));
		OVERWORLD_INJECTED_BIOMES.add(biome);
		
		Object2DoubleMap<Biome> climateMap = OVERWORLD_BIOME_WEIGHT_TOTALS.get(climate);
		double currentWeight = climateMap.computeIfAbsent(biome, (b) -> Double.valueOf(0));
		climateMap.replace(biome, currentWeight + weight);
	}

	public static void addOverworldHillsBiome(Biome parent, Biome hills, int weight) {
		Preconditions.checkArgument(parent != null && hills != null, "One or both arguments are null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_HILLS_MAP.computeIfAbsent(parent, biome -> new WeightedBiomePicker()).addBiome(hills, weight);
		OVERWORLD_INJECTED_BIOMES.add(hills);
	}

	public static void addOverworldShoreBiome(Biome parent, Biome shore, int weight) {
		Preconditions.checkArgument(parent != null && shore != null, "One or both arguments are null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_SHORE_MAP.computeIfAbsent(parent, biome -> new WeightedBiomePicker()).addBiome(shore, weight);
		OVERWORLD_INJECTED_BIOMES.add(shore);
	}

	public static void addOverworldEdgeBiome(Biome parent, Biome edge, int weight) {
		Preconditions.checkArgument(parent != null && edge != null, "One or both arguments are null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_EDGE_MAP.computeIfAbsent(parent, biome -> new WeightedBiomePicker()).addBiome(edge, weight);
		OVERWORLD_INJECTED_BIOMES.add(edge);
	}

	public static void addOverworldBiomeReplacement(Biome replaced, Biome variant, int rarity) {
		Preconditions.checkArgument(replaced != null && variant != null, "One or both arguments are null");
		Preconditions.checkArgument(rarity > 0, "Rarity is zero or negative (Must be positive)");
		OVERWORLD_VARIANT_TRANSFORMERS.computeIfAbsent(replaced, biome -> new VariantTransformer()).addBiome(variant, rarity);
		OVERWORLD_INJECTED_BIOMES.add(variant);
	}

	public static void setOverworldRiverBiome(Biome parent, Biome river) {
		OVERWORLD_RIVER_MAP.put(parent, river);
		if (river != null) {
			OVERWORLD_INJECTED_BIOMES.add(river);
		}
	}

	/**
	 * Allows players to naturally spawn in this biome
	 *
	 * @param biome
	 */
	public static void addSpawnBiome(Biome biome) {
		SPAWN_BIOMES.add(biome);
	}

	public static Map<OverworldClimate, List<BiomeEntry>> getOverworldBaseBiomes() {
		return OVERWORLD_BASE_BIOMES;
	}

	public static List<Biome> getOverworldInjectedBiomes() {
		return OVERWORLD_INJECTED_BIOMES;
	}

	public static Set<Biome> getSpawnBiomes() {
		return SPAWN_BIOMES;
	}

	public static Map<Biome, WeightedBiomePicker> getOverworldHills() {
		return OVERWORLD_HILLS_MAP;
	}

	public static Map<Biome, WeightedBiomePicker> getOverworldShores() {
		return OVERWORLD_SHORE_MAP;
	}

	public static Map<Biome, WeightedBiomePicker> getOverworldEdges() {
		return OVERWORLD_EDGE_MAP;
	}

	public static Map<Biome, Biome> getOverworldRivers() {
		return OVERWORLD_RIVER_MAP;
	}

	public static EnumMap<OverworldClimate, Double> getOverworldModdedWeightTotals() {
		return OVERWORLD_MODDED_WEIGHT_TOTALS;
	}

	public static Map<Biome, VariantTransformer> getOverworldVariantTransformers() {
		return OVERWORLD_VARIANT_TRANSFORMERS;
	}
	
	@SuppressWarnings("deprecation")
	public static double getOverworldBiomeWeight(OverworldClimate climate, Biome biome) {
		Object2DoubleMap<Biome> climateMap = OVERWORLD_BIOME_WEIGHT_TOTALS.get(climate);
		return climateMap.containsKey(biome) ? climateMap.get(biome) : 0;
	}
	
	static {
		// Add Vanilla Weights
		Object2DoubleMap<Biome> cool_weight_totals = OVERWORLD_BIOME_WEIGHT_TOTALS.computeIfAbsent(OverworldClimate.COOL, map -> new Object2DoubleOpenHashMap<>());
		Object2DoubleMap<Biome> dry_weight_totals = OVERWORLD_BIOME_WEIGHT_TOTALS.computeIfAbsent(OverworldClimate.DRY, map -> new Object2DoubleOpenHashMap<>());
		Object2DoubleMap<Biome> snowy_weight_totals = OVERWORLD_BIOME_WEIGHT_TOTALS.computeIfAbsent(OverworldClimate.SNOWY, map -> new Object2DoubleOpenHashMap<>());
		Object2DoubleMap<Biome> temperate_weight_totals = OVERWORLD_BIOME_WEIGHT_TOTALS.computeIfAbsent(OverworldClimate.TEMPERATE, map -> new Object2DoubleOpenHashMap<>());
		
		cool_weight_totals.put(Biomes.FOREST, 1);
		cool_weight_totals.put(Biomes.MOUNTAINS, 1);
		cool_weight_totals.put(Biomes.PLAINS, 1);
		cool_weight_totals.put(Biomes.TAIGA, 1);
		
		dry_weight_totals.put(Biomes.DESERT, 3);
		dry_weight_totals.put(Biomes.SAVANNA, 2);
		dry_weight_totals.put(Biomes.PLAINS, 1);
		
		snowy_weight_totals.put(Biomes.SNOWY_TUNDRA, 3);
		snowy_weight_totals.put(Biomes.SNOWY_TAIGA, 1);
		
		temperate_weight_totals.put(Biomes.FOREST, 1);
		temperate_weight_totals.put(Biomes.MOUNTAINS, 1);
		temperate_weight_totals.put(Biomes.PLAINS, 1);
		temperate_weight_totals.put(Biomes.DARK_FOREST, 1);
		temperate_weight_totals.put(Biomes.BIRCH_FOREST, 1);
		temperate_weight_totals.put(Biomes.SWAMP, 1);
	}
}
