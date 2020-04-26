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
import java.util.Arrays;

import net.minecraft.world.level.LevelGeneratorType;

import net.fabricmc.fabric.api.level.generator.v1.FabricLevelGeneratorType;
import net.fabricmc.fabric.mixin.level.generator.LevelGeneratorTypeAccessor;

public final class FabricLevelGeneratorTypeImpl {
	public static int getFreeId() {
		for (int id = 0; id < LevelGeneratorType.TYPES.length; id++) {
			if (LevelGeneratorType.TYPES[id] == null) {
				return id;
			}
		}

		int length = LevelGeneratorType.TYPES.length;
		LevelGeneratorTypeAccessor.setTypes(Arrays.copyOf(LevelGeneratorType.TYPES, length + 16));
		return length;
	}

	public static LevelGeneratorType checkForFabricLevelGeneratorType(LevelGeneratorType levelGeneratorType) {
		if (!(levelGeneratorType instanceof FabricLevelGeneratorType)) {
			return levelGeneratorType;
		}

		return LevelGeneratorType.DEFAULT;
	}

	public static LevelGeneratorType getTypeFromPath(String name) {
		ArrayList<LevelGeneratorType> matches = new ArrayList<>();

		for (int id = 0; id < LevelGeneratorType.TYPES.length; id++) {
			LevelGeneratorType levelGeneratorType = LevelGeneratorType.TYPES[id];
			String[] levelGeneratorTypes;

			if (levelGeneratorType != null) {
				levelGeneratorTypes = levelGeneratorType.getName().split(":", 2);
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
}
