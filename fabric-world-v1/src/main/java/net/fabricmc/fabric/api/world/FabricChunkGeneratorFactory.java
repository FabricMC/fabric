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

package net.fabricmc.fabric.api.world;

import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

/**
 * Responsible for creating the FabricChunkGeneratorType's ChunkGenerator instance.
 * Called when a new instance of a ChunkGenerator is requested in the ChunkGeneratorType.
 * @param <C> ChunkGeneratorConfig
 * @param <T> ChunkGenerator
 */
@FunctionalInterface
public interface FabricChunkGeneratorFactory<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> {
	T create(World world, BiomeSource source, C config);
}
