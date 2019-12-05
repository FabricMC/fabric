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

import java.util.Collection;

import com.google.common.collect.Lists;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

/**
 * A predicate of a biome collection and randomness source. Tests whether the conditions for generating the biome are met.
 */
public interface BiomeNeighborsPredicate {
	/**
	 * @param biomes a collection of biomes that neighbor the biome to be replaced. May contain duplicates, depending on the biome api method with which this is used.
	 * @param rand the world gen randomness source
	 * @return whether the conditions for generation are met
	 */
	boolean isValidToGenerate(Collection<Biome> biomes, LayerRandomnessSource rand);

	/**
	 * @return a {@link BiomeNeighborsPredicate} which returns true if any of the given biomes border the parent biome.
	 */
	static BiomeNeighborsPredicate anyOf(Biome biome, Biome...otherBiomes) {
		final Collection<Biome> borderBiomes = Lists.asList(biome, otherBiomes);
		return (biomes, rand) -> {
			for (Biome neighbor : borderBiomes) {
				if (biomes.contains(neighbor)) return true;
			}

			return false;
		};
	}

	/**
	 * @return a {@link BiomeNeighborsPredicate} which returns true if any of the biomes bordering the parent biome are of any of the given categories.
	 */
	static BiomeNeighborsPredicate anyOf(Biome.Category category, Biome.Category...otherCategories) {
		final Collection<Biome.Category> categories = Lists.asList(category, otherCategories);
		return (biomes, rand) -> {
			for (Biome neighbor : biomes) {
				if (categories.contains(neighbor.getCategory())) return true;
			}

			return false;
		};
	}

	/**
	 * @param chance the chance for this predicate to return true. 0.0 is no chance, and 1.0 means it will always return true
	 * @return a {@link BiomeNeighborsPredicate} which returns true based on a random chance expressed as a decimal.
	 */
	static BiomeNeighborsPredicate chance(double chance) {
		return (biomes, rand) -> {
			double randVal = (double) rand.nextInt(Integer.MAX_VALUE) / Integer.MAX_VALUE;
			return randVal < chance;
		};
	}

	/**
	 * @return a {@link BiomeNeighborsPredicate} which always returns true.
	 */
	static BiomeNeighborsPredicate alwaysTrue() {
		return (biomes, rand) -> true;
	}
}
