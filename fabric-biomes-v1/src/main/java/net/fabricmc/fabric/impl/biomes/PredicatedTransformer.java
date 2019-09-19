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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

final class PredicatedTransformer {
	private final List<PredicatedBiomeEntry> predicates = new ArrayList<>();

	void addPredicatedBiome(Biome biome, BiPredicate<Biome, LayerRandomnessSource> predicate) {
		predicates.add(new PredicatedBiomeEntry(biome, predicate));
	}

	Biome transform(Biome biome, LayerRandomnessSource random, Biome[] borders) {
		for (PredicatedBiomeEntry predicate : predicates) {
			for (Biome border : borders) {
				if (predicate.test(border, random)) {
					return predicate.getBiome();
				}
			}
		}

		return biome;
	}
}
