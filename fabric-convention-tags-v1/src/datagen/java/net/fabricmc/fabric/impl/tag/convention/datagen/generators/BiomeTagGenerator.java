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

package net.fabricmc.fabric.impl.tag.convention.datagen.generators;

import java.util.concurrent.CompletableFuture;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;

public class BiomeTagGenerator extends FabricTagProvider<Biome> {
	public BiomeTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(output, RegistryKeys.BIOME, completableFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup arg) {
		generateDimensionTags();
		generateCategoryTags();
		generateOtherBiomeTypes();
		generateClimateAndVegetationTags();
		generateTerrainDescriptorTags();
	}

	private void generateDimensionTags() {
		getOrCreateTagBuilder(ConventionalBiomeTags.IN_NETHER)
				.addOptionalTag(BiomeTags.IS_NETHER)
				.add(BiomeKeys.CRIMSON_FOREST)
				.add(BiomeKeys.WARPED_FOREST)
				.add(BiomeKeys.NETHER_WASTES)
				.add(BiomeKeys.SOUL_SAND_VALLEY)
				.add(BiomeKeys.BASALT_DELTAS);
		getOrCreateTagBuilder(ConventionalBiomeTags.IN_THE_END)
				.addOptionalTag(BiomeTags.IS_END)
				.add(BiomeKeys.END_BARRENS)
				.add(BiomeKeys.END_MIDLANDS)
				.add(BiomeKeys.END_HIGHLANDS)
				.add(BiomeKeys.THE_END)
				.add(BiomeKeys.SMALL_END_ISLANDS);
		// We avoid the vanilla group tags here as mods may add to them without actually spawning them in the overworld
		getOrCreateTagBuilder(ConventionalBiomeTags.IN_OVERWORLD)
				.addOptionalTag(BiomeTags.IS_OVERWORLD)
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
				.add(BiomeKeys.LUSH_CAVES).add(BiomeKeys.SNOWY_BEACH).add(BiomeKeys.SWAMP).add(BiomeKeys.STONY_SHORE)
				.add(BiomeKeys.DEEP_DARK).add(BiomeKeys.MANGROVE_SWAMP)
				.add(BiomeKeys.CHERRY_GROVE);
	}

