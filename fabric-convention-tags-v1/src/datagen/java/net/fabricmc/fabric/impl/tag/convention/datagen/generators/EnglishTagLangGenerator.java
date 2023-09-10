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
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockEntityTypeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEnchantmentTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEntityTypeTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalFluidTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;

public class EnglishTagLangGenerator extends FabricLanguageProvider {
	public EnglishTagLangGenerator(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateTranslations(FabricLanguageProvider.TranslationBuilder translationBuilder) {
		// Blocks
		translationBuilder.add(ConventionalBlockTags.ORES, "Ores");
		translationBuilder.add(ConventionalBlockTags.ORES_QUARTZ, "Quartz Ores");
		translationBuilder.add(ConventionalBlockTags.ORES_NETHERITE_SCRAP, "Netherite Scrapes Ores");
		translationBuilder.add(ConventionalBlockTags.BARRELS, "Barrels");
		translationBuilder.add(ConventionalBlockTags.BARRELS_WOODEN, "Barrels Wooden");
		translationBuilder.add(ConventionalBlockTags.BOOKSHELVES, "Bookshelves");
		translationBuilder.add(ConventionalBlockTags.CHESTS, "Chests");
		translationBuilder.add(ConventionalBlockTags.GLASS_BLOCKS, "Glass Blocks");
		translationBuilder.add(ConventionalBlockTags.GLASS_PANES, "Glass Panes");
		translationBuilder.add(ConventionalBlockTags.SHULKER_BOXES, "Shulker Boxes");
		translationBuilder.add(ConventionalBlockTags.BUDDING_BLOCKS, "Budding Blocks");
		translationBuilder.add(ConventionalBlockTags.BUDS, "Buds");
		translationBuilder.add(ConventionalBlockTags.CLUSTERS, "Clusters");
		translationBuilder.add(ConventionalBlockTags.VILLAGER_JOB_SITES, "Villager Job Sites");
		translationBuilder.add(ConventionalBlockTags.SANDSTONE_BLOCKS, "Sandstone Blocks");
		translationBuilder.add(ConventionalBlockTags.SANDSTONE_SLABS, "Sandstone Slabs");
		translationBuilder.add(ConventionalBlockTags.SANDSTONE_STAIRS, "Sandstone Stairs");
		translationBuilder.add(ConventionalBlockTags.SANDSTONE_RED_BLOCKS, "Red Sandstone Blocks");
		translationBuilder.add(ConventionalBlockTags.SANDSTONE_RED_SLABS, "Red Sandstone Slabs");
		translationBuilder.add(ConventionalBlockTags.SANDSTONE_RED_STAIRS, "Red Sandstone Stairs");
		translationBuilder.add(ConventionalBlockTags.SANDSTONE_UNCOLORED_BLOCKS, "Uncolored Sandstone Blocks");
		translationBuilder.add(ConventionalBlockTags.SANDSTONE_UNCOLORED_SLABS, "Uncolored Sandstone Slabs");
		translationBuilder.add(ConventionalBlockTags.SANDSTONE_UNCOLORED_STAIRS, "Uncolored Sandstone Stairs");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS, "Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_BLACK, "Black Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_BLUE, "Blue Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_BROWN, "Brown Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_CYAN, "Cyan Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_GRAY, "Gray Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_GREEN, "Green Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_LIGHT_BLUE, "Light Blue Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_LIGHT_GRAY, "Light Gray Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_LIME, "Lime Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_MAGENTA, "Magenta Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_ORANGE, "Orange Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_PINK, "Pink Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_PURPLE, "Purple Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_RED, "Red Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_WHITE, "White Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.DYED_BLOCKS_YELLOW, "Yellow Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED, "Relocation Not Supported");

		// Block Entity Types
		translationBuilder.add(ConventionalBlockEntityTypeTags.RELOCATION_NOT_SUPPORTED, "Relocation Not Supported");

		// Items
		translationBuilder.add(ConventionalItemTags.TOOLS, "Tools");
		translationBuilder.add(ConventionalItemTags.TOOLS_SHEARS, "Shears");
		translationBuilder.add(ConventionalItemTags.TOOLS_SPEARS, "Spears");
		translationBuilder.add(ConventionalItemTags.TOOLS_BOWS, "Bows");
		translationBuilder.add(ConventionalItemTags.TOOLS_CROSSBOWS, "Crossbows");
		translationBuilder.add(ConventionalItemTags.TOOLS_SHIELDS, "Shields");
		translationBuilder.add(ConventionalItemTags.TOOLS_FISHING_RODS, "Fishing Rods");
		translationBuilder.add(ConventionalItemTags.DUSTS, "Dusts");
		translationBuilder.add(ConventionalItemTags.GEMS, "Gems");
		translationBuilder.add(ConventionalItemTags.INGOTS, "Ingots");
		translationBuilder.add(ConventionalItemTags.NUGGETS, "Nuggets");
		translationBuilder.add(ConventionalItemTags.ORES, "Ores");
		translationBuilder.add(ConventionalItemTags.RAW_MATERIALS, "Raw Materials");
		translationBuilder.add(ConventionalItemTags.RAW_BLOCKS, "Raw Blocks");
		translationBuilder.add(ConventionalItemTags.RAW_MATERIALS_IRON, "Raw Materials Iron");
		translationBuilder.add(ConventionalItemTags.RAW_MATERIALS_GOLD, "Raw Materials Gold");
		translationBuilder.add(ConventionalItemTags.RAW_MATERIALS_COPPER, "Raw Materials Copper");
		translationBuilder.add(ConventionalItemTags.RAW_BLOCKS_IRON, "Raw Blocks Iron");
		translationBuilder.add(ConventionalItemTags.RAW_BLOCKS_GOLD, "Raw Blocks Gold");
		translationBuilder.add(ConventionalItemTags.RAW_BLOCKS_COPPER, "Raw Blocks Copper");
		translationBuilder.add(ConventionalItemTags.INGOTS_IRON, "Ingots Iron");
		translationBuilder.add(ConventionalItemTags.INGOTS_GOLD, "Ingots Gold");
		translationBuilder.add(ConventionalItemTags.INGOTS_COPPER, "Ingots Copper");
		translationBuilder.add(ConventionalItemTags.INGOTS_NETHERITE, "Ingots Netherite");
		translationBuilder.add(ConventionalItemTags.ORES_NETHERITE_SCRAP, "Netherite Scrap Ores");
		translationBuilder.add(ConventionalItemTags.ORES_QUARTZ, "Quartz Ores");
		translationBuilder.add(ConventionalItemTags.GEMS_QUARTZ, "Quartz Gems");
		translationBuilder.add(ConventionalItemTags.GEMS_LAPIS, "Lapis Gems");
		translationBuilder.add(ConventionalItemTags.GEMS_DIAMOND, "Diamond Gems");
		translationBuilder.add(ConventionalItemTags.GEMS_AMETHYST, "Amethyst Gems");
		translationBuilder.add(ConventionalItemTags.GEMS_EMERALD, "Emerald Gems");
		translationBuilder.add(ConventionalItemTags.GEMS_PRISMARINE, "Prismarine Gems");
		translationBuilder.add(ConventionalItemTags.DUSTS_REDSTONE, "Redstone Dusts");
		translationBuilder.add(ConventionalItemTags.DUSTS_GLOWSTONE, "Glowstone Dusts");
		translationBuilder.add(ConventionalItemTags.COAL, "Coal");
		translationBuilder.add(ConventionalItemTags.FOODS, "Foods");
		translationBuilder.add(ConventionalItemTags.POTIONS, "Potions");
		translationBuilder.add(ConventionalItemTags.BUCKETS_WATER, "Water Buckets");
		translationBuilder.add(ConventionalItemTags.BUCKETS_ENTITY_WATER, "Entity Water Buckets");
		translationBuilder.add(ConventionalItemTags.BUCKETS_LAVA, "Lava Buckets");
		translationBuilder.add(ConventionalItemTags.BUCKETS_MILK, "Milk Buckets");
		translationBuilder.add(ConventionalItemTags.BUCKETS_EMPTY, "Empty Buckets");
		translationBuilder.add(ConventionalItemTags.BARRELS, "Barrels");
		translationBuilder.add(ConventionalItemTags.BARRELS_WOODEN, "Wooden Barrels");
		translationBuilder.add(ConventionalItemTags.BOOKSHELVES, "Bookshelves");
		translationBuilder.add(ConventionalItemTags.CHESTS, "Chests");
		translationBuilder.add(ConventionalItemTags.GLASS_BLOCKS, "Glass Blocks");
		translationBuilder.add(ConventionalItemTags.GLASS_PANES, "Glass Panes");
		translationBuilder.add(ConventionalItemTags.SHULKER_BOXES, "Shulker Boxes");
		translationBuilder.add(ConventionalItemTags.BUDDING_BLOCKS, "Budding Blocks");
		translationBuilder.add(ConventionalItemTags.BUDS, "Buds");
		translationBuilder.add(ConventionalItemTags.CLUSTERS, "Clusters");
		translationBuilder.add(ConventionalItemTags.VILLAGER_JOB_SITES, "Villager Job Sites");
		translationBuilder.add(ConventionalItemTags.SANDSTONE_BLOCKS, "Sandstone Blocks");
		translationBuilder.add(ConventionalItemTags.SANDSTONE_SLABS, "Sandstone Slabs");
		translationBuilder.add(ConventionalItemTags.SANDSTONE_STAIRS, "Sandstone Stairs");
		translationBuilder.add(ConventionalItemTags.SANDSTONE_RED_BLOCKS, "Red Sandstone Blocks");
		translationBuilder.add(ConventionalItemTags.SANDSTONE_RED_SLABS, "Red Sandstone Slabs");
		translationBuilder.add(ConventionalItemTags.SANDSTONE_RED_STAIRS, "Red Sandstone Stairs");
		translationBuilder.add(ConventionalItemTags.SANDSTONE_UNCOLORED_BLOCKS, "Uncolored Sandstone Blocks");
		translationBuilder.add(ConventionalItemTags.SANDSTONE_UNCOLORED_SLABS, "Uncolored Sandstone Slabs");
		translationBuilder.add(ConventionalItemTags.SANDSTONE_UNCOLORED_STAIRS, "Uncolored Sandstone Stairs");
		translationBuilder.add(ConventionalItemTags.DYES, "Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_BLACK, "Black Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_BLUE, "Blue Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_BROWN, "Brown Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_CYAN, "Cyan Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_GRAY, "Gray Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_GREEN, "Green Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_LIGHT_BLUE, "Light Blue Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_LIGHT_GRAY, "Light Gray Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_LIME, "Lime Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_MAGENTA, "Magenta Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_ORANGE, "Orange Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_PINK, "Pink Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_PURPLE, "Purple Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_RED, "Red Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_WHITE, "White Dyes");
		translationBuilder.add(ConventionalItemTags.DYES_YELLOW, "Yellow Dyes");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS, "Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_BLACK, "Black Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_BLUE, "Blue Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_BROWN, "Brown Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_CYAN, "Cyan Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_GRAY, "Gray Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_GREEN, "Green Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_LIGHT_BLUE, "Light Blue Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_LIGHT_GRAY, "Light Gray Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_LIME, "Lime Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_MAGENTA, "Magenta Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_ORANGE, "Orange Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_PINK, "Pink Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_PURPLE, "Purple Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_RED, "Red Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_WHITE, "White Dyed Items");
		translationBuilder.add(ConventionalItemTags.DYED_ITEMS_YELLOW, "Yellow Dyed Items");

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
		translationBuilder.add(ConventionalBiomeTags.IS_HOT_OVERWORLD, "Hot Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_HOT, "Hot");
		translationBuilder.add(ConventionalBiomeTags.IS_TEMPERATE_OVERWORLD, "Temperate Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_TEMPERATE, "Temperate");
		translationBuilder.add(ConventionalBiomeTags.IS_COLD_OVERWORLD, "Cold Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_COLD, "Cold");
		translationBuilder.add(ConventionalBiomeTags.IS_WET_OVERWORLD, "Wet Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_WET, "Wet");
		translationBuilder.add(ConventionalBiomeTags.IS_DRY_OVERWORLD, "Dry Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_DRY, "Dry");
		translationBuilder.add(ConventionalBiomeTags.IS_VEGETATION_SPARSE, "Sparse Vegetation");
		translationBuilder.add(ConventionalBiomeTags.IS_VEGETATION_SPARSE_OVERWORLD, "Sparse Overworld Vegetation");
		translationBuilder.add(ConventionalBiomeTags.IS_VEGETATION_DENSE, "Dense Vegetation");
		translationBuilder.add(ConventionalBiomeTags.IS_VEGETATION_DENSE_OVERWORLD, "Dense Overworld Vegetation");
		translationBuilder.add(ConventionalBiomeTags.IS_TREE_CONIFEROUS, "Coniferous Tree");
		translationBuilder.add(ConventionalBiomeTags.IS_TREE_SAVANNA, "Savanna Tree");
		translationBuilder.add(ConventionalBiomeTags.IS_TREE_JUNGLE, "Jungle Tree");
		translationBuilder.add(ConventionalBiomeTags.IS_TREE_DECIDUOUS, "Deciduous Tree");
		translationBuilder.add(ConventionalBiomeTags.IS_VOID, "Void");
		translationBuilder.add(ConventionalBiomeTags.IS_MOUNTAIN_PEAK, "Mountain Peak");
		translationBuilder.add(ConventionalBiomeTags.IS_MOUNTAIN_SLOPE, "Mountain Slope");
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
