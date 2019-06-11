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

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.impl.biomes.ClimateBiomeEntry;
import net.fabricmc.fabric.impl.biomes.InternalBiomeData;
import net.fabricmc.fabric.impl.biomes.VariantPicker;
import net.fabricmc.fabric.impl.biomes.WeightedBiomePicker;
import net.minecraft.world.biome.Biome;

public final class OverworldBiomes
{
	private OverworldBiomes() {}
	
	/**
	 * Adds the biome to the specified climate group, with the specified weight
	 * 
	 * @param biome the biome to be added
	 * @param climate the climate group whereto the biome is added
	 * @param weight the weight of the entry
	 */
	public static void addBiome(Biome biome, Climate climate, int weight)
	{
		
		Object2IntMap<Biome> weightMap = InternalBiomeData.BIOME_WEIGHTS.computeIfAbsent(climate, map -> new Object2IntOpenHashMap<>());
		weightMap.put(biome, weight + weightMap.computeIfAbsent(biome, a -> 0));
		
		for (int i = 0; i < weight; ++i)
			InternalBiomeData.INJECTED_BIOMES.add(new ClimateBiomeEntry(biome, climate));
		
		InternalBiomeData.CUSTOM_BIOMES.add(biome);
	}
	
	/**
	 * Adds the biome hillsBiome as a hills variant of baseBiome, with the specified weight
	 * 
	 * @param baseBiome the biome whereto the hills variant is added
	 * @param hillsBiome the biome to be set as a hills variant
	 * @param weight the weight of the entry
	 */
	public static void addHillsBiome(Biome baseBiome, Biome hillsBiome, int weight)
	{
		InternalBiomeData.HILLS_MAP.computeIfAbsent(baseBiome, biome -> new WeightedBiomePicker()).addBiome(hillsBiome, weight);
		
		InternalBiomeData.CUSTOM_BIOMES.add(hillsBiome);
	}

	/**
	 * Adds the biome shoreBiome as the beach biome for baseBiome, with the specified weight
	 * 
	 * @param baseBiome the base biome whereto the shore biome is added
	 * @param shoreBiome the biome to be added as a shore biome
	 * @param weight the weight of this entry
	 */
	public static void addShoreBiome(Biome baseBiome, Biome shoreBiome, int weight)
	{
		InternalBiomeData.SHORE_MAP.computeIfAbsent(baseBiome, biome -> new WeightedBiomePicker()).addBiome(shoreBiome, weight);

		InternalBiomeData.CUSTOM_BIOMES.add(shoreBiome);
	}

	/**
	 * Adds the biome edgeBiome as the edge biome (excluding as a beach) of the biome baseBiome, with the specified weight
	 * 
	 * @param baseBiome the base biome whereto the edge biome is added
	 * @param edgeBiome the biome to be added as an edge biome
	 * @param weight the weight of this entry
	 */
	public static void addEdgeBiome(Biome baseBiome, Biome edgeBiome, int weight)
	{
		InternalBiomeData.EDGE_MAP.computeIfAbsent(baseBiome, biome -> new WeightedBiomePicker()).addBiome(edgeBiome, weight);

		InternalBiomeData.CUSTOM_BIOMES.add(edgeBiome);
	}
	
	/**
	 * 
	 * @param baseBiome the base biome whereto the edge biome is added
	 * @param variantBiome the biome to be added as a variant
	 * @param rarity inverse of the chance of replacement (there is a one in rarity chance)
	 */
	public static void addBiomeVariant(Biome baseBiome, Biome variantBiome, int rarity)
	{
		InternalBiomeData.VARIANTS_MAP.computeIfAbsent(baseBiome, biome -> new VariantPicker()).addBiomeWithRarity(variantBiome, rarity);
		
		InternalBiomeData.CUSTOM_BIOMES.add(variantBiome);
	}
	
	/**
	 * Sets the river type that will generate in the biome
	 * 
	 * @param baseBiome the base biome wherein the river biome is to be set
	 * @param riverBiome the river biome for this biome
	 */
	public static void setRiverBiome(Biome baseBiome, Biome riverBiome)
	{
		InternalBiomeData.RIVER_MAP.put(baseBiome, riverBiome);

		if (riverBiome != null)
		{
			InternalBiomeData.CUSTOM_BIOMES.add(riverBiome);
		}
	}
	
	/**
	 * @return The weight of the biome in the specified climate
	 */
	public static int getWeight(Biome biome, VanillaClimate climate)
	{
		return InternalBiomeData.BIOME_WEIGHTS.containsKey(climate) ? 0 : InternalBiomeData.BIOME_WEIGHTS.get(climate).getOrDefault(biome, 0);
	}
}
