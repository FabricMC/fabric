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

import java.util.Random;

import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class SimpleLayerRandomnessSource implements LayerRandomnessSource {
	private final PerlinNoiseSampler sampler;

	public SimpleLayerRandomnessSource(long seed) {
		Random random = new Random(seed);
		this.sampler = new PerlinNoiseSampler(random);
	}

	@Override
	public int nextInt(int bound) {
		throw new UnsupportedOperationException("SimpleLayerRandomnessSource does not support calling nextInt(int).");
	}

	@Override
	public PerlinNoiseSampler getNoiseSampler() {
		return sampler;
	}
}
