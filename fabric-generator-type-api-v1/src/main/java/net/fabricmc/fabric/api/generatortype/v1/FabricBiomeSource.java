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

package net.fabricmc.fabric.api.generatortype.v1;

import java.util.List;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

public abstract class FabricBiomeSource extends BiomeSource {
	private final Registry<Biome> biomeRegistry;
	private final long seed;

	protected FabricBiomeSource(Registry<Biome> biomeRegistry, long seed, List<Biome> biomes) {
		super(biomes);
		this.biomeRegistry = biomeRegistry;
		this.seed = seed;
	}

	public Registry<Biome> getBiomeRegistry() {
		return this.biomeRegistry;
	}

	public long getSeed() {
		return this.seed;
	}
}
