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

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

import net.fabricmc.fabric.api.biomes.v1.BiomeGenPredicate;

class PredicatedBiomeEntry {
	private final Biome biome;
	private final BiomeGenPredicate predicate;
	private final double weight;
	private double upperWeightBound = 0D;

	PredicatedBiomeEntry(Biome biome, BiomeGenPredicate predicate, double weight) {
		this.biome = biome;
		this.predicate = predicate;
		this.weight = weight;
	}

	void setUpperWeightBound(double upperWeightBound) {
		this.upperWeightBound = upperWeightBound;
	}

	Biome getBiome() {
		return biome;
	}

	boolean test(Biome biome, LayerRandomnessSource random) {
		return predicate.test(biome, random);
	}

	double getWeight() {
		return weight;
	}

	double getUpperWeightBound() {
		return upperWeightBound;
	}
}
