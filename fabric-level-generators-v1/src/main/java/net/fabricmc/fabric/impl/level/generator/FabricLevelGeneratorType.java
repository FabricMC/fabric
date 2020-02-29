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

package net.fabricmc.fabric.impl.level.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.fabric.mixin.level.generator.LevelGeneratorTypeAccessor;

public final class FabricLevelGeneratorType {
	public static final HashMap<LevelGeneratorType, ChunkGeneratorSupplier> suppliers = new HashMap<>();

	public static LevelGeneratorType create(Identifier name, Identifier storedName, int version, ChunkGeneratorType<?, ? extends ChunkGenerator<?>> generatorType, Function<World, BiomeSource> biomeSource) {
		if (changeIdentifierSeparator(name).contains(":") || name.getNamespace().contains(".")) {
			throw new IllegalArgumentException("Character ':' is not allowed in level generator type identifier and '.' is not allowed in namespace");
		}

		LevelGeneratorType levelType = LevelGeneratorTypeAccessor.fabric_create(getFreeId(), changeIdentifierSeparator(name), changeIdentifierSeparator(storedName), version);
		suppliers.put(levelType, new ChunkGeneratorSupplier(generatorType, biomeSource));
		return levelType;
	}

	public static String changeIdentifierSeparator(Identifier identifier) {
		return identifier.getNamespace() + "." + identifier.getPath();
	}

	private static int getFreeId() {
		for (int id = 0; id < LevelGeneratorType.TYPES.length; id++) {
			if (LevelGeneratorType.TYPES[id] == null) {
				return id;
			}
		}

		int length = LevelGeneratorType.TYPES.length;
		LevelGeneratorTypeAccessor.setTypes(java.util.Arrays.copyOf(LevelGeneratorType.TYPES, length + 16));
		return length;
	}

	public static LevelGeneratorType getTypeFromPath(String name) {
		ArrayList<LevelGeneratorType> matches = new ArrayList<>();

		for (int id = 0; id < LevelGeneratorType.TYPES.length; id++) {
			LevelGeneratorType levelGeneratorType = LevelGeneratorType.TYPES[id];
			String[] levelGeneratorTypes;

			if (levelGeneratorType != null) {
				levelGeneratorTypes = levelGeneratorType.getName().split("\\.", 2);
				if (levelGeneratorTypes.length != 2) continue;

				if (levelGeneratorTypes[1].equals(name)) {
					matches.add(levelGeneratorType);
				}
			}

			if (matches.size() > 1) throw new RuntimeException("Multiple level generator types matching path");
		}

		if (matches.size() != 1) return null;

		return matches.get(0);
	}

	public static final class ChunkGeneratorSupplier {
		private ChunkGeneratorType<?, ? extends ChunkGenerator<?>> chunkGeneratorType;
		private Function<World, BiomeSource> biomeSourceFunction;

		ChunkGeneratorSupplier(ChunkGeneratorType<?, ? extends ChunkGenerator<?>> chunkGeneratorType, Function<World, BiomeSource> biomeSourceFunction) {
			this.chunkGeneratorType = chunkGeneratorType;
			this.biomeSourceFunction = biomeSourceFunction;
		}

		public ChunkGeneratorType<?, ? extends ChunkGenerator<?>> getChunkGeneratorType() {
			return chunkGeneratorType;
		}

		public Function<World, BiomeSource> getBiomeSourceFunction() {
			return biomeSourceFunction;
		}
	}
}
