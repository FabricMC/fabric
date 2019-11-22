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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

import net.fabricmc.fabric.api.biomes.v1.BiomeGenPredicate;

final class PredicatedTransformer {
	private final List<PredicatedBiomeEntry> predicates = new ArrayList<>();

	void addPredicatedBiome(Biome biome, BiomeGenPredicate predicate, double chance) {
		predicates.add(new PredicatedBiomeEntry(biome, predicate, chance));
	}

	Biome transform(Biome biome, LayerRandomnessSource random, Biome[] borders) {
		if (predicates.isEmpty()) {
			return biome;
		} else if (predicates.size() == 1) {
			PredicatedBiomeEntry predicate = predicates.get(0);

			for (Biome border : borders) {
				if (predicate.test(border, random)) {
					return predicate.getBiome();
				}
			}

			return biome;
		}

		List<PredicatedBiomeEntry> truePredicates = new ArrayList<>();
		double currentTotal = 0.0D;

		predicateLoop: for (PredicatedBiomeEntry predicate : predicates) {
			for (Biome border : borders) {
				if (predicate.test(border, random)) {
					truePredicates.add(predicate);

					currentTotal += predicate.getWeight();
					predicate.setUpperWeightBound(currentTotal);
					continue predicateLoop;
				}
			}
		}

		int size = truePredicates.size();

		if (size == 0) {
			return biome;
		} else if (size == 1) {
			return truePredicates.get(0).getBiome();
		} else {
			double target = (double) random.nextInt(Integer.MAX_VALUE) * currentTotal / Integer.MAX_VALUE;

			int low = 0;
			int high = truePredicates.size() - 1;

			while (low < high) {
				int mid = (high + low) >>> 1;

				if (target < truePredicates.get(mid).getUpperWeightBound()) {
					high = mid;
				} else {
					low = mid + 1;
				}
			}

			return truePredicates.get(low).getBiome();
		}
	}
}
