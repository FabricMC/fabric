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

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockEntityTypeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEnchantmentTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;

public class EnglishTagLangGenerator extends FabricLanguageProvider {
	public EnglishTagLangGenerator(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateTranslations(FabricLanguageProvider.TranslationBuilder translationBuilder) {
		// Blocks
		translationBuilder.add(ConventionalBlockTags.ORES, "Ores");
		translationBuilder.add(ConventionalBlockTags.QUARTZ_ORES, "Quartz Ores");
		translationBuilder.add(ConventionalBlockTags.NETHERITE_SCRAP_ORES, "Netherite Scrapes Ores");
		translationBuilder.add(ConventionalBlockTags.BARRELS, "Barrels");
		translationBuilder.add(ConventionalBlockTags.WOODEN_BARRELS, "Barrels Wooden");
		translationBuilder.add(ConventionalBlockTags.BOOKSHELVES, "Bookshelves");
		translationBuilder.add(ConventionalBlockTags.CHESTS, "Chests");
		translationBuilder.add(ConventionalBlockTags.GLASS_BLOCKS, "Glass Blocks");
		translationBuilder.add(ConventionalBlockTags.GLASS_PANES, "Glass Panes");
		translationBuilder.add(ConventionalBlockTags.SHULKER_BOXES, "Shulker Boxes");
		translationBuilder.add(ConventionalBlockTags.BUDDING_BLOCKS, "Budding Blocks");
		translationBuilder.add(ConventionalBlockTags.BUDS, "Buds");
		translationBuilder.add(ConventionalBlockTags.CLUSTERS, "Clusters");
		translationBuilder.add(ConventionalBlockTags.VILLAGER_JOB_SITES, "Villager Job Sites");
		translationBuilder.add(ConventionalBlockTags.BLOCKS_SANDSTONE, "Sandstone Blocks");
		translationBuilder.add(ConventionalBlockTags.SLABS_SANDSTONE, "Sandstone Slabs");
		translationBuilder.add(ConventionalBlockTags.STAIRS_SANDSTONE, "Sandstone Stairs");
		translationBuilder.add(ConventionalBlockTags.RED_BLOCKS_SANDSTONE, "Red Sandstone Blocks");
		translationBuilder.add(ConventionalBlockTags.RED_SLABS_SANDSTONE, "Red Sandstone Slabs");
		translationBuilder.add(ConventionalBlockTags.RED_STAIRS_SANDSTONE, "Red Sandstone Stairs");
		translationBuilder.add(ConventionalBlockTags.UNCOLORED_BLOCKS_SANDSTONE, "Uncolored Sandstone Blocks");
		translationBuilder.add(ConventionalBlockTags.UNCOLORED_SLABS_SANDSTONE, "Uncolored Sandstone Slabs");
		translationBuilder.add(ConventionalBlockTags.UNCOLORED_STAIRS_SANDSTONE, "Uncolored Sandstone Stairs");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS, "Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.BLACK_DYED_BLOCKS, "Black Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.BLUE_DYED_BLOCKS, "Blue Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.BROWN_DYED_BLOCKS, "Brown Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.CYAN_DYED_BLOCKS, "Cyan Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.GRAY_DYED_BLOCKS, "Gray Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.GREEN_DYED_BLOCKS, "Green Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.LIGHT_BLUE_DYED_BLOCKS, "Light Blue Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.LIGHT_GRAY_DYED_BLOCKS, "Light Gray Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.LIME_DYED_BLOCKS, "Lime Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.MAGENTA_DYED_BLOCKS, "Magenta Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.ORANGE_DYED_BLOCKS, "Orange Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.PINK_DYED_BLOCKS, "Pink Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.PURPLE_DYED_BLOCKS, "Purple Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.RED_DYED_BLOCKS, "Red Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.WHITE_DYED_BLOCKS, "White Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.YELLOW_DYED_BLOCKS, "Yellow Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED, "Relocation Not Supported");

		// Block Entity Types
		translationBuilder.add(ConventionalBlockEntityTypeTags.RELOCATION_NOT_SUPPORTED, "Relocation Not Supported");

		// Items
		translationBuilder.add(ConventionalItemTags.TOOLS, "Tools");
		translationBuilder.add(ConventionalItemTags.SHEARS_TOOLS, "Shears");
		translationBuilder.add(ConventionalItemTags.SPEARS_TOOLS, "Spears");
		translationBuilder.add(ConventionalItemTags.BOWS_TOOLS, "Bows");
		translationBuilder.add(ConventionalItemTags.CROSSBOWS_TOOLS, "Crossbows");
		translationBuilder.add(ConventionalItemTags.SHIELDS_TOOLS, "Shields");
		translationBuilder.add(ConventionalItemTags.FISHING_RODS_TOOLS, "Fishing Rods");
		translationBuilder.add(ConventionalItemTags.DUSTS, "Dusts");
		translationBuilder.add(ConventionalItemTags.GEMS, "Gems");
		translationBuilder.add(ConventionalItemTags.INGOTS, "Ingots");
		translationBuilder.add(ConventionalItemTags.NUGGETS, "Nuggets");
		translationBuilder.add(ConventionalItemTags.ORES, "Ores");
		translationBuilder.add(ConventionalItemTags.RAW_MATERIALS, "Raw Materials");
		translationBuilder.add(ConventionalItemTags.RAW_BLOCKS, "Raw Blocks");
		translationBuilder.add(ConventionalItemTags.IRON_RAW_MATERIALS, "Raw Materials Iron");
		translationBuilder.add(ConventionalItemTags.GOLD_RAW_MATERIALS, "Raw Materials Gold");
		translationBuilder.add(ConventionalItemTags.COPPER_RAW_MATERIALS, "Raw Materials Copper");
		translationBuilder.add(ConventionalItemTags.IRON_RAW_BLOCKS, "Raw Blocks Iron");
		translationBuilder.add(ConventionalItemTags.GOLD_RAW_BLOCKS, "Raw Blocks Gold");
		translationBuilder.add(ConventionalItemTags.COPPER_RAW_BLOCKS, "Raw Blocks Copper");
		translationBuilder.add(ConventionalItemTags.IRON_INGOTS, "Ingots Iron");
		translationBuilder.add(ConventionalItemTags.GOLD_INGOTS, "Ingots Gold");
		translationBuilder.add(ConventionalItemTags.COPPER_INGOTS, "Ingots Copper");
		translationBuilder.add(ConventionalItemTags.NETHERITE_INGOTS, "Ingots Netherite");
		translationBuilder.add(ConventionalItemTags.NETHERITE_SCRAP_ORES, "Netherite Scrap Ores");
		translationBuilder.add(ConventionalItemTags.QUARTZ_ORES, "Quartz Ores");
		translationBuilder.add(ConventionalItemTags.QUARTZ_GEMS, "Quartz Gems");
		translationBuilder.add(ConventionalItemTags.LAPIS_GEMS, "Lapis Gems");
		translationBuilder.add(ConventionalItemTags.DIAMOND_GEMS, "Diamond Gems");
		translationBuilder.add(ConventionalItemTags.AMETHYST_GEMS, "Amethyst Gems");
		translationBuilder.add(ConventionalItemTags.EMERALD_GEMS, "Emerald Gems");
		translationBuilder.add(ConventionalItemTags.PRISMARINE_GEMS, "Prismarine Gems");
		translationBuilder.add(ConventionalItemTags.REDSTONE_DUSTS, "Redstone Dusts");
		translationBuilder.add(ConventionalItemTags.GLOWSTONE_DUSTS, "Glowstone Dusts");
		translationBuilder.add(ConventionalItemTags.COAL, "Coal");
		translationBuilder.add(ConventionalItemTags.FOODS, "Foods");
		translationBuilder.add(ConventionalItemTags.POTIONS, "Potions");
		translationBuilder.add(ConventionalItemTags.WATER_BUCKETS, "Water Buckets");
		translationBuilder.add(ConventionalItemTags.ENTITY_WATER_BUCKETS, "Entity Water Buckets");
		translationBuilder.add(ConventionalItemTags.LAVA_BUCKETS, "Lava Buckets");
		translationBuilder.add(ConventionalItemTags.MILK_BUCKETS, "Milk Buckets");
		translationBuilder.add(ConventionalItemTags.EMPTY_BUCKETS, "Empty Buckets");
		translationBuilder.add(ConventionalItemTags.BARRELS, "Barrels");
		translationBuilder.add(ConventionalItemTags.WOODEN_BARRELS, "Wooden Barrels");
		translationBuilder.add(ConventionalItemTags.BOOKSHELVES, "Bookshelves");
		translationBuilder.add(ConventionalItemTags.CHESTS, "Chests");
		translationBuilder.add(ConventionalItemTags.GLASS_BLOCKS, "Glass Blocks");
		translationBuilder.add(ConventionalItemTags.GLASS_PANES, "Glass Panes");
		translationBuilder.add(ConventionalItemTags.SHULKER_BOXES, "Shulker Boxes");
		translationBuilder.add(ConventionalItemTags.BUDDING_BLOCKS, "Budding Blocks");
		translationBuilder.add(ConventionalItemTags.BUDS, "Buds");
		translationBuilder.add(ConventionalItemTags.CLUSTERS, "Clusters");
		translationBuilder.add(ConventionalItemTags.VILLAGER_JOB_SITES, "Villager Job Sites");
		translationBuilder.add(ConventionalItemTags.BLOCKS_SANDSTONE, "Sandstone Blocks");
		translationBuilder.add(ConventionalItemTags.SLABS_SANDSTONE, "Sandstone Slabs");
		translationBuilder.add(ConventionalItemTags.STAIRS_SANDSTONE, "Sandstone Stairs");
		translationBuilder.add(ConventionalItemTags.RED_BLOCKS_SANDSTONE, "Red Sandstone Blocks");
		translationBuilder.add(ConventionalItemTags.RED_SLABS_SANDSTONE, "Red Sandstone Slabs");
		translationBuilder.add(ConventionalItemTags.RED_STAIRS_SANDSTONE, "Red Sandstone Stairs");
		translationBuilder.add(ConventionalItemTags.UNCOLORED_BLOCKS_SANDSTONE, "Uncolored Sandstone Blocks");
		translationBuilder.add(ConventionalItemTags.UNCOLORED_SLABS_SANDSTONE, "Uncolored Sandstone Slabs");
		translationBuilder.add(ConventionalItemTags.UNCOLORED_STAIRS_SANDSTONE, "Uncolored Sandstone Stairs");
		translationBuilder.add(ConventionalItemTags.DYES, "Dyes");
		translationBuilder.add(ConventionalItemTags.BLACK_DYES, "Black Dyes");
		translationBuilder.add(ConventionalItemTags.BLUE_DYES, "Blue Dyes");
		translationBuilder.add(ConventionalItemTags.BROWN_DYES, "Brown Dyes");
		translationBuilder.add(ConventionalItemTags.CYAN_DYES, "Cyan Dyes");
		translationBuilder.add(ConventionalItemTags.GRAY_DYES, "Gray Dyes");
		translationBuilder.add(ConventionalItemTags.GREEN_DYES, "Green Dyes");
		translationBuilder.add(ConventionalItemTags.LIGHT_BLUE_DYES, "Light Blue Dyes");
		translationBuilder.add(ConventionalItemTags.LIGHT_GRAY_DYES, "Light Gray Dyes");
		translationBuilder.add(ConventionalItemTags.LIME_DYES, "Lime Dyes");
		translationBuilder.add(ConventionalItemTags.MAGENTA_DYES, "Magenta Dyes");
		translationBuilder.add(ConventionalItemTags.ORANGE_DYES, "Orange Dyes");
		translationBuilder.add(ConventionalItemTags.PINK_DYES, "Pink Dyes");
		translationBuilder.add(ConventionalItemTags.PURPLE_DYES, "Purple Dyes");
		translationBuilder.add(ConventionalItemTags.RED_DYES, "Red Dyes");
		translationBuilder.add(ConventionalItemTags.WHITE_DYES, "White Dyes");
		translationBuilder.add(ConventionalItemTags.YELLOW_DYES, "Yellow Dyes");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS, "Dyed Items");
		translationBuilder.add(ConventionalItemTags.BLACK_DYED_ITEMS, "Black Dyed Items");
		translationBuilder.add(ConventionalItemTags.BLUE_DYED_ITEMS, "Blue Dyed Items");
		translationBuilder.add(ConventionalItemTags.BROWN_DYED_ITEMS, "Brown Dyed Items");
		translationBuilder.add(ConventionalItemTags.CYAN_DYED_ITEMS, "Cyan Dyed Items");
		translationBuilder.add(ConventionalItemTags.GRAY_DYED_ITEMS, "Gray Dyed Items");
		translationBuilder.add(ConventionalItemTags.GREEN_DYED_ITEMS, "Green Dyed Items");
		translationBuilder.add(ConventionalItemTags.LIGHT_BLUE_DYED_ITEMS, "Light Blue Dyed Items");
		translationBuilder.add(ConventionalItemTags.LIGHT_GRAY_DYED_ITEMS, "Light Gray Dyed Items");
		translationBuilder.add(ConventionalItemTags.LIME_DYED_ITEMS, "Lime Dyed Items");
		translationBuilder.add(ConventionalItemTags.MAGENTA_DYED_ITEMS, "Magenta Dyed Items");
		translationBuilder.add(ConventionalItemTags.ORANGE_DYED_ITEMS, "Orange Dyed Items");
		translationBuilder.add(ConventionalItemTags.PINK_DYED_ITEMS, "Pink Dyed Items");
		translationBuilder.add(ConventionalItemTags.PURPLE_DYED_ITEMS, "Purple Dyed Items");
		translationBuilder.add(ConventionalItemTags.RED_DYED_ITEMS, "Red Dyed Items");
		translationBuilder.add(ConventionalItemTags.WHITE_DYED_ITEMS, "White Dyed Items");
		translationBuilder.add(ConventionalItemTags.YELLOW_DYED_ITEMS, "Yellow Dyed Items");

		// Enchantments
		translationBuilder.add(ConventionalEnchantmentTags.INCREASES_BLOCK_DROPS, "Increases Block Drops");
		translationBuilder.add(ConventionalEnchantmentTags.INCREASES_ENTITY_DROPS, "Increases Entity Drops");
		translationBuilder.add(ConventionalEnchantmentTags.WEAPON_DAMAGE_ENHANCEMENT, "Weapon Damage Enhancements");
		translationBuilder.add(ConventionalEnchantmentTags.ENTITY_MOVEMENT_ENHANCEMENT, "Entity Movement Enhancements");
		translationBuilder.add(ConventionalEnchantmentTags.ENTITY_DEFENSE_ENHANCEMENT, "Entity Defense Enhancements");

		// Entity Types
		translationBuilder.add(ConventionalEntityTypeTags.BOSSES, "Bosses");
		translationBuilder.add(ConventionalEntityTypeTags.MINECARTS, "Minecarts");
		translationBuilder.add(ConventionalEntityTypeTags.BOATS, "Boats");
		translationBuilder.add(ConventionalEntityTypeTags.CAPTURING_NOT_SUPPORTED, "Capturing Not Supported");

		// Fluids
		translationBuilder.add(ConventionalFluidTags.LAVA, "Lava");
		translationBuilder.add(ConventionalFluidTags.WATER, "Water");
		translationBuilder.add(ConventionalFluidTags.MILK, "Milk");
		translationBuilder.add(ConventionalFluidTags.HONEY, "Honey");

		// Biomes
		translationBuilder.add(ConventionalBiomeTags.IS_OVERWORLD, "Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_THE_END, "The End");
		translationBuilder.add(ConventionalBiomeTags.IS_NETHER, "Nether");
		translationBuilder.add(ConventionalBiomeTags.IS_TAIGA, "Taiga");
		translationBuilder.add(ConventionalBiomeTags.IS_EXTREME_HILLS, "Extreme Hills");
		translationBuilder.add(ConventionalBiomeTags.IS_WINDSWEPT, "Windswept");
		translationBuilder.add(ConventionalBiomeTags.IS_JUNGLE, "Jungle");
		translationBuilder.add(ConventionalBiomeTags.IS_PLAINS, "Plains");
		translationBuilder.add(ConventionalBiomeTags.IS_SAVANNA, "Savanna");
		translationBuilder.add(ConventionalBiomeTags.IS_ICY, "Icy");
		translationBuilder.add(ConventionalBiomeTags.IS_AQUATIC_ICY, "Icy Aquatic");
		translationBuilder.add(ConventionalBiomeTags.IS_BEACH, "Beach");
		translationBuilder.add(ConventionalBiomeTags.IS_FOREST, "Forest");
		translationBuilder.add(ConventionalBiomeTags.IS_BIRCH_FOREST, "Birch Forest");
		translationBuilder.add(ConventionalBiomeTags.IS_OCEAN, "Ocean");
		translationBuilder.add(ConventionalBiomeTags.IS_DESERT, "Desert");
		translationBuilder.add(ConventionalBiomeTags.IS_RIVER, "River");
		translationBuilder.add(ConventionalBiomeTags.IS_SWAMP, "Swamp");
		translationBuilder.add(ConventionalBiomeTags.IS_MUSHROOM, "Mushroom");
		translationBuilder.add(ConventionalBiomeTags.IS_UNDERGROUND, "Underground");
		translationBuilder.add(ConventionalBiomeTags.IS_MOUNTAIN, "Mountain");
		translationBuilder.add(ConventionalBiomeTags.OVERWORLD_IS_HOT, "Hot Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_HOT, "Hot");
		translationBuilder.add(ConventionalBiomeTags.OVERWORLD_IS_TEMPERATE, "Temperate Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_TEMPERATE, "Temperate");
		translationBuilder.add(ConventionalBiomeTags.OVERWORLD_IS_COLD, "Cold Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_COLD, "Cold");
		translationBuilder.add(ConventionalBiomeTags.OVERWORLD_IS_WET, "Wet Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_WET, "Wet");
		translationBuilder.add(ConventionalBiomeTags.OVERWORLD_IS_DRY, "Dry Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_DRY, "Dry");
		translationBuilder.add(ConventionalBiomeTags.IS_VEGETATION_SPARSE, "Sparse Vegetation");
		translationBuilder.add(ConventionalBiomeTags.OVERWORLD_IS_VEGETATION_SPARSE, "Sparse Overworld Vegetation");
		translationBuilder.add(ConventionalBiomeTags.IS_VEGETATION_DENSE, "Dense Vegetation");
		translationBuilder.add(ConventionalBiomeTags.OVERWORLD_IS_VEGETATION_DENSE, "Dense Overworld Vegetation");
		translationBuilder.add(ConventionalBiomeTags.CONIFEROUS_IS_TREE, "Coniferous Tree");
		translationBuilder.add(ConventionalBiomeTags.SAVANNA_IS_TREE, "Savanna Tree");
		translationBuilder.add(ConventionalBiomeTags.JUNGLE_IS_TREE, "Jungle Tree");
		translationBuilder.add(ConventionalBiomeTags.DECIDUOUS_IS_TREE, "Deciduous Tree");
		translationBuilder.add(ConventionalBiomeTags.IS_VOID, "Void");
		translationBuilder.add(ConventionalBiomeTags.PEAK_IS_MOUNTAIN, "Mountain Peak");
		translationBuilder.add(ConventionalBiomeTags.SLOPE_IS_MOUNTAIN, "Mountain Slope");
		translationBuilder.add(ConventionalBiomeTags.IS_AQUATIC, "Aquatic");
		translationBuilder.add(ConventionalBiomeTags.IS_WASTELAND, "Wasteland");
		translationBuilder.add(ConventionalBiomeTags.IS_DEAD, "Dead");
		translationBuilder.add(ConventionalBiomeTags.IS_FLORAL, "Floral");
		translationBuilder.add(ConventionalBiomeTags.IS_SNOWY, "Snowy");
		translationBuilder.add(ConventionalBiomeTags.IS_BADLANDS, "Badlands");
		translationBuilder.add(ConventionalBiomeTags.IS_CAVE, "Cave");
		translationBuilder.add(ConventionalBiomeTags.IS_END_ISLAND, "End Island");
		translationBuilder.add(ConventionalBiomeTags.IS_NETHER_FOREST, "Nether Forest");
		translationBuilder.add(ConventionalBiomeTags.IS_SNOWY_PLAINS, "Snowy Plains");
		translationBuilder.add(ConventionalBiomeTags.IS_STONY_SHORES, "Stony Shores");
		translationBuilder.add(ConventionalBiomeTags.IS_FLOWER_FOREST, "Flower Forest");
		translationBuilder.add(ConventionalBiomeTags.IS_DEEP_OCEAN, "Deep Ocean");
		translationBuilder.add(ConventionalBiomeTags.IS_SHALLOW_OCEAN, "Shallow Ocean");
	}
}
