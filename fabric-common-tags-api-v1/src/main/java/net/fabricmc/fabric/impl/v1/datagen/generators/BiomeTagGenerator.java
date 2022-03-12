package net.fabricmc.fabric.impl.v1.datagen.generators;

import net.minecraft.tag.BiomeTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tags.v1.CommonBiomeTags;

public class BiomeTagGenerator extends FabricTagProvider.DynamicRegistryTagProvider<Biome> {
	public BiomeTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator, Registry.BIOME_KEY, "biomes", "Biome Tags");
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
				.addOptionalTag(BiomeTags.STRONGHOLD_HAS_STRUCTURE)// This has most overworld biomes already
				.add(BiomeKeys.RIVER)
				.add(BiomeKeys.FROZEN_RIVER)
				.add(BiomeKeys.COLD_OCEAN)
				.add(BiomeKeys.DEEP_COLD_OCEAN)
				.add(BiomeKeys.DEEP_FROZEN_OCEAN)
				.add(BiomeKeys.DEEP_OCEAN)
				.add(BiomeKeys.DEEP_LUKEWARM_OCEAN)
				.add(BiomeKeys.WARM_OCEAN)
				.add(BiomeKeys.LUKEWARM_OCEAN)
				.add(BiomeKeys.FROZEN_OCEAN)
				.add(BiomeKeys.OCEAN)
				.add(BiomeKeys.BEACH)
				.add(BiomeKeys.SNOWY_BEACH);
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
