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

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a weighted biome picker using Minecraft's {@link LayerRandomnessSource} as a randomness source
 */
public final class WeightedBiomePicker {

	private int weightSum = 0;
	private final List<Biome> biomes = new ArrayList<>();

	/**
	 * @param biome the biome
	 * @param weight the amount of times a biome is added to the biome selection
	 */
	public void addBiome(Biome biome, int weight) {
		this.weightSum += weight;

		for (int i = 0; i < weight; ++i) {
			biomes.add(biome);
		}
	}

	/**
	 * @param rand the randomness source from the layer
	 * @return a random biome, based on the weight
	 */
	public Biome pickRandom(LayerRandomnessSource rand) {
		return biomes.get(rand.nextInt(weightSum));
	}

}
