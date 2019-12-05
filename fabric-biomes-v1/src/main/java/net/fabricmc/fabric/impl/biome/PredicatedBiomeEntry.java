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

import java.util.Collection;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

import net.fabricmc.fabric.api.biomes.v1.BiomeNeighboursPredicate;

class PredicatedBiomeEntry {
	private final Biome biome;
	private final BiomeNeighboursPredicate predicate;
	private final double weight;

	PredicatedBiomeEntry(Biome biome, BiomeNeighboursPredicate predicate, double weight) {
		this.biome = biome;
		this.predicate = predicate;
		this.weight = weight;
	}

	Biome getBiome() {
		return biome;
	}

	boolean test(Collection<Biome> biomes, LayerRandomnessSource random) {
		return predicate.isValidToGenerate(biomes, random);
	}

	double getWeight() {
		return weight;
	}
}
