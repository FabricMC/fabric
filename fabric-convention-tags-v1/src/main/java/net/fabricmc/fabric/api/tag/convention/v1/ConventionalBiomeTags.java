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

package net.fabricmc.fabric.api.tag.convention.v1;

import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.tag.convention.TagRegistration;

/**
 * See {@link net.minecraft.registry.tag.BiomeTags} for vanilla tags.
 * Note that addition to some vanilla tags implies having certain functionality,
 * and as such certain biome tags exist to mirror vanilla tags, and should be preferred
 * over vanilla unless its behavior is desired.
 */
public final class ConventionalBiomeTags {
	private ConventionalBiomeTags() {
	}

	/**
	 * Biomes that spawn in the Overworld.
	 */
	public static final TagKey<Biome> IS_OVERWORLD = register("is_overworld");
	// The following are based on Biome categories, see Biome$Category for details
	/**
	 * Biomes that spawn in the End.
	 */
	public static final TagKey<Biome> IS_THE_END = register("is_the_end");
	/**
	 * Biomes that spawn in the Nether.
	 */
	public static final TagKey<Biome> IS_NETHER = register("is_nether");
	public static final TagKey<Biome> IS_TAIGA = register("is_taiga");
	public static final TagKey<Biome> IS_EXTREME_HILLS = register("is_extreme_hills");
	public static final TagKey<Biome> IS_WINDSWEPT = register("is_windswept");
	public static final TagKey<Biome> IS_JUNGLE = register("is_jungle");
	/**
	 * For temperate or warmer plains-like biomes.
	 * For snowy plains-like biomes, see {@link ConventionalBiomeTags#IS_SNOWY_PLAINS}.
	 */
	public static final TagKey<Biome> IS_PLAINS = register("is_plains");
	public static final TagKey<Biome> IS_SAVANNA = register("is_savanna");
	/**
	 * For land biomes where ice naturally spawns.
	 * For biomes where snow alone spawns, see {@link ConventionalBiomeTags#IS_SNOWY}.
	 */
	public static final TagKey<Biome> IS_ICY = register("is_icy");
	/**
	 * For water biomes where ice naturally spawns.
	 * For biomes where snow alone spawns, see {@link ConventionalBiomeTags#IS_SNOWY}.
	 */
	public static final TagKey<Biome> IS_AQUATIC_ICY = register("is_aquatic_icy");
	/**
	 * Biomes that exist on the shoreline of a body of water.
	 */
	public static final TagKey<Biome> IS_BEACH = register("is_beach");
	/**
	 * Biomes densely populated with deciduous trees.
	 */
	public static final TagKey<Biome> IS_FOREST = register("is_forest");
	public static final TagKey<Biome> IS_BIRCH_FOREST = register("is_birch_forest");
	public static final TagKey<Biome> IS_OCEAN = register("is_ocean");
	public static final TagKey<Biome> IS_DESERT = register("is_desert");
	public static final TagKey<Biome> IS_RIVER = register("is_river");
	public static final TagKey<Biome> IS_SWAMP = register("is_swamp");
	public static final TagKey<Biome> IS_MUSHROOM = register("is_mushroom");
	public static final TagKey<Biome> IS_UNDERGROUND = register("is_underground");
	public static final TagKey<Biome> IS_MOUNTAIN = register("is_mountain");

	public static final TagKey<Biome> IS_HOT_OVERWORLD = register("is_hot/overworld");
	public static final TagKey<Biome> IS_HOT = register("is_hot");
	public static final TagKey<Biome> IS_TEMPERATE_OVERWORLD = register("is_temperate/overworld");
	public static final TagKey<Biome> IS_TEMPERATE = register("is_temperate");
	public static final TagKey<Biome> IS_COLD_OVERWORLD = register("is_cold/overworld");
	public static final TagKey<Biome> IS_COLD = register("is_cold");
	public static final TagKey<Biome> IS_WET_OVERWORLD = register("is_wet/overworld");
	public static final TagKey<Biome> IS_WET = register("is_wet");
	public static final TagKey<Biome> IS_DRY_OVERWORLD = register("is_dry/overworld");
	public static final TagKey<Biome> IS_DRY = register("is_dry");
	public static final TagKey<Biome> IS_VEGETATION_SPARSE = register("is_sparse_vegetation");
	public static final TagKey<Biome> IS_VEGETATION_SPARSE_OVERWORLD = register("is_sparse_vegetation/overworld");
	public static final TagKey<Biome> IS_VEGETATION_DENSE = register("is_dense_vegetation");
	public static final TagKey<Biome> IS_VEGETATION_DENSE_OVERWORLD = register("is_dense_vegetation/overworld");
	public static final TagKey<Biome> IS_TREE_CONIFEROUS = register("is_tree/coniferous");
	public static final TagKey<Biome> IS_TREE_SAVANNA = register("is_tree/savanna");
	public static final TagKey<Biome> IS_TREE_JUNGLE = register("is_tree/jungle");
	public static final TagKey<Biome> IS_TREE_DECIDUOUS = register("is_tree/deciduous");
	public static final TagKey<Biome> IS_VOID = register("is_void");
	public static final TagKey<Biome> IS_MOUNTAIN_PEAK = register("is_mountain/peak");
	public static final TagKey<Biome> IS_MOUNTAIN_SLOPE = register("is_mountain/slope");
	/**
	 * Biomes consisting primarily of water.
	 */
	public static final TagKey<Biome> IS_AQUATIC = register("is_aquatic");
	/**
	 * Barren biomes that lack vegetation.
	 */
	public static final TagKey<Biome> IS_WASTELAND = register("is_wasteland");
	/**
	 * Biomes whose flora primarily consists of dead or decaying vegetation.
	 */
	public static final TagKey<Biome> IS_DEAD = register("is_dead");
	/**
	 * Biomes with a large amount of flowers.
	 */
	public static final TagKey<Biome> IS_FLORAL = register("is_floral");
	/**
	 * For biomes where snow, and not ice, naturally spawns as a predominant feature.
	 * For biomes where ice is a predominant feature, see {@link ConventionalBiomeTags#IS_ICY}.
	 */
	public static final TagKey<Biome> IS_SNOWY = register("is_snowy");

	public static final TagKey<Biome> IS_BADLANDS = register("is_badlands");
	public static final TagKey<Biome> IS_CAVE = register("is_cave");
	/**
	 * Biomes that spawn as or on islands in the End.
	 */
	public static final TagKey<Biome> IS_END_ISLAND = register("is_end_island");
	public static final TagKey<Biome> IS_NETHER_FOREST = register("is_nether_forest");
	/**
	 * For snowy plains-like biomes.
	 * For warmer plains-like biomes, see {@link ConventionalBiomeTags#IS_PLAINS}.
	 */
	public static final TagKey<Biome> IS_SNOWY_PLAINS = register("is_snowy_plains");
	public static final TagKey<Biome> IS_STONY_SHORES = register("is_stony_shores");
	public static final TagKey<Biome> IS_FLOWER_FOREST = register("is_flower_forest");
	public static final TagKey<Biome> IS_DEEP_OCEAN = register("is_deep_ocean");
	public static final TagKey<Biome> IS_SHALLOW_OCEAN = register("is_shallow_ocean");

	private static TagKey<Biome> register(String tagID) {
		return TagRegistration.BIOME_TAG_REGISTRATION.registerCommon(tagID);
	}
}
