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

package net.fabricmc.fabric.impl.tag.common.datagen.generators;

import net.minecraft.tag.BiomeTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.v1.CommonBiomeTags;

public class BiomeTagGenerator extends FabricTagProvider.DynamicRegistryTagProvider<Biome> {
	public BiomeTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator, Registry.BIOME_KEY, "worldgen/biome", "Biome Tags");
	}

	@Override
	protected void generateTags() {
		generateDimensionTags();
		generateCategoryTags();
		generateOtherBiomeTypes();
		generateClimateAndVegetationTags();
		generateTerrainDescriptorTags();
	}

	private void generateDimensionTags() {
		getOrCreateTagBuilder(CommonBiomeTags.IN_NETHER)
				.addOptionalTag(BiomeTags.IS_NETHER)
				.add(BiomeKeys.CRIMSON_FOREST)
				.add(BiomeKeys.WARPED_FOREST)
				.add(BiomeKeys.NETHER_WASTES)
				.add(BiomeKeys.SOUL_SAND_VALLEY)
				.add(BiomeKeys.BASALT_DELTAS);
		getOrCreateTagBuilder(CommonBiomeTags.IN_THE_END)
				.add(BiomeKeys.END_BARRENS)
				.add(BiomeKeys.END_MIDLANDS)
				.add(BiomeKeys.END_HIGHLANDS)
				.add(BiomeKeys.THE_END)
				.add(BiomeKeys.SMALL_END_ISLANDS)
				.add(BiomeKeys.THE_VOID);
		// We avoid the vanilla group tags here as mods may add to them without actually spawning them in the overworld
		getOrCreateTagBuilder(CommonBiomeTags.IN_OVERWORLD)
				.add(BiomeKeys.RIVER).add(BiomeKeys.FROZEN_RIVER)
				.add(BiomeKeys.COLD_OCEAN).add(BiomeKeys.DEEP_COLD_OCEAN)
				.add(BiomeKeys.DEEP_FROZEN_OCEAN).add(BiomeKeys.DEEP_OCEAN)
				.add(BiomeKeys.DEEP_LUKEWARM_OCEAN).add(BiomeKeys.WARM_OCEAN).add(BiomeKeys.LUKEWARM_OCEAN)
				.add(BiomeKeys.FROZEN_OCEAN).add(BiomeKeys.OCEAN)
				.add(BiomeKeys.BEACH).add(BiomeKeys.PLAINS)
				.add(BiomeKeys.SUNFLOWER_PLAINS).add(BiomeKeys.SNOWY_PLAINS)
				.add(BiomeKeys.ICE_SPIKES).add(BiomeKeys.DESERT)
				.add(BiomeKeys.FOREST).add(BiomeKeys.FLOWER_FOREST)
				.add(BiomeKeys.BIRCH_FOREST).add(BiomeKeys.DARK_FOREST)
				.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST).add(BiomeKeys.OLD_GROWTH_PINE_TAIGA)
				.add(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA).add(BiomeKeys.TAIGA)
				.add(BiomeKeys.SNOWY_TAIGA).add(BiomeKeys.SAVANNA)
				.add(BiomeKeys.SAVANNA_PLATEAU).add(BiomeKeys.WINDSWEPT_HILLS)
				.add(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS).add(BiomeKeys.WINDSWEPT_FOREST)
				.add(BiomeKeys.WINDSWEPT_SAVANNA).add(BiomeKeys.JUNGLE).add(BiomeKeys.SPARSE_JUNGLE)
				.add(BiomeKeys.BAMBOO_JUNGLE).add(BiomeKeys.BADLANDS).add(BiomeKeys.ERODED_BADLANDS)
				.add(BiomeKeys.WOODED_BADLANDS).add(BiomeKeys.MEADOW).add(BiomeKeys.GROVE)
				.add(BiomeKeys.SNOWY_SLOPES).add(BiomeKeys.FROZEN_PEAKS).add(BiomeKeys.JAGGED_PEAKS)
				.add(BiomeKeys.STONY_PEAKS).add(BiomeKeys.MUSHROOM_FIELDS).add(BiomeKeys.DRIPSTONE_CAVES)
				.add(BiomeKeys.LUSH_CAVES).add(BiomeKeys.SNOWY_BEACH);
	}

	/**
	 * See {@link Biome.Category} for details.
	 */
	private void generateCategoryTags() {
		getOrCreateTagBuilder(CommonBiomeTags.TAIGA)
				.addOptionalTag(BiomeTags.IS_TAIGA);
		getOrCreateTagBuilder(CommonBiomeTags.EXTREME_HILLS)
				.add(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS)
				.add(BiomeKeys.WINDSWEPT_HILLS);
		getOrCreateTagBuilder(CommonBiomeTags.JUNGLE)
				.addOptionalTag(BiomeTags.IS_JUNGLE);
		getOrCreateTagBuilder(CommonBiomeTags.MESA)
				.add(BiomeKeys.WOODED_BADLANDS)
				.add(BiomeKeys.ERODED_BADLANDS)
				.add(BiomeKeys.BADLANDS);
		getOrCreateTagBuilder(CommonBiomeTags.PLAINS)
				.add(BiomeKeys.SNOWY_PLAINS)
				.add(BiomeKeys.SUNFLOWER_PLAINS)
				.add(BiomeKeys.PLAINS);
		getOrCreateTagBuilder(CommonBiomeTags.SAVANNA)
				.add(BiomeKeys.SAVANNA_PLATEAU)
				.add(BiomeKeys.WINDSWEPT_SAVANNA)
				.add(BiomeKeys.SAVANNA);
		getOrCreateTagBuilder(CommonBiomeTags.ICY)
				.add(BiomeKeys.FROZEN_RIVER)
				.add(BiomeKeys.FROZEN_PEAKS)
				.add(BiomeKeys.DEEP_FROZEN_OCEAN)
				.add(BiomeKeys.SNOWY_BEACH)
				.add(BiomeKeys.SNOWY_PLAINS)
				.add(BiomeKeys.SNOWY_SLOPES)
				.add(BiomeKeys.SNOWY_TAIGA)
				.add(BiomeKeys.FROZEN_OCEAN);
		getOrCreateTagBuilder(CommonBiomeTags.BEACH)
				.addOptionalTag(BiomeTags.IS_BEACH)
				.add(BiomeKeys.STONY_SHORE);
		getOrCreateTagBuilder(CommonBiomeTags.FOREST)
				.addOptionalTag(BiomeTags.IS_FOREST);
		getOrCreateTagBuilder(CommonBiomeTags.OCEAN)
				.addOptionalTag(BiomeTags.IS_OCEAN);
		getOrCreateTagBuilder(CommonBiomeTags.DESERT)
				.add(BiomeKeys.DESERT);
		getOrCreateTagBuilder(CommonBiomeTags.RIVER)
				.addOptionalTag(BiomeTags.IS_RIVER);
		getOrCreateTagBuilder(CommonBiomeTags.SWAMP)
				.add(BiomeKeys.SWAMP);
		getOrCreateTagBuilder(CommonBiomeTags.MUSHROOM)
				.add(BiomeKeys.MUSHROOM_FIELDS);
		getOrCreateTagBuilder(CommonBiomeTags.UNDERGROUND)
				.addOptionalTag(CommonBiomeTags.CAVES);
		getOrCreateTagBuilder(CommonBiomeTags.MOUNTAIN)
				.addOptionalTag(BiomeTags.IS_MOUNTAIN);
	}

	private void generateOtherBiomeTypes() {
		getOrCreateTagBuilder(CommonBiomeTags.BADLANDS)
				.addOptionalTag(CommonBiomeTags.MESA)
				.addOptionalTag(BiomeTags.IS_BADLANDS);
		getOrCreateTagBuilder(CommonBiomeTags.CAVES)
				.add(BiomeKeys.DRIPSTONE_CAVES)
				.add(BiomeKeys.LUSH_CAVES);
		getOrCreateTagBuilder(CommonBiomeTags.VOID)
				.add(BiomeKeys.THE_VOID);
	}

	private void generateClimateAndVegetationTags() {
		getOrCreateTagBuilder(CommonBiomeTags.CLIMATE_COLD)
				.addOptionalTag(CommonBiomeTags.ICY);
		getOrCreateTagBuilder(CommonBiomeTags.CLIMATE_TEMPERATE);
		getOrCreateTagBuilder(CommonBiomeTags.CLIMATE_HOT)
				.addOptionalTag(CommonBiomeTags.JUNGLE)
				.addOptionalTag(CommonBiomeTags.SAVANNA)
				.addOptionalTag(CommonBiomeTags.DESERT)
				.addOptionalTag(CommonBiomeTags.BADLANDS)
				.addOptionalTag(CommonBiomeTags.IN_NETHER);
		getOrCreateTagBuilder(CommonBiomeTags.CLIMATE_WET)
				.addOptionalTag(CommonBiomeTags.AQUATIC)
				.add(BiomeKeys.LUSH_CAVES)
				.addOptionalTag(CommonBiomeTags.JUNGLE);
		getOrCreateTagBuilder(CommonBiomeTags.CLIMATE_DRY)
				.addOptionalTag(CommonBiomeTags.IN_NETHER)
				.addOptionalTag(CommonBiomeTags.BADLANDS)
				.addOptionalTag(CommonBiomeTags.DESERT)
				.addOptionalTag(CommonBiomeTags.SAVANNA);
		getOrCreateTagBuilder(CommonBiomeTags.VEGETATION_DENSE)
				.addOptionalTag(CommonBiomeTags.JUNGLE)
				.add(BiomeKeys.SUNFLOWER_PLAINS);
		getOrCreateTagBuilder(CommonBiomeTags.VEGETATION_SPARSE)
				.addOptionalTag(CommonBiomeTags.SAVANNA)
				.addOptionalTag(CommonBiomeTags.DESERT)
				.addOptionalTag(CommonBiomeTags.DEAD)
				.add(BiomeKeys.LUSH_CAVES)
				.addOptionalTag(CommonBiomeTags.WASTELAND);
		getOrCreateTagBuilder(CommonBiomeTags.TREE_CONIFEROUS)
				.addOptionalTag(CommonBiomeTags.TAIGA);
		getOrCreateTagBuilder(CommonBiomeTags.TREE_DECIDUOUS)
				.add(BiomeKeys.FOREST)
				.add(BiomeKeys.WINDSWEPT_FOREST)
				.add(BiomeKeys.DARK_FOREST)
				.add(BiomeKeys.LUSH_CAVES)
				.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST);
		getOrCreateTagBuilder(CommonBiomeTags.TREE_JUNGLE)
				.addOptionalTag(CommonBiomeTags.JUNGLE);
		getOrCreateTagBuilder(CommonBiomeTags.TREE_SAVANNA)
				.addOptionalTag(CommonBiomeTags.SAVANNA);
		getOrCreateTagBuilder(CommonBiomeTags.FLORAL)
				.add(BiomeKeys.SUNFLOWER_PLAINS)
				.add(BiomeKeys.FLOWER_FOREST);
	}

	private void generateTerrainDescriptorTags() {
		getOrCreateTagBuilder(CommonBiomeTags.MOUNTAIN_PEAK)
				.add(BiomeKeys.FROZEN_PEAKS)
				.add(BiomeKeys.JAGGED_PEAKS)
				.add(BiomeKeys.STONY_PEAKS);
		getOrCreateTagBuilder(CommonBiomeTags.MOUNTAIN_SLOPE)
				.add(BiomeKeys.SNOWY_SLOPES);
		getOrCreateTagBuilder(CommonBiomeTags.AQUATIC)
				.addOptionalTag(CommonBiomeTags.OCEAN)
				.addOptionalTag(CommonBiomeTags.RIVER);
		getOrCreateTagBuilder(CommonBiomeTags.DEAD);
		getOrCreateTagBuilder(CommonBiomeTags.WASTELAND);
		getOrCreateTagBuilder(CommonBiomeTags.END_ISLANDS);
		getOrCreateTagBuilder(CommonBiomeTags.NETHER_FORESTS)
				.add(BiomeKeys.WARPED_FOREST)
				.add(BiomeKeys.CRIMSON_FOREST);
		getOrCreateTagBuilder(CommonBiomeTags.SNOWY_PLAINS)
				.add(BiomeKeys.SNOWY_PLAINS);
		getOrCreateTagBuilder(CommonBiomeTags.STONY_SHORES)
				.add(BiomeKeys.STONY_SHORE);
		getOrCreateTagBuilder(CommonBiomeTags.FLOWER_FORESTS)
				.add(BiomeKeys.FLOWER_FOREST);
	}
}
