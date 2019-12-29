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

package net.fabricmc.fabric.impl.levelgenerator;

import java.util.HashMap;
import java.util.function.Function;

import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.fabric.mixin.levelgenerator.AccessLevelGeneratorType;

public final class FabricLevelGeneratorType {
	public static final HashMap<LevelGeneratorType, Pair<ChunkGeneratorType<?, ? extends ChunkGenerator<?>>, Function<World, BiomeSource>>> suppliers = new HashMap<>();

	public static LevelGeneratorType create(Identifier name, Identifier storedName, int version, ChunkGeneratorType<?, ? extends ChunkGenerator<?>> generatorType, Function<World, BiomeSource> biomeSource) {
		LevelGeneratorType levelType = AccessLevelGeneratorType.fabric_init(getFreeId(), name.toString(), storedName.toString(), version);
		suppliers.put(levelType, new Pair<>(generatorType, biomeSource));
		return levelType;
	}

	private static int getFreeId() {
		for (int id = 0; id < LevelGeneratorType.TYPES.length; id++) {
			if (LevelGeneratorType.TYPES[id] == null) {
				return id;
			}
		}

		throw new RuntimeException("No more free id's");
	}

	public static LevelGeneratorType getTypeFromPath(String name) {
		for (int id = 0; id < LevelGeneratorType.TYPES.length; id++) {
			LevelGeneratorType levelGeneratorType = LevelGeneratorType.TYPES[id];

			if (levelGeneratorType != null) {
				String[] levelGeneratorTypePath = levelGeneratorType.getName().split(":");
				if (levelGeneratorTypePath.length < 2) continue;
				if (levelGeneratorTypePath[1].equalsIgnoreCase(name)) return levelGeneratorType;
			}
		}

		return null;
	}
}
