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

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;

public final class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
	public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, FabricTagProvider.BlockTagProvider blockTags) {
		super(output, completableFuture, blockTags);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup arg) {
		generateToolTags();
		generateBucketTags();
		generateOreAndRelatedTags();
		generateConsumableTags();
		generateFoodTags();
		generateDyeTags();
		generateDyedTags();
		generateVillagerJobSites();
		generateOtherTags();
		copyItemTags();
		generateBackwardsCompatTags();
	}

	private void copyItemTags() {
		copy(ConventionalBlockTags.STONES, ConventionalItemTags.STONES);
		copy(ConventionalBlockTags.COBBLESTONES, ConventionalItemTags.COBBLESTONES);
		copy(ConventionalBlockTags.BARRELS, ConventionalItemTags.BARRELS);
		copy(ConventionalBlockTags.WOODEN_BARRELS, ConventionalItemTags.WOODEN_BARRELS);
		copy(ConventionalBlockTags.BOOKSHELVES, ConventionalItemTags.BOOKSHELVES);
		copy(ConventionalBlockTags.CHESTS, ConventionalItemTags.CHESTS);
		copy(ConventionalBlockTags.WOODEN_CHESTS, ConventionalItemTags.WOODEN_CHESTS);
		copy(ConventionalBlockTags.GLASS_BLOCKS, ConventionalItemTags.GLASS_BLOCKS);
		copy(ConventionalBlockTags.GLASS_PANES, ConventionalItemTags.GLASS_PANES);
		copy(ConventionalBlockTags.SHULKER_BOXES, ConventionalItemTags.SHULKER_BOXES);

		copy(ConventionalBlockTags.BUDDING_BLOCKS, ConventionalItemTags.BUDDING_BLOCKS);
		copy(ConventionalBlockTags.BUDS, ConventionalItemTags.BUDS);
		copy(ConventionalBlockTags.CLUSTERS, ConventionalItemTags.CLUSTERS);

		copy(ConventionalBlockTags.SANDSTONE_BLOCKS, ConventionalItemTags.SANDSTONE_BLOCKS);
		copy(ConventionalBlockTags.SANDSTONE_SLABS, ConventionalItemTags.SANDSTONE_SLABS);
		copy(ConventionalBlockTags.SANDSTONE_STAIRS, ConventionalItemTags.SANDSTONE_STAIRS);
		copy(ConventionalBlockTags.RED_SANDSTONE_BLOCKS, ConventionalItemTags.RED_SANDSTONE_BLOCKS);
		copy(ConventionalBlockTags.RED_SANDSTONE_SLABS, ConventionalItemTags.RED_SANDSTONE_SLABS);
		copy(ConventionalBlockTags.RED_SANDSTONE_STAIRS, ConventionalItemTags.RED_SANDSTONE_STAIRS);
		copy(ConventionalBlockTags.UNCOLORED_SANDSTONE_BLOCKS, ConventionalItemTags.UNCOLORED_SANDSTONE_BLOCKS);
		copy(ConventionalBlockTags.UNCOLORED_SANDSTONE_SLABS, ConventionalItemTags.UNCOLORED_SANDSTONE_SLABS);
		copy(ConventionalBlockTags.UNCOLORED_SANDSTONE_STAIRS, ConventionalItemTags.UNCOLORED_SANDSTONE_STAIRS);

		copy(ConventionalBlockTags.STORAGE_BLOCKS, ConventionalItemTags.STORAGE_BLOCKS);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_BONE_MEAL, ConventionalItemTags.STORAGE_BLOCKS_BONE_MEAL);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_COAL, ConventionalItemTags.STORAGE_BLOCKS_COAL);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_COPPER, ConventionalItemTags.STORAGE_BLOCKS_COPPER);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_DIAMOND, ConventionalItemTags.STORAGE_BLOCKS_DIAMOND);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_DRIED_KELP, ConventionalItemTags.STORAGE_BLOCKS_DRIED_KELP);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_EMERALD, ConventionalItemTags.STORAGE_BLOCKS_EMERALD);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_GOLD, ConventionalItemTags.STORAGE_BLOCKS_GOLD);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_IRON, ConventionalItemTags.STORAGE_BLOCKS_IRON);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_LAPIS, ConventionalItemTags.STORAGE_BLOCKS_LAPIS);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_NETHERITE, ConventionalItemTags.STORAGE_BLOCKS_NETHERITE);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_RAW_COPPER, ConventionalItemTags.STORAGE_BLOCKS_RAW_COPPER);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_RAW_GOLD, ConventionalItemTags.STORAGE_BLOCKS_RAW_GOLD);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_RAW_IRON, ConventionalItemTags.STORAGE_BLOCKS_RAW_IRON);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_REDSTONE, ConventionalItemTags.STORAGE_BLOCKS_REDSTONE);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_SLIME, ConventionalItemTags.STORAGE_BLOCKS_SLIME);
		copy(ConventionalBlockTags.STORAGE_BLOCKS_WHEAT, ConventionalItemTags.STORAGE_BLOCKS_WHEAT);
	}

	private void generateDyeTags() {
		getOrCreateTagBuilder(ConventionalItemTags.DYES)
				.addOptionalTag(ConventionalItemTags.WHITE_DYES)
				.addOptionalTag(ConventionalItemTags.ORANGE_DYES)
				.addOptionalTag(ConventionalItemTags.MAGENTA_DYES)
				.addOptionalTag(ConventionalItemTags.LIGHT_BLUE_DYES)
				.addOptionalTag(ConventionalItemTags.YELLOW_DYES)
				.addOptionalTag(ConventionalItemTags.LIME_DYES)
				.addOptionalTag(ConventionalItemTags.PINK_DYES)
				.addOptionalTag(ConventionalItemTags.GRAY_DYES)
				.addOptionalTag(ConventionalItemTags.LIGHT_GRAY_DYES)
				.addOptionalTag(ConventionalItemTags.CYAN_DYES)
				.addOptionalTag(ConventionalItemTags.PURPLE_DYES)
				.addOptionalTag(ConventionalItemTags.BLUE_DYES)
				.addOptionalTag(ConventionalItemTags.BROWN_DYES)
				.addOptionalTag(ConventionalItemTags.GREEN_DYES)
				.addOptionalTag(ConventionalItemTags.RED_DYES)
				.addOptionalTag(ConventionalItemTags.BLACK_DYES);
		getOrCreateTagBuilder(ConventionalItemTags.BLACK_DYES)
				.add(Items.BLACK_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.BLUE_DYES)
				.add(Items.BLUE_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.BROWN_DYES)
				.add(Items.BROWN_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.GREEN_DYES)
				.add(Items.GREEN_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.RED_DYES)
				.add(Items.RED_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.WHITE_DYES)
				.add(Items.WHITE_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.YELLOW_DYES)
				.add(Items.YELLOW_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.LIGHT_BLUE_DYES)
				.add(Items.LIGHT_BLUE_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.LIGHT_GRAY_DYES)
				.add(Items.LIGHT_GRAY_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.LIME_DYES)
				.add(Items.LIME_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.MAGENTA_DYES)
				.add(Items.MAGENTA_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.ORANGE_DYES)
				.add(Items.ORANGE_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.PINK_DYES)
				.add(Items.PINK_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.CYAN_DYES)
				.add(Items.CYAN_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.GRAY_DYES)
				.add(Items.GRAY_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.PURPLE_DYES)
				.add(Items.PURPLE_DYE);
	}

	private void generateConsumableTags() {
		getOrCreateTagBuilder(ConventionalItemTags.POTIONS)
				.add(Items.POTION)
				.add(Items.SPLASH_POTION)
				.add(Items.LINGERING_POTION);
	}

	private void generateFoodTags() {
		getOrCreateTagBuilder(ConventionalItemTags.FRUITS_FOODS)
				.add(Items.APPLE)
				.add(Items.GOLDEN_APPLE)
				.add(Items.ENCHANTED_GOLDEN_APPLE);

		getOrCreateTagBuilder(ConventionalItemTags.VEGETABLES_FOODS)
				.add(Items.CARROT)
				.add(Items.GOLDEN_CARROT)
				.add(Items.POTATO)
				.add(Items.MELON_SLICE)
				.add(Items.BEETROOT);

		getOrCreateTagBuilder(ConventionalItemTags.BERRIES_FOODS)
				.add(Items.SWEET_BERRIES)
				.add(Items.GLOW_BERRIES);

		getOrCreateTagBuilder(ConventionalItemTags.BREADS_FOODS)
				.add(Items.BREAD);

		getOrCreateTagBuilder(ConventionalItemTags.COOKIES_FOODS)
				.add(Items.COOKIE);

		getOrCreateTagBuilder(ConventionalItemTags.RAW_MEATS_FOODS)
				.add(Items.BEEF)
				.add(Items.CHICKEN)
				.add(Items.RABBIT)
				.add(Items.MUTTON);

		getOrCreateTagBuilder(ConventionalItemTags.RAW_FISHES_FOODS)
				.add(Items.COD)
				.add(Items.SALMON)
				.add(Items.TROPICAL_FISH)
				.add(Items.PUFFERFISH);

		getOrCreateTagBuilder(ConventionalItemTags.COOKED_MEATS_FOODS)
				.add(Items.COOKED_BEEF)
				.add(Items.COOKED_CHICKEN)
				.add(Items.COOKED_RABBIT)
				.add(Items.COOKED_MUTTON);

		getOrCreateTagBuilder(ConventionalItemTags.COOKED_FISHES_FOODS)
				.add(Items.COOKED_COD)
				.add(Items.COOKED_SALMON);

		getOrCreateTagBuilder(ConventionalItemTags.SOUPS_FOODS)
				.add(Items.BEETROOT_SOUP)
				.add(Items.MUSHROOM_STEW)
				.add(Items.RABBIT_STEW)
				.add(Items.SUSPICIOUS_STEW);

		getOrCreateTagBuilder(ConventionalItemTags.CANDIES_FOODS);

		getOrCreateTagBuilder(ConventionalItemTags.EDIBLE_WHEN_PLACED_FOODS)
				.add(Items.CAKE);

		getOrCreateTagBuilder(ConventionalItemTags.FOOD_POISONING_FOODS)
				.add(Items.POISONOUS_POTATO)
				.add(Items.ROTTEN_FLESH)
				.add(Items.SPIDER_EYE)
				.add(Items.PUFFERFISH);

		getOrCreateTagBuilder(ConventionalItemTags.FOODS)
				.add(Items.BAKED_POTATO)
				.add(Items.PUMPKIN_PIE)
				.add(Items.HONEY_BOTTLE)
				.add(Items.DRIED_KELP)
				.addOptionalTag(ConventionalItemTags.FRUITS_FOODS)
				.addOptionalTag(ConventionalItemTags.VEGETABLES_FOODS)
				.addOptionalTag(ConventionalItemTags.BERRIES_FOODS)
				.addOptionalTag(ConventionalItemTags.BREADS_FOODS)
				.addOptionalTag(ConventionalItemTags.COOKIES_FOODS)
				.addOptionalTag(ConventionalItemTags.RAW_MEATS_FOODS)
				.addOptionalTag(ConventionalItemTags.RAW_FISHES_FOODS)
				.addOptionalTag(ConventionalItemTags.COOKED_MEATS_FOODS)
				.addOptionalTag(ConventionalItemTags.COOKED_FISHES_FOODS)
				.addOptionalTag(ConventionalItemTags.SOUPS_FOODS)
				.addOptionalTag(ConventionalItemTags.CANDIES_FOODS)
				.addOptionalTag(ConventionalItemTags.EDIBLE_WHEN_PLACED_FOODS)
				.addOptionalTag(ConventionalItemTags.FOOD_POISONING_FOODS);
	}

	private void generateBucketTags() {
		getOrCreateTagBuilder(ConventionalItemTags.EMPTY_BUCKETS)
				.add(Items.BUCKET);
		getOrCreateTagBuilder(ConventionalItemTags.LAVA_BUCKETS)
				.add(Items.LAVA_BUCKET);
		getOrCreateTagBuilder(ConventionalItemTags.ENTITY_WATER_BUCKETS)
				.add(Items.AXOLOTL_BUCKET)
				.add(Items.COD_BUCKET)
				.add(Items.PUFFERFISH_BUCKET)
				.add(Items.TADPOLE_BUCKET)
				.add(Items.TROPICAL_FISH_BUCKET)
				.add(Items.SALMON_BUCKET);
		getOrCreateTagBuilder(ConventionalItemTags.WATER_BUCKETS)
				.add(Items.WATER_BUCKET);
		getOrCreateTagBuilder(ConventionalItemTags.MILK_BUCKETS)
				.add(Items.MILK_BUCKET);
	}

	private void generateOreAndRelatedTags() {
		// Categories
		getOrCreateTagBuilder(ConventionalItemTags.DUSTS)
				.add(Items.GLOWSTONE_DUST)
				.add(Items.REDSTONE);
		getOrCreateTagBuilder(ConventionalItemTags.GEMS)
				.add(Items.AMETHYST_SHARD, Items.DIAMOND, Items.EMERALD, Items.LAPIS_LAZULI);
		getOrCreateTagBuilder(ConventionalItemTags.INGOTS)
				.add(Items.COPPER_INGOT, Items.GOLD_INGOT, Items.IRON_INGOT, Items.NETHERITE_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.NUGGETS)
				.add(Items.GOLD_NUGGET, Items.IRON_NUGGET);
		copy(ConventionalBlockTags.ORES, ConventionalItemTags.ORES);
		getOrCreateTagBuilder(ConventionalItemTags.ORES)
				.addOptionalTag(ConventionalItemTags.NETHERITE_SCRAP_ORES)
				.addOptionalTag(ConventionalItemTags.QUARTZ_ORES);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS)
				.addOptionalTag(ConventionalItemTags.COPPER_RAW_MATERIALS)
				.addOptionalTag(ConventionalItemTags.GOLD_RAW_MATERIALS)
				.addOptionalTag(ConventionalItemTags.IRON_RAW_MATERIALS);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS)
				.add(Items.RAW_COPPER, Items.RAW_GOLD, Items.RAW_IRON);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_BLOCKS)
				.add(Items.RAW_COPPER_BLOCK, Items.RAW_GOLD_BLOCK, Items.RAW_IRON_BLOCK);

		// Vanilla instances
		getOrCreateTagBuilder(ConventionalItemTags.IRON_INGOTS)
				.add(Items.IRON_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.COPPER_INGOTS)
				.add(Items.COPPER_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.GOLD_INGOTS)
				.add(Items.GOLD_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.NETHERITE_INGOTS)
				.add(Items.NETHERITE_INGOT);

		getOrCreateTagBuilder(ConventionalItemTags.IRON_RAW_BLOCKS)
				.add(Items.RAW_IRON_BLOCK);
		getOrCreateTagBuilder(ConventionalItemTags.COPPER_RAW_BLOCKS)
				.add(Items.RAW_COPPER_BLOCK);
		getOrCreateTagBuilder(ConventionalItemTags.GOLD_RAW_BLOCKS)
				.add(Items.RAW_GOLD_BLOCK);

		getOrCreateTagBuilder(ConventionalItemTags.IRON_RAW_MATERIALS)
				.add(Items.RAW_IRON);
		getOrCreateTagBuilder(ConventionalItemTags.COPPER_RAW_MATERIALS)
				.add(Items.RAW_COPPER);
		getOrCreateTagBuilder(ConventionalItemTags.GOLD_RAW_MATERIALS)
				.add(Items.RAW_GOLD);

		getOrCreateTagBuilder(ConventionalItemTags.REDSTONE_DUSTS)
				.add(Items.REDSTONE);
		getOrCreateTagBuilder(ConventionalItemTags.GLOWSTONE_DUSTS)
				.add(Items.GLOWSTONE_DUST);
		getOrCreateTagBuilder(ConventionalItemTags.COAL)
				.addOptionalTag(ItemTags.COALS);

		getOrCreateTagBuilder(ConventionalItemTags.QUARTZ_ORES)
				.add(Items.NETHER_QUARTZ_ORE);
		getOrCreateTagBuilder(ConventionalItemTags.NETHERITE_SCRAP_ORES)
				.add(Items.ANCIENT_DEBRIS);

		getOrCreateTagBuilder(ConventionalItemTags.QUARTZ_GEMS)
				.add(Items.QUARTZ);
		getOrCreateTagBuilder(ConventionalItemTags.EMERALD_GEMS)
				.add(Items.EMERALD);
		getOrCreateTagBuilder(ConventionalItemTags.LAPIS_GEMS)
				.add(Items.LAPIS_LAZULI);
		getOrCreateTagBuilder(ConventionalItemTags.DIAMOND_GEMS)
				.add(Items.DIAMOND);
		getOrCreateTagBuilder(ConventionalItemTags.AMETHYST_GEMS)
				.add(Items.AMETHYST_SHARD);
		getOrCreateTagBuilder(ConventionalItemTags.PRISMARINE_GEMS)
				.add(Items.PRISMARINE_CRYSTALS);
	}

	private void generateToolTags() {
		getOrCreateTagBuilder(ConventionalItemTags.TOOLS)
				.addOptionalTag(ItemTags.AXES)
				.addOptionalTag(ItemTags.HOES)
				.addOptionalTag(ItemTags.PICKAXES)
				.addOptionalTag(ItemTags.SHOVELS)
				.addOptionalTag(ItemTags.SWORDS)
				.addOptionalTag(ConventionalItemTags.BOWS_TOOLS)
				.addOptionalTag(ConventionalItemTags.BRUSHES_TOOLS)
				.addOptionalTag(ConventionalItemTags.CROSSBOWS_TOOLS)
				.addOptionalTag(ConventionalItemTags.FISHING_RODS_TOOLS)
				.addOptionalTag(ConventionalItemTags.SHEARS_TOOLS)
				.addOptionalTag(ConventionalItemTags.SHIELDS_TOOLS)
				.addOptionalTag(ConventionalItemTags.SPEARS_TOOLS)
				.addOptionalTag(ConventionalItemTags.MINING_TOOLS)
				.addOptionalTag(ConventionalItemTags.MELEE_WEAPONS_TOOLS)
				.addOptionalTag(ConventionalItemTags.RANGED_WEAPONS_TOOLS);

		getOrCreateTagBuilder(ConventionalItemTags.BOWS_TOOLS)
				.add(Items.BOW);
		getOrCreateTagBuilder(ConventionalItemTags.CROSSBOWS_TOOLS)
				.add(Items.CROSSBOW);
		getOrCreateTagBuilder(ConventionalItemTags.SHEARS_TOOLS)
				.add(Items.SHEARS);
		getOrCreateTagBuilder(ConventionalItemTags.SHIELDS_TOOLS)
				.add(Items.SHIELD);
		getOrCreateTagBuilder(ConventionalItemTags.SPEARS_TOOLS)
				.add(Items.TRIDENT);
		getOrCreateTagBuilder(ConventionalItemTags.FISHING_RODS_TOOLS)
				.add(Items.FISHING_ROD);
		getOrCreateTagBuilder(ConventionalItemTags.BRUSHES_TOOLS)
				.add(Items.BRUSH);

		getOrCreateTagBuilder(ConventionalItemTags.MINING_TOOLS)
				.add(Items.WOODEN_PICKAXE)
				.add(Items.STONE_PICKAXE)
				.add(Items.GOLDEN_PICKAXE)
				.add(Items.IRON_PICKAXE)
				.add(Items.DIAMOND_PICKAXE)
				.add(Items.NETHERITE_PICKAXE);

		getOrCreateTagBuilder(ConventionalItemTags.MELEE_WEAPONS_TOOLS)
				.add(Items.WOODEN_SWORD)
				.add(Items.STONE_SWORD)
				.add(Items.GOLDEN_SWORD)
				.add(Items.IRON_SWORD)
				.add(Items.DIAMOND_SWORD)
				.add(Items.NETHERITE_SWORD)
				.add(Items.WOODEN_AXE)
				.add(Items.STONE_AXE)
				.add(Items.GOLDEN_AXE)
				.add(Items.IRON_AXE)
				.add(Items.DIAMOND_AXE)
				.add(Items.NETHERITE_AXE);

		getOrCreateTagBuilder(ConventionalItemTags.RANGED_WEAPONS_TOOLS)
				.add(Items.BOW)
				.add(Items.CROSSBOW)
				.add(Items.TRIDENT);
	}

	private void generateVillagerJobSites() {
		BlockTagGenerator.VILLAGER_JOB_SITE_BLOCKS.stream()
				.map(ItemConvertible::asItem)
				.distinct() // cauldron blocks have the same item
				.forEach(getOrCreateTagBuilder(ConventionalItemTags.VILLAGER_JOB_SITES)::add);
	}

	private void generateOtherTags() {
		getOrCreateTagBuilder(ConventionalItemTags.STRINGS)
				.add(Items.STRING);

		getOrCreateTagBuilder(ConventionalItemTags.WOODEN_RODS)
				.add(Items.STICK);

		getOrCreateTagBuilder(ConventionalItemTags.BLAZE_RODS)
				.add(Items.BLAZE_ROD);

		getOrCreateTagBuilder(ConventionalItemTags.RODS)
				.addOptionalTag(ConventionalItemTags.WOODEN_RODS)
				.addOptionalTag(ConventionalItemTags.BLAZE_RODS);

		getOrCreateTagBuilder(ConventionalItemTags.HEADS)
				.add(Items.SKELETON_SKULL)
				.add(Items.WITHER_SKELETON_SKULL)
				.add(Items.PLAYER_HEAD)
				.add(Items.ZOMBIE_HEAD)
				.add(Items.CREEPER_HEAD)
				.add(Items.PIGLIN_HEAD)
				.add(Items.DRAGON_HEAD);

		getOrCreateTagBuilder(ConventionalItemTags.ROPES); // Generate tag so others can see it exists through JSON.

		getOrCreateTagBuilder(ConventionalItemTags.CHAINS)
				.add(Items.CHAIN);
	}

	private void generateDyedTags() {
		// Cannot pull entries from block tag because Wall Banners do not have an item form
		getOrCreateTagBuilder(ConventionalItemTags.BLACK_DYED)
				.add(Items.BLACK_BANNER).add(Items.BLACK_BED).add(Items.BLACK_CANDLE).add(Items.BLACK_CARPET)
				.add(Items.BLACK_CONCRETE).add(Items.BLACK_CONCRETE_POWDER).add(Items.BLACK_GLAZED_TERRACOTTA)
				.add(Items.BLACK_SHULKER_BOX).add(Items.BLACK_STAINED_GLASS).add(Items.BLACK_STAINED_GLASS_PANE)
				.add(Items.BLACK_TERRACOTTA).add(Items.BLACK_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.BLUE_DYED)
				.add(Items.BLUE_BANNER).add(Items.BLUE_BED).add(Items.BLUE_CANDLE).add(Items.BLUE_CARPET)
				.add(Items.BLUE_CONCRETE).add(Items.BLUE_CONCRETE_POWDER).add(Items.BLUE_GLAZED_TERRACOTTA)
				.add(Items.BLUE_SHULKER_BOX).add(Items.BLUE_STAINED_GLASS).add(Items.BLUE_STAINED_GLASS_PANE)
				.add(Items.BLUE_TERRACOTTA).add(Items.BLUE_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.BROWN_DYED)
				.add(Items.BROWN_BANNER).add(Items.BROWN_BED).add(Items.BROWN_CANDLE).add(Items.BROWN_CARPET)
				.add(Items.BROWN_CONCRETE).add(Items.BROWN_CONCRETE_POWDER).add(Items.BROWN_GLAZED_TERRACOTTA)
				.add(Items.BROWN_SHULKER_BOX).add(Items.BROWN_STAINED_GLASS).add(Items.BROWN_STAINED_GLASS_PANE)
				.add(Items.BROWN_TERRACOTTA).add(Items.BROWN_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.CYAN_DYED)
				.add(Items.CYAN_BANNER).add(Items.CYAN_BED).add(Items.CYAN_CANDLE).add(Items.CYAN_CARPET)
				.add(Items.CYAN_CONCRETE).add(Items.CYAN_CONCRETE_POWDER).add(Items.CYAN_GLAZED_TERRACOTTA)
				.add(Items.CYAN_SHULKER_BOX).add(Items.CYAN_STAINED_GLASS).add(Items.CYAN_STAINED_GLASS_PANE)
				.add(Items.CYAN_TERRACOTTA).add(Items.CYAN_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.GRAY_DYED)
				.add(Items.GRAY_BANNER).add(Items.GRAY_BED).add(Items.GRAY_CANDLE).add(Items.GRAY_CARPET)
				.add(Items.GRAY_CONCRETE).add(Items.GRAY_CONCRETE_POWDER).add(Items.GRAY_GLAZED_TERRACOTTA)
				.add(Items.GRAY_SHULKER_BOX).add(Items.GRAY_STAINED_GLASS).add(Items.GRAY_STAINED_GLASS_PANE)
				.add(Items.GRAY_TERRACOTTA).add(Items.GRAY_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.GREEN_DYED)
				.add(Items.GREEN_BANNER).add(Items.GREEN_BED).add(Items.GREEN_CANDLE).add(Items.GREEN_CARPET)
				.add(Items.GREEN_CONCRETE).add(Items.GREEN_CONCRETE_POWDER).add(Items.GREEN_GLAZED_TERRACOTTA)
				.add(Items.GREEN_SHULKER_BOX).add(Items.GREEN_STAINED_GLASS).add(Items.GREEN_STAINED_GLASS_PANE)
				.add(Items.GREEN_TERRACOTTA).add(Items.GREEN_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.LIGHT_BLUE_DYED)
				.add(Items.LIGHT_BLUE_BANNER).add(Items.LIGHT_BLUE_BED).add(Items.LIGHT_BLUE_CANDLE).add(Items.LIGHT_BLUE_CARPET)
				.add(Items.LIGHT_BLUE_CONCRETE).add(Items.LIGHT_BLUE_CONCRETE_POWDER).add(Items.LIGHT_BLUE_GLAZED_TERRACOTTA)
				.add(Items.LIGHT_BLUE_SHULKER_BOX).add(Items.LIGHT_BLUE_STAINED_GLASS).add(Items.LIGHT_BLUE_STAINED_GLASS_PANE)
				.add(Items.LIGHT_BLUE_TERRACOTTA).add(Items.LIGHT_BLUE_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.LIGHT_GRAY_DYED)
				.add(Items.LIGHT_GRAY_BANNER).add(Items.LIGHT_GRAY_BED).add(Items.LIGHT_GRAY_CANDLE).add(Items.LIGHT_GRAY_CARPET)
				.add(Items.LIGHT_GRAY_CONCRETE).add(Items.LIGHT_GRAY_CONCRETE_POWDER).add(Items.LIGHT_GRAY_GLAZED_TERRACOTTA)
				.add(Items.LIGHT_GRAY_SHULKER_BOX).add(Items.LIGHT_GRAY_STAINED_GLASS).add(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
				.add(Items.LIGHT_GRAY_TERRACOTTA).add(Items.LIGHT_GRAY_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.LIME_DYED)
				.add(Items.LIME_BANNER).add(Items.LIME_BED).add(Items.LIME_CANDLE).add(Items.LIME_CARPET)
				.add(Items.LIME_CONCRETE).add(Items.LIME_CONCRETE_POWDER).add(Items.LIME_GLAZED_TERRACOTTA)
				.add(Items.LIME_SHULKER_BOX).add(Items.LIME_STAINED_GLASS).add(Items.LIME_STAINED_GLASS_PANE)
				.add(Items.LIME_TERRACOTTA).add(Items.LIME_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.MAGENTA_DYED)
				.add(Items.MAGENTA_BANNER).add(Items.MAGENTA_BED).add(Items.MAGENTA_CANDLE).add(Items.MAGENTA_CARPET)
				.add(Items.MAGENTA_CONCRETE).add(Items.MAGENTA_CONCRETE_POWDER).add(Items.MAGENTA_GLAZED_TERRACOTTA)
				.add(Items.MAGENTA_SHULKER_BOX).add(Items.MAGENTA_STAINED_GLASS).add(Items.MAGENTA_STAINED_GLASS_PANE)
				.add(Items.MAGENTA_TERRACOTTA).add(Items.MAGENTA_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.ORANGE_DYED)
				.add(Items.ORANGE_BANNER).add(Items.ORANGE_BED).add(Items.ORANGE_CANDLE).add(Items.ORANGE_CARPET)
				.add(Items.ORANGE_CONCRETE).add(Items.ORANGE_CONCRETE_POWDER).add(Items.ORANGE_GLAZED_TERRACOTTA)
				.add(Items.ORANGE_SHULKER_BOX).add(Items.ORANGE_STAINED_GLASS).add(Items.ORANGE_STAINED_GLASS_PANE)
				.add(Items.ORANGE_TERRACOTTA).add(Items.ORANGE_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.PINK_DYED)
				.add(Items.PINK_BANNER).add(Items.PINK_BED).add(Items.PINK_CANDLE).add(Items.PINK_CARPET)
				.add(Items.PINK_CONCRETE).add(Items.PINK_CONCRETE_POWDER).add(Items.PINK_GLAZED_TERRACOTTA)
				.add(Items.PINK_SHULKER_BOX).add(Items.PINK_STAINED_GLASS).add(Items.PINK_STAINED_GLASS_PANE)
				.add(Items.PINK_TERRACOTTA).add(Items.PINK_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.PURPLE_DYED)
				.add(Items.PURPLE_BANNER).add(Items.PURPLE_BED).add(Items.PURPLE_CANDLE).add(Items.PURPLE_CARPET)
				.add(Items.PURPLE_CONCRETE).add(Items.PURPLE_CONCRETE_POWDER).add(Items.PURPLE_GLAZED_TERRACOTTA)
				.add(Items.PURPLE_SHULKER_BOX).add(Items.PURPLE_STAINED_GLASS).add(Items.PURPLE_STAINED_GLASS_PANE)
				.add(Items.PURPLE_TERRACOTTA).add(Items.PURPLE_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.RED_DYED)
				.add(Items.RED_BANNER).add(Items.RED_BED).add(Items.RED_CANDLE).add(Items.RED_CARPET)
				.add(Items.RED_CONCRETE).add(Items.RED_CONCRETE_POWDER).add(Items.RED_GLAZED_TERRACOTTA)
				.add(Items.RED_SHULKER_BOX).add(Items.RED_STAINED_GLASS).add(Items.RED_STAINED_GLASS_PANE)
				.add(Items.RED_TERRACOTTA).add(Items.RED_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.WHITE_DYED)
				.add(Items.WHITE_BANNER).add(Items.WHITE_BED).add(Items.WHITE_CANDLE).add(Items.WHITE_CARPET)
				.add(Items.WHITE_CONCRETE).add(Items.WHITE_CONCRETE_POWDER).add(Items.WHITE_GLAZED_TERRACOTTA)
				.add(Items.WHITE_SHULKER_BOX).add(Items.WHITE_STAINED_GLASS).add(Items.WHITE_STAINED_GLASS_PANE)
				.add(Items.WHITE_TERRACOTTA).add(Items.WHITE_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.YELLOW_DYED)
				.add(Items.YELLOW_BANNER).add(Items.YELLOW_BED).add(Items.YELLOW_CANDLE).add(Items.YELLOW_CARPET)
				.add(Items.YELLOW_CONCRETE).add(Items.YELLOW_CONCRETE_POWDER).add(Items.YELLOW_GLAZED_TERRACOTTA)
				.add(Items.YELLOW_SHULKER_BOX).add(Items.YELLOW_STAINED_GLASS).add(Items.YELLOW_STAINED_GLASS_PANE)
				.add(Items.YELLOW_TERRACOTTA).add(Items.YELLOW_WOOL);

		getOrCreateTagBuilder(ConventionalItemTags.DYED)
				.addTag(ConventionalItemTags.WHITE_DYED)
				.addTag(ConventionalItemTags.ORANGE_DYED)
				.addTag(ConventionalItemTags.MAGENTA_DYED)
				.addTag(ConventionalItemTags.LIGHT_BLUE_DYED)
				.addTag(ConventionalItemTags.YELLOW_DYED)
				.addTag(ConventionalItemTags.LIME_DYED)
				.addTag(ConventionalItemTags.PINK_DYED)
				.addTag(ConventionalItemTags.GRAY_DYED)
				.addTag(ConventionalItemTags.LIGHT_GRAY_DYED)
				.addTag(ConventionalItemTags.CYAN_DYED)
				.addTag(ConventionalItemTags.PURPLE_DYED)
				.addTag(ConventionalItemTags.BLUE_DYED)
				.addTag(ConventionalItemTags.BROWN_DYED)
				.addTag(ConventionalItemTags.GREEN_DYED)
				.addTag(ConventionalItemTags.RED_DYED)
				.addTag(ConventionalItemTags.BLACK_DYED);
	}

	private void generateBackwardsCompatTags() {
		// Backwards compat with pre-1.21 tags. Done after so optional tag is last for better readability.
		// TODO: Remove backwards compat tag entries in 1.22

		getOrCreateTagBuilder(ConventionalItemTags.WOODEN_BARRELS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "wooden_barrels"));
		getOrCreateTagBuilder(ConventionalItemTags.WOODEN_CHESTS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "wooden_chests"));
		getOrCreateTagBuilder(ConventionalItemTags.BLACK_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "black_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.BLUE_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "blue_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.BROWN_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "brown_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.GREEN_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "green_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.RED_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "red_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.WHITE_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "white_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.YELLOW_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "yellow_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.LIGHT_BLUE_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "light_blue_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.LIGHT_GRAY_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "light_gray_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.LIME_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "lime_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.MAGENTA_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "magenta_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.ORANGE_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "orange_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.PINK_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "pink_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.CYAN_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "cyan_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.GRAY_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "gray_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.PURPLE_DYES).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "purple_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.IRON_RAW_MATERIALS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "raw_iron_ores"));
		getOrCreateTagBuilder(ConventionalItemTags.COPPER_RAW_MATERIALS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "raw_copper_ores"));
		getOrCreateTagBuilder(ConventionalItemTags.GOLD_RAW_MATERIALS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "raw_gold_ores"));
		getOrCreateTagBuilder(ConventionalItemTags.GLOWSTONE_DUSTS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "glowstone_dusts"));
		getOrCreateTagBuilder(ConventionalItemTags.REDSTONE_DUSTS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "redstone_dusts"));
		getOrCreateTagBuilder(ConventionalItemTags.DIAMOND_GEMS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "diamonds"));
		getOrCreateTagBuilder(ConventionalItemTags.LAPIS_GEMS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "lapis"));
		getOrCreateTagBuilder(ConventionalItemTags.EMERALD_GEMS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "emeralds"));
		getOrCreateTagBuilder(ConventionalItemTags.QUARTZ_GEMS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "quartz"));
		getOrCreateTagBuilder(ConventionalItemTags.SHEARS_TOOLS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "shears"));
		getOrCreateTagBuilder(ConventionalItemTags.SPEARS_TOOLS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "spears"));
		getOrCreateTagBuilder(ConventionalItemTags.BOWS_TOOLS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "bows"));
		getOrCreateTagBuilder(ConventionalItemTags.SHIELDS_TOOLS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "shields"));
		getOrCreateTagBuilder(ConventionalItemTags.STRINGS).addOptionalTag(new Identifier(TagUtil.C_TAG_NAMESPACE, "strings"));
	}
}
