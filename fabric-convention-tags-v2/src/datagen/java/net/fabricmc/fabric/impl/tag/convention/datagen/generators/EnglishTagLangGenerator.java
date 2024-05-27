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

import net.minecraft.registry.RegistryWrapper;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEnchantmentTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalStructureTags;

public class EnglishTagLangGenerator extends FabricLanguageProvider {
	public EnglishTagLangGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
		// Blocks
		translationBuilder.add(ConventionalBlockTags.STONES, "Stones");
		translationBuilder.add(ConventionalBlockTags.COBBLESTONES, "Cobblestones");
		translationBuilder.add(ConventionalBlockTags.ORES, "Ores");
		translationBuilder.add(ConventionalBlockTags.QUARTZ_ORES, "Quartz Ores");
		translationBuilder.add(ConventionalBlockTags.NETHERITE_SCRAP_ORES, "Netherite Scrap Ores");
		translationBuilder.add(ConventionalBlockTags.BARRELS, "Barrels");
		translationBuilder.add(ConventionalBlockTags.WOODEN_BARRELS, "Wooden Barrels");
		translationBuilder.add(ConventionalBlockTags.BOOKSHELVES, "Bookshelves");
		translationBuilder.add(ConventionalBlockTags.CHESTS, "Chests");
		translationBuilder.add(ConventionalBlockTags.WOODEN_CHESTS, "Wooden Chests");
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
		translationBuilder.add(ConventionalBlockTags.RED_SANDSTONE_BLOCKS, "Red Sandstone Blocks");
		translationBuilder.add(ConventionalBlockTags.RED_SANDSTONE_SLABS, "Red Sandstone Slabs");
		translationBuilder.add(ConventionalBlockTags.RED_SANDSTONE_STAIRS, "Red Sandstone Stairs");
		translationBuilder.add(ConventionalBlockTags.UNCOLORED_SANDSTONE_BLOCKS, "Uncolored Sandstone Blocks");
		translationBuilder.add(ConventionalBlockTags.UNCOLORED_SANDSTONE_SLABS, "Uncolored Sandstone Slabs");
		translationBuilder.add(ConventionalBlockTags.UNCOLORED_SANDSTONE_STAIRS, "Uncolored Sandstone Stairs");
		translationBuilder.add(ConventionalBlockTags.DYED, "Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.BLACK_DYED, "Black Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.BLUE_DYED, "Blue Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.BROWN_DYED, "Brown Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.CYAN_DYED, "Cyan Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.GRAY_DYED, "Gray Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.GREEN_DYED, "Green Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.LIGHT_BLUE_DYED, "Light Blue Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.LIGHT_GRAY_DYED, "Light Gray Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.LIME_DYED, "Lime Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.MAGENTA_DYED, "Magenta Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.ORANGE_DYED, "Orange Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.PINK_DYED, "Pink Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.PURPLE_DYED, "Purple Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.RED_DYED, "Red Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.WHITE_DYED, "White Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.YELLOW_DYED, "Yellow Dyed Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS, "Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_BONE_MEAL, "Bone Meal Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_COAL, "Coal Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_COPPER, "Copper Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_DIAMOND, "Diamond Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_DRIED_KELP, "Dried Kelp Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_EMERALD, "Emerald Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_GOLD, "Gold Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_IRON, "Iron Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_LAPIS, "Lapis Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_NETHERITE, "Netherite Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_RAW_COPPER, "Raw Copper Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_RAW_GOLD, "Raw Gold Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_RAW_IRON, "Raw Iron Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_REDSTONE, "Redstone Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_SLIME, "Slime Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.STORAGE_BLOCKS_WHEAT, "Wheat Storage Blocks");
		translationBuilder.add(ConventionalBlockTags.PLAYER_WORKSTATIONS_CRAFTING_TABLES, "Crafting Tables");
		translationBuilder.add(ConventionalBlockTags.PLAYER_WORKSTATIONS_FURNACES, "Furnaces");
		translationBuilder.add(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED, "Relocation Not Supported");
		translationBuilder.add(ConventionalBlockTags.SKULLS, "Skulls");
		translationBuilder.add(ConventionalBlockTags.ROPES, "Ropes");
		translationBuilder.add(ConventionalBlockTags.CHAINS, "Chains");
		translationBuilder.add(ConventionalBlockTags.HIDDEN_FROM_RECIPE_VIEWERS, "Hidden From Recipe Viewers");

		// Items
		translationBuilder.add(ConventionalItemTags.STONES, "Stones");
		translationBuilder.add(ConventionalItemTags.COBBLESTONES, "Cobblestones");
		translationBuilder.add(ConventionalItemTags.TOOLS, "Tools");
		translationBuilder.add(ConventionalItemTags.SHEARS_TOOLS, "Shears");
		translationBuilder.add(ConventionalItemTags.SPEARS_TOOLS, "Spears");
		translationBuilder.add(ConventionalItemTags.BOWS_TOOLS, "Bows");
		translationBuilder.add(ConventionalItemTags.CROSSBOWS_TOOLS, "Crossbows");
		translationBuilder.add(ConventionalItemTags.SHIELDS_TOOLS, "Shields");
		translationBuilder.add(ConventionalItemTags.FISHING_RODS_TOOLS, "Fishing Rods");
		translationBuilder.add(ConventionalItemTags.BRUSHES_TOOLS, "Brushes");
		translationBuilder.add(ConventionalItemTags.MELEE_WEAPONS_TOOLS, "Melee Weapons");
		translationBuilder.add(ConventionalItemTags.RANGED_WEAPONS_TOOLS, "Ranged Weapons");
		translationBuilder.add(ConventionalItemTags.MINING_TOOLS, "Mining Tools");
		translationBuilder.add(ConventionalItemTags.ARMORS, "Armors");
		translationBuilder.add(ConventionalItemTags.ENCHANTABLES, "Enchantables");
		translationBuilder.add(ConventionalItemTags.BRICKS, "Bricks");
		translationBuilder.add(ConventionalItemTags.DUSTS, "Dusts");
		translationBuilder.add(ConventionalItemTags.GEMS, "Gems");
		translationBuilder.add(ConventionalItemTags.INGOTS, "Ingots");
		translationBuilder.add(ConventionalItemTags.NUGGETS, "Nuggets");
		translationBuilder.add(ConventionalItemTags.ORES, "Ores");
		translationBuilder.add(ConventionalItemTags.RAW_MATERIALS, "Raw Materials");
		translationBuilder.add(ConventionalItemTags.IRON_RAW_MATERIALS, "Raw Iron Materials");
		translationBuilder.add(ConventionalItemTags.GOLD_RAW_MATERIALS, "Raw Gold Materials");
		translationBuilder.add(ConventionalItemTags.COPPER_RAW_MATERIALS, "Raw Copper Materials");
		translationBuilder.add(ConventionalItemTags.NORMAL_BRICKS, "Bricks");
		translationBuilder.add(ConventionalItemTags.NETHER_BRICKS, "Nether Bricks");
		translationBuilder.add(ConventionalItemTags.IRON_INGOTS, "Iron Ingots");
		translationBuilder.add(ConventionalItemTags.GOLD_INGOTS, "Gold Ingots");
		translationBuilder.add(ConventionalItemTags.COPPER_INGOTS, "Copper Ingots");
		translationBuilder.add(ConventionalItemTags.NETHERITE_INGOTS, "Netherite Ingots");
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
		translationBuilder.add(ConventionalItemTags.POTIONS, "Potions");
		translationBuilder.add(ConventionalItemTags.FOODS, "Foods");
		translationBuilder.add(ConventionalItemTags.FRUITS_FOODS, "Fruits");
		translationBuilder.add(ConventionalItemTags.VEGETABLES_FOODS, "Vegetables");
		translationBuilder.add(ConventionalItemTags.BERRIES_FOODS, "Berries");
		translationBuilder.add(ConventionalItemTags.BREADS_FOODS, "Breads");
		translationBuilder.add(ConventionalItemTags.COOKIES_FOODS, "Cookies");
		translationBuilder.add(ConventionalItemTags.RAW_MEATS_FOODS, "Raw Meats");
		translationBuilder.add(ConventionalItemTags.COOKED_MEATS_FOODS, "Cooked Meats");
		translationBuilder.add(ConventionalItemTags.RAW_FISHES_FOODS, "Raw Fishes");
		translationBuilder.add(ConventionalItemTags.COOKED_FISHES_FOODS, "Cooked Fishes");
		translationBuilder.add(ConventionalItemTags.SOUPS_FOODS, "Soups");
		translationBuilder.add(ConventionalItemTags.CANDIES_FOODS, "Candies");
		translationBuilder.add(ConventionalItemTags.EDIBLE_WHEN_PLACED_FOODS, "Edible When Placed");
		translationBuilder.add(ConventionalItemTags.FOOD_POISONING_FOODS, "Food Poisoning");
		translationBuilder.add(ConventionalItemTags.BUCKETS, "Buckets");
		translationBuilder.add(ConventionalItemTags.WATER_BUCKETS, "Water Buckets");
		translationBuilder.add(ConventionalItemTags.ENTITY_WATER_BUCKETS, "Entity Water Buckets");
		translationBuilder.add(ConventionalItemTags.LAVA_BUCKETS, "Lava Buckets");
		translationBuilder.add(ConventionalItemTags.MILK_BUCKETS, "Milk Buckets");
		translationBuilder.add(ConventionalItemTags.POWDER_SNOW_BUCKETS, "Powder Snow Buckets");
		translationBuilder.add(ConventionalItemTags.EMPTY_BUCKETS, "Empty Buckets");
		translationBuilder.add(ConventionalItemTags.BARRELS, "Barrels");
		translationBuilder.add(ConventionalItemTags.WOODEN_BARRELS, "Wooden Barrels");
		translationBuilder.add(ConventionalItemTags.BOOKSHELVES, "Bookshelves");
		translationBuilder.add(ConventionalItemTags.CHESTS, "Chests");
		translationBuilder.add(ConventionalItemTags.WOODEN_CHESTS, "Wooden Chests");
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
		translationBuilder.add(ConventionalItemTags.RED_SANDSTONE_BLOCKS, "Red Sandstone Blocks");
		translationBuilder.add(ConventionalItemTags.RED_SANDSTONE_SLABS, "Red Sandstone Slabs");
		translationBuilder.add(ConventionalItemTags.RED_SANDSTONE_STAIRS, "Red Sandstone Stairs");
		translationBuilder.add(ConventionalItemTags.UNCOLORED_SANDSTONE_BLOCKS, "Uncolored Sandstone Blocks");
		translationBuilder.add(ConventionalItemTags.UNCOLORED_SANDSTONE_SLABS, "Uncolored Sandstone Slabs");
		translationBuilder.add(ConventionalItemTags.UNCOLORED_SANDSTONE_STAIRS, "Uncolored Sandstone Stairs");
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
		translationBuilder.add(ConventionalItemTags.DYED, "Dyed Items");
		translationBuilder.add(ConventionalItemTags.BLACK_DYED, "Black Dyed Items");
		translationBuilder.add(ConventionalItemTags.BLUE_DYED, "Blue Dyed Items");
		translationBuilder.add(ConventionalItemTags.BROWN_DYED, "Brown Dyed Items");
		translationBuilder.add(ConventionalItemTags.CYAN_DYED, "Cyan Dyed Items");
		translationBuilder.add(ConventionalItemTags.GRAY_DYED, "Gray Dyed Items");
		translationBuilder.add(ConventionalItemTags.GREEN_DYED, "Green Dyed Items");
		translationBuilder.add(ConventionalItemTags.LIGHT_BLUE_DYED, "Light Blue Dyed Items");
		translationBuilder.add(ConventionalItemTags.LIGHT_GRAY_DYED, "Light Gray Dyed Items");
		translationBuilder.add(ConventionalItemTags.LIME_DYED, "Lime Dyed Items");
		translationBuilder.add(ConventionalItemTags.MAGENTA_DYED, "Magenta Dyed Items");
		translationBuilder.add(ConventionalItemTags.ORANGE_DYED, "Orange Dyed Items");
		translationBuilder.add(ConventionalItemTags.PINK_DYED, "Pink Dyed Items");
		translationBuilder.add(ConventionalItemTags.PURPLE_DYED, "Purple Dyed Items");
		translationBuilder.add(ConventionalItemTags.RED_DYED, "Red Dyed Items");
		translationBuilder.add(ConventionalItemTags.WHITE_DYED, "White Dyed Items");
		translationBuilder.add(ConventionalItemTags.YELLOW_DYED, "Yellow Dyed Items");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS, "Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_BONE_MEAL, "Bone Meal Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_COAL, "Coal Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_COPPER, "Copper Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_DIAMOND, "Diamond Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_DRIED_KELP, "Dried Kelp Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_EMERALD, "Emerald Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_GOLD, "Gold Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_IRON, "Iron Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_LAPIS, "Lapis Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_NETHERITE, "Netherite Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_RAW_COPPER, "Raw Copper Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_RAW_GOLD, "Raw Gold Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_RAW_IRON, "Raw Iron Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_REDSTONE, "Redstone Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_SLIME, "Slime Storage Blocks");
		translationBuilder.add(ConventionalItemTags.STORAGE_BLOCKS_WHEAT, "Wheat Storage Blocks");
		translationBuilder.add(ConventionalItemTags.PLAYER_WORKSTATIONS_CRAFTING_TABLES, "Crafting Tables");
		translationBuilder.add(ConventionalItemTags.PLAYER_WORKSTATIONS_FURNACES, "Furnaces");
		translationBuilder.add(ConventionalItemTags.STRINGS, "Strings");
		translationBuilder.add(ConventionalItemTags.MUSIC_DISCS, "Music Discs");
		translationBuilder.add(ConventionalItemTags.RODS, "Rods");
		translationBuilder.add(ConventionalItemTags.WOODEN_RODS, "Wooden Rods");
		translationBuilder.add(ConventionalItemTags.BLAZE_RODS, "Blaze Rods");
		translationBuilder.add(ConventionalItemTags.BREEZE_RODS, "Breeze Rods");
		translationBuilder.add(ConventionalItemTags.ROPES, "Ropes");
		translationBuilder.add(ConventionalItemTags.CHAINS, "Chains");
		translationBuilder.add(ConventionalItemTags.HIDDEN_FROM_RECIPE_VIEWERS, "Hidden From Recipe Viewers");

		// Enchantments
		translationBuilder.add(ConventionalEnchantmentTags.INCREASE_BLOCK_DROPS, "Increases Block Drops");
		translationBuilder.add(ConventionalEnchantmentTags.INCREASE_ENTITY_DROPS, "Increases Entity Drops");
		translationBuilder.add(ConventionalEnchantmentTags.WEAPON_DAMAGE_ENHANCEMENTS, "Weapon Damage Enhancements");
		translationBuilder.add(ConventionalEnchantmentTags.ENTITY_SPEED_ENHANCEMENTS, "Entity Speed Enhancements");
		translationBuilder.add(ConventionalEnchantmentTags.ENTITY_AUXILIARY_MOVEMENT_ENHANCEMENTS, "Entity Auxiliary Movement Enhancements");
		translationBuilder.add(ConventionalEnchantmentTags.ENTITY_DEFENSE_ENHANCEMENTS, "Entity Defense Enhancements");

		// Entity Types
		translationBuilder.add(ConventionalEntityTypeTags.BOSSES, "Bosses");
		translationBuilder.add(ConventionalEntityTypeTags.MINECARTS, "Minecarts");
		translationBuilder.add(ConventionalEntityTypeTags.BOATS, "Boats");
		translationBuilder.add(ConventionalEntityTypeTags.CAPTURING_NOT_SUPPORTED, "Capturing Not Supported");
		translationBuilder.add(ConventionalEntityTypeTags.TELEPORTING_NOT_SUPPORTED, "Teleporting Not Supported");

		// Fluids
		translationBuilder.add(ConventionalFluidTags.LAVA, "Lava");
		translationBuilder.add(ConventionalFluidTags.WATER, "Water");
		translationBuilder.add(ConventionalFluidTags.MILK, "Milk");
		translationBuilder.add(ConventionalFluidTags.HONEY, "Honey");
		translationBuilder.add(ConventionalFluidTags.HIDDEN_FROM_RECIPE_VIEWERS, "Hidden From Recipe Viewers");

		// Structures
		translationBuilder.add(ConventionalStructureTags.HIDDEN_FROM_DISPLAYERS, "Hidden From Displayers");
		translationBuilder.add(ConventionalStructureTags.HIDDEN_FROM_LOCATOR_SELECTION, "Hidden From Locator Selection");

		// Biomes
		translationBuilder.add(ConventionalBiomeTags.NO_DEFAULT_MONSTERS, "No Default Monsters");
		translationBuilder.add(ConventionalBiomeTags.HIDDEN_FROM_LOCATOR_SELECTION, "Hidden From Locator Selection");
		translationBuilder.add(ConventionalBiomeTags.IS_VOID, "Void");
		translationBuilder.add(ConventionalBiomeTags.IS_OVERWORLD, "Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_HOT, "Hot");
		translationBuilder.add(ConventionalBiomeTags.IS_HOT_OVERWORLD, "Hot Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_HOT_NETHER, "Hot Nether");
		translationBuilder.add(ConventionalBiomeTags.IS_TEMPERATE, "Temperate");
		translationBuilder.add(ConventionalBiomeTags.IS_TEMPERATE_OVERWORLD, "Temperate Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_COLD, "Cold");
		translationBuilder.add(ConventionalBiomeTags.IS_COLD_OVERWORLD, "Cold Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_COLD_END, "Cold End");
		translationBuilder.add(ConventionalBiomeTags.IS_WET, "Wet");
		translationBuilder.add(ConventionalBiomeTags.IS_WET_OVERWORLD, "Wet Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_DRY, "Dry");
		translationBuilder.add(ConventionalBiomeTags.IS_DRY_OVERWORLD, "Dry Overworld");
		translationBuilder.add(ConventionalBiomeTags.IS_DRY_NETHER, "Dry Nether");
		translationBuilder.add(ConventionalBiomeTags.IS_DRY_END, "Dry End");
		translationBuilder.add(ConventionalBiomeTags.IS_VEGETATION_SPARSE, "Sparse Vegetation");
		translationBuilder.add(ConventionalBiomeTags.IS_VEGETATION_SPARSE_OVERWORLD, "Sparse Overworld Vegetation");
		translationBuilder.add(ConventionalBiomeTags.IS_VEGETATION_DENSE, "Dense Vegetation");
		translationBuilder.add(ConventionalBiomeTags.IS_VEGETATION_DENSE_OVERWORLD, "Dense Overworld Vegetation");
		translationBuilder.add(ConventionalBiomeTags.IS_CONIFEROUS_TREE, "Coniferous Tree");
		translationBuilder.add(ConventionalBiomeTags.IS_SAVANNA_TREE, "Savanna Tree");
		translationBuilder.add(ConventionalBiomeTags.IS_JUNGLE_TREE, "Jungle Tree");
		translationBuilder.add(ConventionalBiomeTags.IS_DECIDUOUS_TREE, "Deciduous Tree");
		translationBuilder.add(ConventionalBiomeTags.IS_MOUNTAIN, "Mountain");
		translationBuilder.add(ConventionalBiomeTags.IS_MOUNTAIN_PEAK, "Mountain Peak");
		translationBuilder.add(ConventionalBiomeTags.IS_MOUNTAIN_SLOPE, "Mountain Slope");
		translationBuilder.add(ConventionalBiomeTags.IS_PLAINS, "Plains");
		translationBuilder.add(ConventionalBiomeTags.IS_SNOWY_PLAINS, "Snowy Plains");
		translationBuilder.add(ConventionalBiomeTags.IS_FOREST, "Forest");
		translationBuilder.add(ConventionalBiomeTags.IS_BIRCH_FOREST, "Birch Forest");
		translationBuilder.add(ConventionalBiomeTags.IS_FLOWER_FOREST, "Flower Forest");
		translationBuilder.add(ConventionalBiomeTags.IS_TAIGA, "Taiga");
		translationBuilder.add(ConventionalBiomeTags.IS_OLD_GROWTH, "Old Growth");
		translationBuilder.add(ConventionalBiomeTags.IS_HILL, "Hills");
		translationBuilder.add(ConventionalBiomeTags.IS_WINDSWEPT, "Windswept");
		translationBuilder.add(ConventionalBiomeTags.IS_JUNGLE, "Jungle");
		translationBuilder.add(ConventionalBiomeTags.IS_SAVANNA, "Savanna");
		translationBuilder.add(ConventionalBiomeTags.IS_SWAMP, "Swamp");
		translationBuilder.add(ConventionalBiomeTags.IS_DESERT, "Desert");
		translationBuilder.add(ConventionalBiomeTags.IS_BADLANDS, "Badlands");
		translationBuilder.add(ConventionalBiomeTags.IS_BEACH, "Beach");
		translationBuilder.add(ConventionalBiomeTags.IS_STONY_SHORES, "Stony Shores");
		translationBuilder.add(ConventionalBiomeTags.IS_MUSHROOM, "Mushroom");
		translationBuilder.add(ConventionalBiomeTags.IS_RIVER, "River");
		translationBuilder.add(ConventionalBiomeTags.IS_OCEAN, "Ocean");
		translationBuilder.add(ConventionalBiomeTags.IS_DEEP_OCEAN, "Deep Ocean");
		translationBuilder.add(ConventionalBiomeTags.IS_SHALLOW_OCEAN, "Shallow Ocean");
		translationBuilder.add(ConventionalBiomeTags.IS_UNDERGROUND, "Underground");
		translationBuilder.add(ConventionalBiomeTags.IS_CAVE, "Cave");
		translationBuilder.add(ConventionalBiomeTags.IS_WASTELAND, "Wasteland");
		translationBuilder.add(ConventionalBiomeTags.IS_DEAD, "Dead");
		translationBuilder.add(ConventionalBiomeTags.IS_FLORAL, "Floral");
		translationBuilder.add(ConventionalBiomeTags.IS_SNOWY, "Snowy");
		translationBuilder.add(ConventionalBiomeTags.IS_ICY, "Icy");
		translationBuilder.add(ConventionalBiomeTags.IS_AQUATIC, "Aquatic");
		translationBuilder.add(ConventionalBiomeTags.IS_AQUATIC_ICY, "Icy Aquatic");
		translationBuilder.add(ConventionalBiomeTags.IS_NETHER, "Nether");
		translationBuilder.add(ConventionalBiomeTags.IS_NETHER_FOREST, "Nether Forest");
		translationBuilder.add(ConventionalBiomeTags.IS_END, "The End");
		translationBuilder.add(ConventionalBiomeTags.IS_OUTER_END_ISLAND, "Outer End Island");
	}
}
