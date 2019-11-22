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

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

/**
 * A predicate of a biome array and randomness source.
 */
public interface BiomeEdgeGenPredicate {
	/**
	 * @param biomes the biomes neighboring generation
	 * @param rand the world gen randomness source
	 * @return whether the conditions for generation are met
	 */
	boolean meetsGenerationConditions(Biome[] biomes, LayerRandomnessSource rand);

	static BiomeEdgeGenPredicate bordersAny(Biome biome, Biome...otherBiomes) {
		final Biome[] borderBiomes = ArrayUtils.add(otherBiomes, biome);
		return (biomes, rand) -> {
			for (Biome neighbor : biomes) {
				if (ArrayUtils.contains(borderBiomes, neighbor)) return true;
			}

			return false;
		};
	}

	static BiomeEdgeGenPredicate chance(double chance) {
		return (biomes, rand) -> {
			double randVal = (double) rand.nextInt(Integer.MAX_VALUE) / Integer.MAX_VALUE;
			return randVal < chance;
		};
	}
}
