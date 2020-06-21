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

import net.fabricmc.fabric.impl.biome.EndRegion;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;

/**
 * API that allows for adding biomes to the biome source for The End.
 */
public final class TheEndBiomes {
	private TheEndBiomes() { }

	/**
	 * Ands the biome with the specified weight to the main end island region; note that this includes the main island and some of the land encircling the empty space.
	 *
	 * @param biome the biome to be added
	 * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
	 * heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
	 * Vanilla biomes have a weight of 1.0
	 */
	public static void addMainIslandBiome(Biome biome, double weight) {
		InternalBiomeData.addEndBiome(biome, EndRegion.MAIN_ISLAND, weight);
	}

	/**
	 * Ands the biome to with the specified weight to the end highlands regions which correspond to the End Highlands biome.
	 *
	 * @param biome the biome to be added
	 * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
	 * heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
	 * Vanilla biomes have a weight of 1.0
	 */
	public static void addHighlandsBiome(Biome biome, double weight) {
		InternalBiomeData.addEndBiome(biome, EndRegion.MAIN_ISLAND, weight);
	}

	/**
	 * Ands the biome to with the specified weight to the end midlands regions which correspond to the End Midlands biome.
	 *
	 * @param biome the biome to be added
	 * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
	 * heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
	 * Vanilla biomes have a weight of 1.0
	 */
	public static void addMidlandsiome(Biome biome, double weight) {
		InternalBiomeData.addEndBiome(biome, EndRegion.MAIN_ISLAND, weight);
	}

	/**
	 * Ands the biome to with the specified weight to the end barrens regions which correspond to the End Barrens biome.
	 *
	 * @param biome the biome to be added
	 * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
	 * heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
	 * Vanilla biomes have a weight of 1.0
	 */
	public static void addBarrensBiome(Biome biome, double weight) {
		InternalBiomeData.addEndBiome(biome, EndRegion.MAIN_ISLAND, weight);
	}

	/**
	 * Ands the biome to with the specified weight to the small end island regions which correspond to the Small End Islands biome.
	 *
	 * @param biome the biome to be added
	 * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
	 * heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
	 * Vanilla biomes have a weight of 1.0
	 */
	public static void addSmallIslandsBiome(Biome biome, double weight) {
		InternalBiomeData.addEndBiome(biome, EndRegion.MAIN_ISLAND, weight);
	}

	/**
	 * Ands the biome to the specified end region with the specified weight.
	 *
	 * @param biome the biome to be added
	 * @param region The region of the end this biome is added to.
	 * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
	 * heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
	 * Vanilla biomes have a weight of 1.0
	 */
	public static void addBiome(Biome biome, EndRegion region, double weight) {
		InternalBiomeData.addEndBiome(biome, region, weight);
	}
}
