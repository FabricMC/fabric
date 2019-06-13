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

import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

/**
 * Represents a biome and its corresponding weight
 */
public class BaseBiomeEntry {

	private final Biome biome;
	private final double weight;
	private final double upperWeightBound;

	/**
	 * @param biome the biome
	 * @param weight how often a biome will be chosen. Most vanilla biomes have a
	 * weight of 1, but a full list of values can be seen in {@link OverworldClimate}.
	 * @param climate the climate of the biome entry, just used to store weights
	 */
	public BaseBiomeEntry(final Biome biome, final double weight, OverworldClimate climate) {
		this.biome = biome;
		this.weight = weight;
		InternalBiomeData.OVERWORLD_MODDED_BASE_BIOME_WEIGHT_TOTALS.compute(climate, (c, w) -> w == null ? weight : weight + w);
		upperWeightBound = InternalBiomeData.OVERWORLD_MODDED_BASE_BIOME_WEIGHT_TOTALS.get(climate);
	}

	/**
	 * @return the biome
	 */
	public Biome getBiome() {
		return biome;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @return the upper weight boundary for the search
	 */
	public double getUpperWeightBound() {
		return upperWeightBound;
	}

	/**
	 * @return The raw id for the biome
	 */
	public int getRawId() {
		return Registry.BIOME.getRawId(biome);
	}

}
