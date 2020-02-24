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

package net.fabricmc.fabric.api.world.v1.gen;

import java.util.function.Supplier;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

/**
 * Fabric version of ChunkGeneratorType.
 * Needed as the standard ChunkGeneratorFactory used by ChunkGeneratorType is private.
 *
 * @param <C> ChunkGenerator config
 * @param <T> ChunkGenerator
 */
public final class FabricChunkGeneratorType<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> extends ChunkGeneratorType<C, T> {
	private FabricChunkGeneratorFactory<C, T> factory;

	private FabricChunkGeneratorType(FabricChunkGeneratorFactory<C, T> factory, boolean buffetScreenOption, Supplier<C> settingsSupplier) {
		super(null, buffetScreenOption, settingsSupplier);
		this.factory = factory;
	}

	/**
	 * Called to register and create new instance of the ChunkGeneratorType.
	 *
	 * @param id                 registry ID of the ChunkGeneratorType
	 * @param factory            factory instance to provide a ChunkGenerator
	 * @param settingsSupplier   config supplier
	 * @param buffetScreenOption whether or not the ChunkGeneratorType should appear in the buffet screen options page
	 */
	public static <C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> FabricChunkGeneratorType<C, T> register(Identifier id, FabricChunkGeneratorFactory<C, T> factory, Supplier<C> settingsSupplier, boolean buffetScreenOption) {
		return Registry.register(Registry.CHUNK_GENERATOR_TYPE, id, new FabricChunkGeneratorType<>(factory, buffetScreenOption, settingsSupplier));
	}

	/**
	 * Creates and returns an instance of the ChunkGeneratorType's ChunkGenerator.
	 *
	 * @param world       DimensionType's world instance
	 * @param biomeSource BiomeSource to use while generating the world
	 * @param config      ChunkGenerator config instance
	 */
	@Override
	public T create(World world, BiomeSource biomeSource, C config) {
		return factory.create(world, biomeSource, config);
	}

	/**
	 * Responsible for creating the FabricChunkGeneratorType's ChunkGenerator instance.
	 * Called when a new instance of a ChunkGenerator is requested in {@link FabricChunkGeneratorType#create(World, BiomeSource, ChunkGeneratorConfig)}.
	 *
	 * @param <C> ChunkGeneratorConfig
	 * @param <T> ChunkGenerator
	 */
	@FunctionalInterface
	public interface FabricChunkGeneratorFactory<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> {
		T create(World world, BiomeSource source, C config);
	}
}