	private void generateCategoryTags() {
		getOrCreateTagBuilder(ConventionalBiomeTags.TAIGA)
				.addOptionalTag(BiomeTags.IS_TAIGA);
		getOrCreateTagBuilder(ConventionalBiomeTags.EXTREME_HILLS)
				.add(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS)
				.add(BiomeKeys.WINDSWEPT_HILLS);
		getOrCreateTagBuilder(ConventionalBiomeTags.WINDSWEPT)
				.add(BiomeKeys.WINDSWEPT_HILLS)
				.add(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS)
				.add(BiomeKeys.WINDSWEPT_FOREST)
				.add(BiomeKeys.WINDSWEPT_SAVANNA);
		getOrCreateTagBuilder(ConventionalBiomeTags.JUNGLE)
				.addOptionalTag(BiomeTags.IS_JUNGLE);
		getOrCreateTagBuilder(ConventionalBiomeTags.MESA)
				.add(BiomeKeys.WOODED_BADLANDS)
				.add(BiomeKeys.ERODED_BADLANDS)
				.add(BiomeKeys.BADLANDS);
		getOrCreateTagBuilder(ConventionalBiomeTags.PLAINS)
				.add(BiomeKeys.SUNFLOWER_PLAINS)
				.add(BiomeKeys.PLAINS);
		getOrCreateTagBuilder(ConventionalBiomeTags.SAVANNA)
				.addOptionalTag(BiomeTags.IS_SAVANNA)
				.add(BiomeKeys.SAVANNA_PLATEAU)
				.add(BiomeKeys.WINDSWEPT_SAVANNA)
				.add(BiomeKeys.SAVANNA);
		getOrCreateTagBuilder(ConventionalBiomeTags.ICY)
				.add(BiomeKeys.FROZEN_PEAKS)
				.add(BiomeKeys.ICE_SPIKES);
		getOrCreateTagBuilder(ConventionalBiomeTags.AQUATIC_ICY)
				.add(BiomeKeys.FROZEN_RIVER)
				.add(BiomeKeys.DEEP_FROZEN_OCEAN)
				.add(BiomeKeys.FROZEN_OCEAN);
		getOrCreateTagBuilder(ConventionalBiomeTags.SNOWY)
				.add(BiomeKeys.SNOWY_BEACH)
				.add(BiomeKeys.SNOWY_PLAINS)
				.add(BiomeKeys.SNOWY_SLOPES)
				.add(BiomeKeys.SNOWY_TAIGA);
		getOrCreateTagBuilder(ConventionalBiomeTags.BEACH)
				.addOptionalTag(BiomeTags.IS_BEACH)
				.add(BiomeKeys.STONY_SHORE);
		getOrCreateTagBuilder(ConventionalBiomeTags.FOREST)
				.addOptionalTag(BiomeTags.IS_FOREST);
		getOrCreateTagBuilder(ConventionalBiomeTags.BIRCH_FOREST)
				.add(BiomeKeys.BIRCH_FOREST)
				.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST);
		getOrCreateTagBuilder(ConventionalBiomeTags.OCEAN)
				.addOptionalTag(ConventionalBiomeTags.DEEP_OCEAN)
				.addOptionalTag(ConventionalBiomeTags.SHALLOW_OCEAN)
				.addOptionalTag(BiomeTags.IS_OCEAN);
		getOrCreateTagBuilder(ConventionalBiomeTags.DESERT)
				.add(BiomeKeys.DESERT);
		getOrCreateTagBuilder(ConventionalBiomeTags.RIVER)
				.addOptionalTag(BiomeTags.IS_RIVER);
		getOrCreateTagBuilder(ConventionalBiomeTags.SWAMP)
				.add(BiomeKeys.MANGROVE_SWAMP)
				.add(BiomeKeys.SWAMP);
		getOrCreateTagBuilder(ConventionalBiomeTags.MUSHROOM)
				.add(BiomeKeys.MUSHROOM_FIELDS);
		getOrCreateTagBuilder(ConventionalBiomeTags.UNDERGROUND)
				.addOptionalTag(ConventionalBiomeTags.CAVES);
		getOrCreateTagBuilder(ConventionalBiomeTags.MOUNTAIN)
				.addOptionalTag(BiomeTags.IS_MOUNTAIN);
	}

	private void generateOtherBiomeTypes() {
		getOrCreateTagBuilder(ConventionalBiomeTags.BADLANDS)
				.addOptionalTag(ConventionalBiomeTags.MESA)
				.addOptionalTag(BiomeTags.IS_BADLANDS);
		getOrCreateTagBuilder(ConventionalBiomeTags.CAVES)
				.add(BiomeKeys.DEEP_DARK)
				.add(BiomeKeys.DRIPSTONE_CAVES)
				.add(BiomeKeys.LUSH_CAVES);
		getOrCreateTagBuilder(ConventionalBiomeTags.VOID)
				.add(BiomeKeys.THE_VOID);
		getOrCreateTagBuilder(ConventionalBiomeTags.DEEP_OCEAN)
				.add(BiomeKeys.DEEP_FROZEN_OCEAN)
				.add(BiomeKeys.DEEP_LUKEWARM_OCEAN)
				.add(BiomeKeys.DEEP_COLD_OCEAN)
				.add(BiomeKeys.DEEP_OCEAN);
		getOrCreateTagBuilder(ConventionalBiomeTags.SHALLOW_OCEAN)
				.add(BiomeKeys.OCEAN)
				.add(BiomeKeys.LUKEWARM_OCEAN)
				.add(BiomeKeys.FROZEN_OCEAN)
				.add(BiomeKeys.COLD_OCEAN)
				.add(BiomeKeys.WARM_OCEAN);
	}

	private void generateClimateAndVegetationTags() {
		getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_COLD)
				.add(BiomeKeys.SNOWY_PLAINS)
				.add(BiomeKeys.GROVE)
				.add(BiomeKeys.JAGGED_PEAKS)
				.add(BiomeKeys.TAIGA).add(BiomeKeys.SNOWY_TAIGA)
				.add(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA).add(BiomeKeys.OLD_GROWTH_PINE_TAIGA)
				.addOptionalTag(ConventionalBiomeTags.ICY);
		getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_TEMPERATE)
				.add(BiomeKeys.FOREST)
				.add(BiomeKeys.SUNFLOWER_PLAINS)
				.add(BiomeKeys.SWAMP)
				.add(BiomeKeys.STONY_SHORE)
				.add(BiomeKeys.DARK_FOREST)
				.add(BiomeKeys.WINDSWEPT_FOREST)
				.add(BiomeKeys.BIRCH_FOREST)
				.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST)
				.add(BiomeKeys.MEADOW)
				.add(BiomeKeys.PLAINS);
		getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_HOT)
				.addOptionalTag(ConventionalBiomeTags.JUNGLE)
				.addOptionalTag(ConventionalBiomeTags.SAVANNA)
				.addOptionalTag(ConventionalBiomeTags.DESERT)
				.addOptionalTag(ConventionalBiomeTags.BADLANDS)
				.add(BiomeKeys.STONY_PEAKS)
				.addOptionalTag(ConventionalBiomeTags.MUSHROOM)
				.addOptionalTag(ConventionalBiomeTags.IN_NETHER);
		getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_WET)
				.addOptionalTag(ConventionalBiomeTags.AQUATIC)
				.addOptionalTag(ConventionalBiomeTags.SWAMP)
				.add(BiomeKeys.LUSH_CAVES)
				.addOptionalTag(ConventionalBiomeTags.JUNGLE);
		getOrCreateTagBuilder(ConventionalBiomeTags.CLIMATE_DRY)
				.addOptionalTag(ConventionalBiomeTags.IN_NETHER)
				.addOptionalTag(ConventionalBiomeTags.BADLANDS)
				.addOptionalTag(ConventionalBiomeTags.DESERT)
				.addOptionalTag(ConventionalBiomeTags.SAVANNA);
		getOrCreateTagBuilder(ConventionalBiomeTags.VEGETATION_DENSE)
				.addOptionalTag(ConventionalBiomeTags.JUNGLE)
				.add(BiomeKeys.SUNFLOWER_PLAINS);
		getOrCreateTagBuilder(ConventionalBiomeTags.VEGETATION_SPARSE)
				.addOptionalTag(ConventionalBiomeTags.SAVANNA)
				.addOptionalTag(ConventionalBiomeTags.DESERT)
				.addOptionalTag(ConventionalBiomeTags.DEAD)
				.add(BiomeKeys.LUSH_CAVES)
				.addOptionalTag(ConventionalBiomeTags.WASTELAND);
		getOrCreateTagBuilder(ConventionalBiomeTags.TREE_CONIFEROUS)
				.add(BiomeKeys.GROVE)
				.addOptionalTag(ConventionalBiomeTags.TAIGA);
		getOrCreateTagBuilder(ConventionalBiomeTags.TREE_DECIDUOUS)
				.add(BiomeKeys.FOREST)
				.add(BiomeKeys.WINDSWEPT_FOREST)
				.add(BiomeKeys.FLOWER_FOREST)
				.add(BiomeKeys.BIRCH_FOREST)
				.add(BiomeKeys.DARK_FOREST)
				.add(BiomeKeys.OLD_GROWTH_BIRCH_FOREST);
		getOrCreateTagBuilder(ConventionalBiomeTags.TREE_JUNGLE)
				.addOptionalTag(ConventionalBiomeTags.JUNGLE);
		getOrCreateTagBuilder(ConventionalBiomeTags.TREE_SAVANNA)
				.addOptionalTag(ConventionalBiomeTags.SAVANNA);
		getOrCreateTagBuilder(ConventionalBiomeTags.FLORAL)
				.add(BiomeKeys.SUNFLOWER_PLAINS)
				.add(BiomeKeys.MEADOW)
				.add(BiomeKeys.CHERRY_GROVE)
				.addOptionalTag(ConventionalBiomeTags.FLOWER_FORESTS);
	}

	private void generateTerrainDescriptorTags() {
		getOrCreateTagBuilder(ConventionalBiomeTags.MOUNTAIN_PEAK)
				.add(BiomeKeys.FROZEN_PEAKS)
				.add(BiomeKeys.JAGGED_PEAKS)
				.add(BiomeKeys.STONY_PEAKS);
		getOrCreateTagBuilder(ConventionalBiomeTags.MOUNTAIN_SLOPE)
				.add(BiomeKeys.SNOWY_SLOPES);
		getOrCreateTagBuilder(ConventionalBiomeTags.AQUATIC)
				.addOptionalTag(ConventionalBiomeTags.OCEAN)
				.addOptionalTag(ConventionalBiomeTags.RIVER);
		getOrCreateTagBuilder(ConventionalBiomeTags.DEAD);
		getOrCreateTagBuilder(ConventionalBiomeTags.WASTELAND);
		getOrCreateTagBuilder(ConventionalBiomeTags.END_ISLANDS);
		getOrCreateTagBuilder(ConventionalBiomeTags.NETHER_FORESTS)
				.add(BiomeKeys.WARPED_FOREST)
				.add(BiomeKeys.CRIMSON_FOREST);
		getOrCreateTagBuilder(ConventionalBiomeTags.SNOWY_PLAINS)
				.add(BiomeKeys.SNOWY_PLAINS);
		getOrCreateTagBuilder(ConventionalBiomeTags.STONY_SHORES)
				.add(BiomeKeys.STONY_SHORE);
		getOrCreateTagBuilder(ConventionalBiomeTags.FLOWER_FORESTS)
				.add(BiomeKeys.FLOWER_FOREST);
	}
}
