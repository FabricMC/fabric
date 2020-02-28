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

package net.fabricmc.fabric.api.level.generator.v1;

import java.util.function.Function;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.fabric.impl.level.generator.FabricLevelGeneratorType;

public final class LevelGeneratorFactory {
	/**
	 * Creates a new instance of a LevelGeneratorType.
	 *
	 * @param name                identifier of the {@link LevelGeneratorType}, must not contain ':'
	 * @param generatorType       instance of {@link ChunkGeneratorType}
	 * @param biomeSourceSupplier provides a {@link BiomeSource} using a lambda
	 */
	public static LevelGeneratorType create(Identifier name, ChunkGeneratorType<?, ? extends ChunkGenerator<?>> generatorType, Function<World, BiomeSource> biomeSourceSupplier) {
		return FabricLevelGeneratorType.create(name, name, 0, generatorType, biomeSourceSupplier);
	}

	/**
	 * Creates a new instance of a LevelGeneratorType.
	 *
	 * @param name                identifier of the {@link LevelGeneratorType}, must not contain ':'
	 * @param version             version of {@link LevelGeneratorType} used to store in the level properties and shown in crash reports
	 * @param generatorType       instance of {@link ChunkGeneratorType}
	 * @param biomeSourceSupplier provides a {@link BiomeSource} using a lambda
	 */
	public static LevelGeneratorType create(Identifier name, int version, ChunkGeneratorType<?, ? extends ChunkGenerator<?>> generatorType, Function<World, BiomeSource> biomeSourceSupplier) {
		return FabricLevelGeneratorType.create(name, name, version, generatorType, biomeSourceSupplier);
	}

	/**
	 * Creates a new instance of a LevelGeneratorType.
	 *
	 * @param name                identifier of the {@link LevelGeneratorType}, must not contain ':'
	 * @param storedName          identifier of the {@link LevelGeneratorType} used to store in the level properties
	 * @param version             version of {@link LevelGeneratorType} used to store in the level properties and shown in crash reports
	 * @param generatorType       instance of {@link ChunkGeneratorType}
	 * @param biomeSourceSupplier provides a {@link BiomeSource} using a lambda
	 */
	public static LevelGeneratorType create(Identifier name, Identifier storedName, int version, ChunkGeneratorType<?, ? extends ChunkGenerator<?>> generatorType, Function<World, BiomeSource> biomeSourceSupplier) {
		return FabricLevelGeneratorType.create(name, storedName, version, generatorType, biomeSourceSupplier);
	}
}
