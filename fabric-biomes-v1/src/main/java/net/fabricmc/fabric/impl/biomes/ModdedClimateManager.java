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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.fabricmc.fabric.api.biomes.v1.Climate;
import net.fabricmc.fabric.api.biomes.v1.VanillaClimate;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public final class ModdedClimateManager {
	private ModdedClimateManager () {}

	/**
	 * The weight of vanilla biomes in modded injections
	 */
	private static final int VANILLA_BIOME_WEIGHT = 3;

	private static final Set<VanillaClimate> INJECTED_CLIMATES = new HashSet<>();

	private static final int BIRCH_FOREST_ID = Registry.BIOME.getRawId(Biomes.BIRCH_FOREST);
	private static final int DESERT_ID = Registry.BIOME.getRawId(Biomes.DESERT);
	private static final int MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.MOUNTAINS);
	private static final int FOREST_ID = Registry.BIOME.getRawId(Biomes.FOREST);
	private static final int SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.SNOWY_TUNDRA);
	private static final int PLAINS_ID = Registry.BIOME.getRawId(Biomes.PLAINS);
	private static final int DARK_FOREST_ID = Registry.BIOME.getRawId(Biomes.DARK_FOREST);
	private static final int SAVANNA_ID = Registry.BIOME.getRawId(Biomes.SAVANNA);
	private static final int SWAMP_ID = Registry.BIOME.getRawId(Biomes.SWAMP);
	private static final int TAIGA_ID = Registry.BIOME.getRawId(Biomes.TAIGA);
	private static final int SNOWY_TAIGA_ID = Registry.BIOME.getRawId(Biomes.SNOWY_TAIGA);

	private static final int[] VANILLA_DRY_BIOMES = new int[]{DESERT_ID, DESERT_ID, DESERT_ID, SAVANNA_ID, SAVANNA_ID, PLAINS_ID};
	private static final int[] VANILLA_TEMPERATE_BIOMES = new int[]{FOREST_ID, DARK_FOREST_ID, MOUNTAINS_ID, PLAINS_ID, BIRCH_FOREST_ID, SWAMP_ID};
	private static final int[] VANILLA_COOL_BIOMES = new int[]{FOREST_ID, MOUNTAINS_ID, TAIGA_ID, PLAINS_ID};
	private static final int[] VANILLA_SNOWY_BIOMES = new int[]{SNOWY_TUNDRA_ID, SNOWY_TUNDRA_ID, SNOWY_TUNDRA_ID, SNOWY_TAIGA_ID};

	public static void setClimateModified(Climate climate) {
		if (climate instanceof VanillaClimate) {
			VanillaClimate vanillaClimate = (VanillaClimate) climate;
			if (INJECTED_CLIMATES.add(vanillaClimate)) {
				List<Biome> buffer = new ArrayList<>();
				final int[] vanillaList;
				switch (vanillaClimate) {
				case COOL:
					vanillaList = VANILLA_COOL_BIOMES;
					break;
				case DRY:
					vanillaList = VANILLA_DRY_BIOMES;
					break;
				case SNOWY:
					vanillaList = VANILLA_SNOWY_BIOMES;
					break;
				case TEMPERATE:
					vanillaList = VANILLA_TEMPERATE_BIOMES;
					break;
				default:
					// should be impossible
					vanillaList = new int[] {};
				}

				for (int biomeId : vanillaList) {
					Biome biome = Registry.BIOME.get(biomeId);
					for (int i = 1; i < VANILLA_BIOME_WEIGHT; ++i) // Since there is already one entry in the vanilla array
						buffer.add(biome);
				}
				for (Biome biome : buffer)
					InternalBiomeData.INJECTED_BIOMES.add(new ClimateBiomeEntry(biome, climate));
			}
		}
	}
}
