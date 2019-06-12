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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

/**
 * Provides a weighted biome picker using Minecraft's {@link LayerRandomnessSource} as a randomness source
 */
public final class WeightedBiomePicker {

	private int weightSum = 0;
	private IntList biomes = new IntArrayList();

	/**
	 * @param biome
	 * @param weight
	 */
	public void addBiome(Biome biome, int weight) {
		this.weightSum += weight;
		int b = Registry.BIOME.getRawId(biome);

		for (int i = 0; i < weight; ++i)
			biomes.add(b);
	}

	public int pickRandom(LayerRandomnessSource rand) {
		return biomes.getInt(rand.nextInt(weightSum));
	}

}
