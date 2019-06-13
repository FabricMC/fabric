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

import net.fabricmc.fabric.impl.biomes.InternalBiomeData;
import net.minecraft.world.biome.Biome;

/**
 * API that exposes some internals of the minecraft default biome source for the overworld
 */
public final class OverworldBiomes {

	private OverworldBiomes() {
	}

	/**
	 * Adds the biome to the specified climate group, with the specified weight
	 *
	 * @param biome the biome to be added
	 * @param climate the climate group whereto the biome is added
	 * @param weight the weight of the entry. Vanilla weights for each biome in each climate are listed in the {@link OverworldClimate} javadoc.
	 */
	public static void addBaseBiome(Biome biome, OverworldClimate climate, double weight) {
		InternalBiomeData.addOverworldBaseBiome(climate, biome, weight);
	}

	/**
	 * Adds the biome as a hills variant of the parent biome, with the specified weight
	 *
	 * @param parent the biome to where the hills variant is added
	 * @param hills the biome to be set as a hills variant
	 * @param weight the weight of the entry. The weight in this method corrosponds to the amount of times it is added
	 * to the list that is randomly selected from when a hills biome is chosen.
	 */
	public static void addHillsBiome(Biome parent, Biome hills, int weight) {
		InternalBiomeData.addOverworldHillsBiome(parent, hills, weight);
	}

	/**
	 * Adds the biome as a shore/beach biome for the parent biome, with the specified weight
	 *
	 * @param parent the base biome to where the shore biome is added
	 * @param shore the biome to be added as a shore biome
	 * @param weight the weight of this entry. The weight in this method corrosponds to the amount of times it is added
	 * to the list that is randomly selected from when a shore biome is chosen.
	 */
	public static void addShoreBiome(Biome parent, Biome shore, int weight) {
		InternalBiomeData.addOverworldShoreBiome(parent, shore, weight);
	}

	/**
	 * Adds the biome as an an edge biome (excluding as a beach) of the parent biome, with the specified weight
	 *
	 * @param parent the base biome to where the edge biome is added
	 * @param edge the biome to be added as an edge biome
	 * @param weight the weight of this entry. The weight in this method corrosponds to the amount of times it is added
	 * to the list that is randomly selected from when an edge biome is chosen.
	 */
	public static void addEdgeBiome(Biome parent, Biome edge, int weight) {
		InternalBiomeData.addOverworldEdgeBiome(parent, edge, weight);
	}

	/**
	 * Adds a 'variant' biome which replaces another biome on occasion.
	 * For example, addBiomeVariant(Biomes.JUNGLE, Biomes.DESERT, 3) will replace 1/3 of jungles with deserts.
	 *
	 * @param replaced the base biome that is replaced by a variant
	 * @param variant the biome to be added as a variant
	 * @param rarity the reciprocal of the chance of replacement (there is a 1/rarity chance)
	 */
	public static void addBiomeVariant(Biome replaced, Biome variant, double rarity) {
		InternalBiomeData.addOverworldBiomeReplacement(replaced, variant, rarity);
	}

	/**
	 * Sets the river type that will generate in the biome
	 *
	 * @param parent the base biome in which the river biome is to be set
	 * @param river the river biome for this biome
	 */
	public static void setRiverBiome(Biome parent, Biome river) {
		InternalBiomeData.setOverworldRiverBiome(parent, river);
	}

}
