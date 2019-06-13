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
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.minecraft.world.biome.Biome;

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
	private static final EnumMap<OverworldClimate, List<BaseBiomeEntry>> OVERWORLD_MODDED_BASE_BIOMES = new EnumMap<>(OverworldClimate.class);
	protected static final EnumMap<OverworldClimate, Double> OVERWORLD_MODDED_BASE_BIOME_WEIGHT_TOTALS = new EnumMap<>(OverworldClimate.class);
	private static final List<Biome> OVERWORLD_INJECTED_BIOMES = new ArrayList<>();
	private static final Set<Biome> SPAWN_BIOMES = new HashSet<>();

	public static void addOverworldBaseBiome(OverworldClimate climate, Biome biome, double weight) {
		Preconditions.checkArgument(climate != null && biome != null, "One or both arguments are null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_MODDED_BASE_BIOMES.computeIfAbsent(climate, k -> new ArrayList<>()).add(new BaseBiomeEntry(biome, weight, climate));
		OVERWORLD_INJECTED_BIOMES.add(biome);
	}

	public static void addOverworldHillsBiome(Biome parent, Biome hills, int weight) {
		Preconditions.checkArgument(parent != null && hills != null, "One or both arguments are null");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_HILLS_MAP.computeIfAbsent(parent, biome -> new WeightedBiomePicker()).addBiome(hills, weight);
		OVERWORLD_INJECTED_BIOMES.add(hills);
	}

	public static void addOverworldShoreBiome(Biome parent, Biome shore, int weight) {
		Preconditions.checkArgument(parent != null && shore != null, "One or both arguments are null");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_SHORE_MAP.computeIfAbsent(parent, biome -> new WeightedBiomePicker()).addBiome(shore, weight);
		OVERWORLD_INJECTED_BIOMES.add(shore);
	}

	public static void addOverworldEdgeBiome(Biome parent, Biome edge, int weight) {
		Preconditions.checkArgument(parent != null && edge != null, "One or both arguments are null");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_EDGE_MAP.computeIfAbsent(parent, biome -> new WeightedBiomePicker()).addBiome(edge, weight);
		OVERWORLD_INJECTED_BIOMES.add(edge);
	}

	public static void addOverworldBiomeReplacement(Biome replaced, Biome variant, double rarity) {
		Preconditions.checkArgument(replaced != null && variant != null, "One or both arguments are null");
		Preconditions.checkArgument(rarity >= 1, "Rarity is less than 1 (Must be positive and greater or equal to 1)");
		OVERWORLD_VARIANT_TRANSFORMERS.computeIfPresent(replaced, (biome, transformers) -> {
			transformers.addBiome(variant, rarity);
			return transformers;
		});
		OVERWORLD_VARIANT_TRANSFORMERS.computeIfAbsent(replaced, biome -> new VariantTransformer()).addBiome(variant, rarity);
		OVERWORLD_INJECTED_BIOMES.add(variant);
	}

	public static void setOverworldRiverBiome(Biome parent, Biome river) {
		OVERWORLD_RIVER_MAP.put(parent, river);
		if (river != null) {
			OVERWORLD_INJECTED_BIOMES.add(river);
		}
	}

	public static void addSpawnBiome(Biome biome) {
		SPAWN_BIOMES.add(biome);
	}

	public static Map<OverworldClimate, List<BaseBiomeEntry>> getOverworldModdedBaseBiomes() {
		return OVERWORLD_MODDED_BASE_BIOMES;
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

	public static EnumMap<OverworldClimate, Double> getOverworldModdedBaseBiomeWeightTotals() {
		return OVERWORLD_MODDED_BASE_BIOME_WEIGHT_TOTALS;
	}

	public static Map<Biome, VariantTransformer> getOverworldVariantTransformers() {
		return OVERWORLD_VARIANT_TRANSFORMERS;
	}

}
