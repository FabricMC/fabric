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
import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;

import java.util.*;

/**
 * Lists and maps for internal use only! Stores data that is used by the various mixins into the world generation
 */
public final class InternalBiomeData {

	private InternalBiomeData() {
	}

	private static final EnumMap<OverworldClimate, WeightedBiomePicker> OVERWORLD_BASE_MODDED_BIOME_PICKERS = new EnumMap<>(OverworldClimate.class);
	private static final Map<Biome, WeightedBiomePicker> OVERWORLD_HILLS_MAP = new HashMap<>();
	private static final Map<Biome, WeightedBiomePicker> OVERWORLD_SHORE_MAP = new HashMap<>();
	private static final Map<Biome, WeightedBiomePicker> OVERWORLD_EDGE_MAP = new HashMap<>();
	private static final Map<Biome, VariantTransformer> OVERWORLD_VARIANT_TRANSFORMERS = new HashMap<>();

	private static final Map<Biome, Biome> OVERWORLD_RIVER_MAP = new HashMap<>();
	private static final List<Biome> OVERWORLD_INJECTED_BIOMES = new ArrayList<>();
	private static final Set<Biome> SPAWN_BIOMES = new HashSet<>();

	public static void addOverworldBaseBiome(OverworldClimate climate, Biome biome, double weight) {
		Preconditions.checkArgument(climate != null && biome != null, "One or both arguments are null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_BASE_MODDED_BIOME_PICKERS.computeIfAbsent(climate, k -> new WeightedBiomePicker()).addBiome(biome, weight);
		OVERWORLD_INJECTED_BIOMES.add(biome);
	}

	public static void addOverworldHillsBiome(Biome parent, Biome hills, double weight) {
		Preconditions.checkArgument(parent != null && hills != null, "One or both arguments are null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_HILLS_MAP.computeIfAbsent(parent, biome -> DefaultHillsData.injectDefaultHills(parent, new WeightedBiomePicker())).addBiome(hills, weight);
		OVERWORLD_INJECTED_BIOMES.add(hills);
	}

	public static void addOverworldShoreBiome(Biome parent, Biome shore, double weight) {
		Preconditions.checkArgument(parent != null && shore != null, "One or both arguments are null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_SHORE_MAP.computeIfAbsent(parent, biome -> new WeightedBiomePicker()).addBiome(shore, weight);
		OVERWORLD_INJECTED_BIOMES.add(shore);
	}

	public static void addOverworldEdgeBiome(Biome parent, Biome edge, double weight) {
		Preconditions.checkArgument(parent != null && edge != null, "One or both arguments are null");
		Preconditions.checkArgument(!Double.isNaN(weight), "Weight is NaN");
		Preconditions.checkArgument(weight > 0, "Weight is zero or negative (must be positive)");
		OVERWORLD_EDGE_MAP.computeIfAbsent(parent, biome -> new WeightedBiomePicker()).addBiome(edge, weight);
		OVERWORLD_INJECTED_BIOMES.add(edge);
	}

	public static void addOverworldBiomeReplacement(Biome replaced, Biome variant, double chance, OverworldClimate[] climates) {
		Preconditions.checkArgument(replaced != null && variant != null, "One or both arguments are null");
		Preconditions.checkArgument(chance > 0 && chance <= 1, "Chance is not greater than 0 or less than or equal to 1");
		OVERWORLD_VARIANT_TRANSFORMERS.computeIfAbsent(replaced, biome -> new VariantTransformer()).addBiome(variant, chance, climates);
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

	public static EnumMap<OverworldClimate, WeightedBiomePicker> getOverworldBaseModdedBiomePickers() {
		return OVERWORLD_BASE_MODDED_BIOME_PICKERS;
	}

	public static Map<Biome, VariantTransformer> getOverworldVariantTransformers() {
		return OVERWORLD_VARIANT_TRANSFORMERS;
	}

	private static class DefaultHillsData {
		private static final ImmutableMap<Biome, Biome> DEFAULT_HILLS;

		static WeightedBiomePicker injectDefaultHills(Biome base, WeightedBiomePicker picker) {
			Biome defaultHill = DEFAULT_HILLS.get(base);

			if (defaultHill != null) {
				picker.addBiome(defaultHill, 1);
			} else if (BiomeLayers.areSimilar(Registry.BIOME.getRawId(base), Registry.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU))) {
				picker.addBiome(Biomes.BADLANDS, 1);
			} else if (base == Biomes.DEEP_OCEAN || base == Biomes.DEEP_LUKEWARM_OCEAN || base == Biomes.DEEP_COLD_OCEAN) {
				picker.addBiome(Biomes.PLAINS, 1);
				picker.addBiome(Biomes.FOREST, 1);
			} else if (base == Biomes.DEEP_FROZEN_OCEAN) {
				// Note: Vanilla Deep Frozen Oceans only have a 1/3 chance of having default hills.
				// This is a clever trick that ensures that when a mod adds hills with a weight of 1, the 1/3 chance is fulfilled.
				// 0.5 + 1.0 = 1.5, and 0.5 / 1.5 = 1/3.

				picker.addBiome(Biomes.PLAINS, 0.25);
				picker.addBiome(Biomes.FOREST, 0.25);
			} else if (base == Biomes.PLAINS) {
				picker.addBiome(Biomes.WOODED_HILLS, 1);
				picker.addBiome(Biomes.FOREST, 2);
			}

			return picker;
		}

		static {
			ImmutableMap.Builder<Biome, Biome> builder = ImmutableMap.builder();
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
