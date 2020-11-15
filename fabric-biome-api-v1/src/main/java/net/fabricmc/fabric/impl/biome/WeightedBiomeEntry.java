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

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

/**
 * Represents a biome and its corresponding weight.
 */
final class WeightedBiomeEntry {
	private final RegistryKey<Biome> biome;
	private final double weight;
	private final double upperWeightBound;

	/**
	 * @param biome the biome
	 * @param weight how often a biome will be chosen
	 * @param upperWeightBound the upper weight bound within the context of the other entries, used for the binary search
	 */
	WeightedBiomeEntry(final RegistryKey<Biome> biome, final double weight, final double upperWeightBound) {
		this.biome = biome;
		this.weight = weight;
		this.upperWeightBound = upperWeightBound;
	}

	RegistryKey<Biome> getBiome() {
		return biome;
	}

	double getWeight() {
		return weight;
	}

	/**
	 * @return the upper weight boundary for the search
	 */
	double getUpperWeightBound() {
		return upperWeightBound;
	}
}
