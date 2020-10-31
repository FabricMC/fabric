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

import java.util.function.Supplier;

import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public abstract class FabricChunkGenerator<T extends FabricGeneratorConfig> extends ChunkGenerator {
	private final Supplier<ChunkGeneratorSettings> generatorSettings;
	private final T generatorConfig;
	private final long seed;

	protected FabricChunkGenerator(BiomeSource biomeSource, Supplier<ChunkGeneratorSettings> generatorSettings, T generatorConfig, long seed) {
		super(biomeSource, biomeSource, generatorSettings.get().getStructuresConfig(), seed);
		this.generatorSettings = generatorSettings;
		this.generatorConfig = generatorConfig;
		this.seed = seed;
	}

	public Supplier<ChunkGeneratorSettings> getGeneratorSettings() {
		return this.generatorSettings;
	}

	public T getGeneratorConfig() {
		return this.generatorConfig;
	}

	public long getSeed() {
		return this.seed;
	}
}
