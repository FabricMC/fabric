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

package net.fabricmc.fabric.api.biomes.v1;

import static net.fabricmc.fabric.impl.biomes.BiomeLists.BIOME_WEIGHT_LOOKUP;
import static net.fabricmc.fabric.impl.biomes.BiomeLists.CUSTOM_BIOMES;
import static net.fabricmc.fabric.impl.biomes.BiomeLists.EDGE_MAP;
import static net.fabricmc.fabric.impl.biomes.BiomeLists.HILLS_MAP;
import static net.fabricmc.fabric.impl.biomes.BiomeLists.INJECTED_BIOME_LIST;
import static net.fabricmc.fabric.impl.biomes.BiomeLists.RIVER_MAP;
import static net.fabricmc.fabric.impl.biomes.BiomeLists.SHORE_MAP;

import java.util.HashMap;
import java.util.Map;

import net.fabricmc.fabric.impl.biomes.BiomeAssociate;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public final class OverworldBiomeUtils
{
	private OverworldBiomeUtils() {}
	
	/**
	 * Adds the biome to the specified climate group, with the specified weight
	 * 
	 * @param biome
	 * @param climate
	 * @param weight
	 */
	public static void addBiomeToClimate(Biome biome, BiomeClimate climate, int weight)
	{
		Map<Biome, Integer> weightMap = BIOME_WEIGHT_LOOKUP.computeIfAbsent(climate, map -> new HashMap<>());
		weightMap.put(biome, weight + weightMap.computeIfAbsent(biome, a -> Integer.valueOf(0)));
		
		for (int i = 0; i < weight; ++i)
			INJECTED_BIOME_LIST.add(new Pair<>(Registry.BIOME.getRawId(biome), climate));
		
		CUSTOM_BIOMES.add(biome);
	}
	
	/**
	 * Adds the biome hillsBiome as a hills variant of baseBiome, with the specified weight
	 * 
	 * @param baseBiome
	 * @param hillsBiome
	 * @param weight
	 */
	public static void addHillsBiome(Biome baseBiome, Biome hillsBiome, int weight)
	{
		HILLS_MAP.computeIfAbsent(baseBiome, associate -> new BiomeAssociate()).addBiomeWithWeight(hillsBiome, weight);

		CUSTOM_BIOMES.add(hillsBiome);
	}

	/**
	 * Adds the biome shoreBiome as the beach biome for baseBiome, with the specified weight
	 * 
	 * @param baseBiome
	 * @param shoreBiome
	 * @param weight
	 */
	public static void addShoreBiome(Biome baseBiome, Biome shoreBiome, int weight)
	{
		SHORE_MAP.computeIfAbsent(baseBiome, associate -> new BiomeAssociate()).addBiomeWithWeight(shoreBiome, weight);

		CUSTOM_BIOMES.add(shoreBiome);
	}

	/**
	 * Adds the biome edgeBiome as the edge biome (excluding as a beach) of the biome baseBiome, with the specified weight
	 * 
	 * @param baseBiome
	 * @param edgeBiome
	 * @param weight
	 */
	public static void addEdgeBiome(Biome baseBiome, Biome edgeBiome, int weight)
	{
		EDGE_MAP.computeIfAbsent(baseBiome, associate -> new BiomeAssociate()).addBiomeWithWeight(edgeBiome, weight);

		CUSTOM_BIOMES.add(edgeBiome);
	}

	/**
	 * Sets the river type that will generate in the biome
	 * 
	 * @param baseBiome
	 * @param riverType
	 */
	public static void setRiverBiome(Biome baseBiome, RiverAssociate riverType)
	{
		RIVER_MAP.put(baseBiome, riverType);

		if (!(riverType == RiverAssociate.NONE)) CUSTOM_BIOMES.add(riverType.getBiome());
	}
	
	/**
	 * @param biome
	 * @param climate
	 * @return The weight of the biome in the specified climate
	 */
	public static int getWeightOfInClimate(Biome biome, BiomeClimate climate)
	{
		return BIOME_WEIGHT_LOOKUP.containsKey(climate) ? 0 : BIOME_WEIGHT_LOOKUP.get(climate).getOrDefault(biome, 0);
	}
}
