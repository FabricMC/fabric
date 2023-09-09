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
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {

	public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, FabricTagProvider.BlockTagProvider blockTags) {
		super(output, completableFuture, blockTags);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup arg) {
		generateToolTags();
		generateBucketTags();
		generateOreAndRelatedTags();
		generateConsumableTags();
		generateDyeTags();
		generateDyedTags();
		generateVillagerJobSites();
		copyItemTags();
		generateBackwardsCompatTags();
	}

	private void copyItemTags() {
		copy(ConventionalBlockTags.BARRELS, ConventionalItemTags.BARRELS);
		copy(ConventionalBlockTags.BOOKSHELVES, ConventionalItemTags.BOOKSHELVES);
		copy(ConventionalBlockTags.CHESTS, ConventionalItemTags.CHESTS);
		copy(ConventionalBlockTags.GLASS_BLOCKS, ConventionalItemTags.GLASS_BLOCKS);
		copy(ConventionalBlockTags.GLASS_PANES, ConventionalItemTags.GLASS_PANES);
		copy(ConventionalBlockTags.SHULKER_BOXES, ConventionalItemTags.SHULKER_BOXES);
		copy(ConventionalBlockTags.BARRELS_WOODEN, ConventionalItemTags.BARRELS_WOODEN);

		copy(ConventionalBlockTags.BUDDING_BLOCKS, ConventionalItemTags.BUDDING_BLOCKS);
		copy(ConventionalBlockTags.BUDS, ConventionalItemTags.BUDS);
		copy(ConventionalBlockTags.CLUSTERS, ConventionalItemTags.CLUSTERS);

		copy(ConventionalBlockTags.SANDSTONE_BLOCKS, ConventionalItemTags.SANDSTONE_BLOCKS);
		copy(ConventionalBlockTags.SANDSTONE_SLABS, ConventionalItemTags.SANDSTONE_SLABS);
		copy(ConventionalBlockTags.SANDSTONE_STAIRS, ConventionalItemTags.SANDSTONE_STAIRS);
		copy(ConventionalBlockTags.SANDSTONE_RED_BLOCKS, ConventionalItemTags.SANDSTONE_RED_BLOCKS);
		copy(ConventionalBlockTags.SANDSTONE_RED_SLABS, ConventionalItemTags.SANDSTONE_RED_SLABS);
		copy(ConventionalBlockTags.SANDSTONE_RED_STAIRS, ConventionalItemTags.SANDSTONE_RED_STAIRS);
		copy(ConventionalBlockTags.SANDSTONE_UNCOLORED_BLOCKS, ConventionalItemTags.SANDSTONE_UNCOLORED_BLOCKS);
		copy(ConventionalBlockTags.SANDSTONE_UNCOLORED_SLABS, ConventionalItemTags.SANDSTONE_UNCOLORED_SLABS);
		copy(ConventionalBlockTags.SANDSTONE_UNCOLORED_STAIRS, ConventionalItemTags.SANDSTONE_UNCOLORED_STAIRS);
	}

	private void generateDyeTags() {
		getOrCreateTagBuilder(ConventionalItemTags.DYES)
				.addOptionalTag(ConventionalItemTags.DYES_BLACK)
				.addOptionalTag(ConventionalItemTags.DYES_BLUE)
				.addOptionalTag(ConventionalItemTags.DYES_BROWN)
				.addOptionalTag(ConventionalItemTags.DYES_GREEN)
				.addOptionalTag(ConventionalItemTags.DYES_RED)
				.addOptionalTag(ConventionalItemTags.DYES_WHITE)
				.addOptionalTag(ConventionalItemTags.DYES_YELLOW)
				.addOptionalTag(ConventionalItemTags.DYES_LIGHT_GRAY)
				.addOptionalTag(ConventionalItemTags.DYES_LIGHT_BLUE)
				.addOptionalTag(ConventionalItemTags.DYES_LIME)
				.addOptionalTag(ConventionalItemTags.DYES_MAGENTA)
				.addOptionalTag(ConventionalItemTags.DYES_ORANGE)
				.addOptionalTag(ConventionalItemTags.DYES_PINK)
				.addOptionalTag(ConventionalItemTags.DYES_CYAN)
				.addOptionalTag(ConventionalItemTags.DYES_GRAY)
				.addOptionalTag(ConventionalItemTags.DYES_PURPLE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_BLACK)
				.add(Items.BLACK_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_BLUE)
				.add(Items.BLUE_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_BROWN)
				.add(Items.BROWN_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_GREEN)
				.add(Items.GREEN_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_RED)
				.add(Items.RED_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_WHITE)
				.add(Items.WHITE_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_YELLOW)
				.add(Items.YELLOW_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_LIGHT_BLUE)
				.add(Items.LIGHT_BLUE_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_LIGHT_GRAY)
				.add(Items.LIGHT_GRAY_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_LIME)
				.add(Items.LIME_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_MAGENTA)
				.add(Items.MAGENTA_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_ORANGE)
				.add(Items.ORANGE_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_PINK)
				.add(Items.PINK_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_CYAN)
				.add(Items.CYAN_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_GRAY)
				.add(Items.GRAY_DYE);
		getOrCreateTagBuilder(ConventionalItemTags.DYES_PURPLE)
				.add(Items.PURPLE_DYE);
	}

	private void generateConsumableTags() {
		Registries.ITEM.forEach(item -> {
			if (item.getFoodComponent() != null) {
				getOrCreateTagBuilder(ConventionalItemTags.FOODS).add(item);
			}
		});
		getOrCreateTagBuilder(ConventionalItemTags.POTIONS)
				.add(Items.LINGERING_POTION)
				.add(Items.SPLASH_POTION)
				.add(Items.POTION);
	}

	private void generateBucketTags() {
		getOrCreateTagBuilder(ConventionalItemTags.BUCKETS_EMPTY)
				.add(Items.BUCKET);
		getOrCreateTagBuilder(ConventionalItemTags.BUCKETS_LAVA)
				.add(Items.LAVA_BUCKET);
		getOrCreateTagBuilder(ConventionalItemTags.BUCKETS_ENTITY_WATER)
				.add(Items.AXOLOTL_BUCKET)
				.add(Items.COD_BUCKET)
				.add(Items.PUFFERFISH_BUCKET)
				.add(Items.TROPICAL_FISH_BUCKET)
				.add(Items.SALMON_BUCKET)
				.add(Items.TADPOLE_BUCKET);
		getOrCreateTagBuilder(ConventionalItemTags.BUCKETS_WATER)
				.add(Items.WATER_BUCKET);
		getOrCreateTagBuilder(ConventionalItemTags.BUCKETS_MILK)
				.add(Items.MILK_BUCKET);
	}

	private void generateOreAndRelatedTags() {
		// Categories
		getOrCreateTagBuilder(ConventionalItemTags.DUSTS)
				.add(Items.GLOWSTONE_DUST)
				.add(Items.REDSTONE);
		getOrCreateTagBuilder(ConventionalItemTags.GEMS)
				.add(Items.DIAMOND, Items.EMERALD, Items.AMETHYST_SHARD, Items.LAPIS_LAZULI);
		getOrCreateTagBuilder(ConventionalItemTags.INGOTS)
				.add(Items.COPPER_INGOT, Items.GOLD_INGOT, Items.IRON_INGOT, Items.NETHERITE_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.NUGGETS)
				.add(Items.GOLD_NUGGET, Items.IRON_NUGGET);
		copy(ConventionalBlockTags.ORES, ConventionalItemTags.ORES);
		getOrCreateTagBuilder(ConventionalItemTags.ORES)
				.addOptionalTag(ConventionalItemTags.ORES_QUARTZ)
				.addOptionalTag(ConventionalItemTags.ORES_NETHERITE_SCRAP);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS)
				.addOptionalTag(ConventionalItemTags.RAW_MATERIALS_IRON)
				.addOptionalTag(ConventionalItemTags.RAW_MATERIALS_COPPER)
				.addOptionalTag(ConventionalItemTags.RAW_MATERIALS_GOLD);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS)
				.add(Items.RAW_IRON, Items.RAW_COPPER, Items.RAW_GOLD);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_BLOCKS)
				.add(Items.RAW_IRON_BLOCK, Items.RAW_COPPER_BLOCK, Items.RAW_GOLD_BLOCK);

		// Vanilla instances
		getOrCreateTagBuilder(ConventionalItemTags.INGOTS_IRON)
				.add(Items.IRON_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.INGOTS_COPPER)
				.add(Items.COPPER_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.INGOTS_GOLD)
				.add(Items.GOLD_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.INGOTS_NETHERITE)
				.add(Items.NETHERITE_INGOT);

		getOrCreateTagBuilder(ConventionalItemTags.RAW_BLOCKS_IRON)
				.add(Items.RAW_IRON_BLOCK);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_BLOCKS_COPPER)
				.add(Items.RAW_COPPER_BLOCK);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_BLOCKS_GOLD)
				.add(Items.RAW_GOLD_BLOCK);

		getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS_IRON)
				.add(Items.RAW_IRON);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS_COPPER)
				.add(Items.RAW_COPPER);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS_GOLD)
				.add(Items.RAW_GOLD);

		getOrCreateTagBuilder(ConventionalItemTags.DUSTS_REDSTONE)
				.add(Items.REDSTONE);
		getOrCreateTagBuilder(ConventionalItemTags.DUSTS_GLOWSTONE)
				.add(Items.GLOWSTONE_DUST);
		getOrCreateTagBuilder(ConventionalItemTags.COAL)
				.addOptionalTag(ItemTags.COALS);

		getOrCreateTagBuilder(ConventionalItemTags.ORES_QUARTZ)
				.add(Items.NETHER_QUARTZ_ORE);
		getOrCreateTagBuilder(ConventionalItemTags.ORES_NETHERITE_SCRAP)
				.add(Items.ANCIENT_DEBRIS);

		getOrCreateTagBuilder(ConventionalItemTags.GEMS_QUARTZ)
				.add(Items.QUARTZ);
		getOrCreateTagBuilder(ConventionalItemTags.GEMS_EMERALD)
				.add(Items.EMERALD);
		getOrCreateTagBuilder(ConventionalItemTags.GEMS_LAPIS)
				.add(Items.LAPIS_LAZULI);
		getOrCreateTagBuilder(ConventionalItemTags.GEMS_DIAMOND)
				.add(Items.DIAMOND);
		getOrCreateTagBuilder(ConventionalItemTags.GEMS_AMETHYST)
				.add(Items.AMETHYST_SHARD);
		getOrCreateTagBuilder(ConventionalItemTags.GEMS_PRISMARINE)
				.add(Items.PRISMARINE_CRYSTALS);
	}

	private void generateToolTags() {
		getOrCreateTagBuilder(ConventionalItemTags.TOOLS)
				.addOptionalTag(ConventionalItemTags.TOOLS_BOWS)
				.addOptionalTag(ConventionalItemTags.TOOLS_CROSSBOWS)
				.addOptionalTag(ConventionalItemTags.TOOLS_SHEARS)
				.addOptionalTag(ConventionalItemTags.TOOLS_SHIELDS)
				.addOptionalTag(ConventionalItemTags.TOOLS_SPEARS)
				.addOptionalTag(ConventionalItemTags.TOOLS_FISHING_RODS);

		getOrCreateTagBuilder(ConventionalItemTags.TOOLS_BOWS)
				.add(Items.BOW);
		getOrCreateTagBuilder(ConventionalItemTags.TOOLS_CROSSBOWS)
				.add(Items.CROSSBOW);
		getOrCreateTagBuilder(ConventionalItemTags.TOOLS_SHEARS)
				.add(Items.SHEARS);
		getOrCreateTagBuilder(ConventionalItemTags.TOOLS_SHIELDS)
				.add(Items.SHIELD);
		getOrCreateTagBuilder(ConventionalItemTags.TOOLS_SPEARS)
				.add(Items.TRIDENT);
		getOrCreateTagBuilder(ConventionalItemTags.TOOLS_FISHING_RODS)
				.add(Items.FISHING_ROD);
	}

	private void generateVillagerJobSites() {
		BlockTagGenerator.VILLAGER_JOB_SITE_BLOCKS.stream()
				.map(ItemConvertible::asItem)
				.distinct() // cauldron blocks have the same item
				.forEach(getOrCreateTagBuilder(ConventionalItemTags.VILLAGER_JOB_SITES)::add);
	}

	private void generateDyedTags() {
		copy(ConventionalBlockTags.DYED_BLOCKS_BLACK, ConventionalItemTags.DYED_ITEMS_BLACK);
		copy(ConventionalBlockTags.DYED_BLOCKS_BLUE, ConventionalItemTags.DYED_ITEMS_BLUE);
		copy(ConventionalBlockTags.DYED_BLOCKS_BROWN, ConventionalItemTags.DYED_ITEMS_BROWN);
		copy(ConventionalBlockTags.DYED_BLOCKS_CYAN, ConventionalItemTags.DYED_ITEMS_CYAN);
		copy(ConventionalBlockTags.DYED_BLOCKS_GRAY, ConventionalItemTags.DYED_ITEMS_GRAY);
		copy(ConventionalBlockTags.DYED_BLOCKS_GREEN, ConventionalItemTags.DYED_ITEMS_GREEN);
		copy(ConventionalBlockTags.DYED_BLOCKS_LIGHT_GRAY, ConventionalItemTags.DYED_ITEMS_LIGHT_GRAY);
		copy(ConventionalBlockTags.DYED_BLOCKS_LIGHT_BLUE, ConventionalItemTags.DYED_ITEMS_LIGHT_BLUE);
		copy(ConventionalBlockTags.DYED_BLOCKS_LIME, ConventionalItemTags.DYED_ITEMS_LIME);
		copy(ConventionalBlockTags.DYED_BLOCKS_MAGENTA, ConventionalItemTags.DYED_ITEMS_MAGENTA);
		copy(ConventionalBlockTags.DYED_BLOCKS_ORANGE, ConventionalItemTags.DYED_ITEMS_ORANGE);
		copy(ConventionalBlockTags.DYED_BLOCKS_PINK, ConventionalItemTags.DYED_ITEMS_PINK);
		copy(ConventionalBlockTags.DYED_BLOCKS_PURPLE, ConventionalItemTags.DYED_ITEMS_PURPLE);
		copy(ConventionalBlockTags.DYED_BLOCKS_RED, ConventionalItemTags.DYED_ITEMS_RED);
		copy(ConventionalBlockTags.DYED_BLOCKS_WHITE, ConventionalItemTags.DYED_ITEMS_WHITE);
		copy(ConventionalBlockTags.DYED_BLOCKS_YELLOW, ConventionalItemTags.DYED_ITEMS_YELLOW);

		getOrCreateTagBuilder(ConventionalItemTags.DYED_ITEMS)
				.addTag(ConventionalItemTags.DYED_ITEMS_BLACK)
				.addTag(ConventionalItemTags.DYED_ITEMS_BLUE)
				.addTag(ConventionalItemTags.DYED_ITEMS_BROWN)
				.addTag(ConventionalItemTags.DYED_ITEMS_CYAN)
				.addTag(ConventionalItemTags.DYED_ITEMS_GRAY)
				.addTag(ConventionalItemTags.DYED_ITEMS_GREEN)
				.addTag(ConventionalItemTags.DYED_ITEMS_LIGHT_BLUE)
				.addTag(ConventionalItemTags.DYED_ITEMS_LIGHT_GRAY)
				.addTag(ConventionalItemTags.DYED_ITEMS_LIME)
				.addTag(ConventionalItemTags.DYED_ITEMS_MAGENTA)
				.addTag(ConventionalItemTags.DYED_ITEMS_ORANGE)
				.addTag(ConventionalItemTags.DYED_ITEMS_PINK)
				.addTag(ConventionalItemTags.DYED_ITEMS_PURPLE)
				.addTag(ConventionalItemTags.DYED_ITEMS_RED)
				.addTag(ConventionalItemTags.DYED_ITEMS_WHITE)
				.addTag(ConventionalItemTags.DYED_ITEMS_YELLOW);
	}

	private void generateBackwardsCompatTags() {
		// Backwards compat with pre-1.21 tags. Done after so optional tag is last for better readability.
		// TODO: Remove backwards compat tag entries in 1.22

		getOrCreateTagBuilder(ConventionalItemTags.DYES_BLACK).addOptionalTag(new Identifier("c", "black_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_BLUE).addOptionalTag(new Identifier("c", "blue_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_BROWN).addOptionalTag(new Identifier("c", "brown_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_GREEN).addOptionalTag(new Identifier("c", "green_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_RED).addOptionalTag(new Identifier("c", "red_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_WHITE).addOptionalTag(new Identifier("c", "white_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_YELLOW).addOptionalTag(new Identifier("c", "yellow_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_LIGHT_BLUE).addOptionalTag(new Identifier("c", "light_blue_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_LIGHT_GRAY).addOptionalTag(new Identifier("c", "light_gray_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_LIME).addOptionalTag(new Identifier("c", "lime_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_MAGENTA).addOptionalTag(new Identifier("c", "magenta_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_ORANGE).addOptionalTag(new Identifier("c", "orange_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_PINK).addOptionalTag(new Identifier("c", "pink_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_CYAN).addOptionalTag(new Identifier("c", "cyan_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_GRAY).addOptionalTag(new Identifier("c", "gray_dyes"));
		getOrCreateTagBuilder(ConventionalItemTags.DYES_PURPLE).addOptionalTag(new Identifier("c", "purple_dyes"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.DYES);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.FOODS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.POTIONS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.BUCKETS_EMPTY).addOptionalTag(new Identifier("c", "empty_buckets"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.BUCKETS_LAVA).addOptionalTag(new Identifier("c", "lava_buckets"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.BUCKETS_ENTITY_WATER).addOptionalTag(new Identifier("c", "entity_water_buckets"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.BUCKETS_WATER).addOptionalTag(new Identifier("c", "water_buckets"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.BUCKETS_MILK).addOptionalTag(new Identifier("c", "milk_buckets"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.DUSTS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.GEMS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.INGOTS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.NUGGETS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.RAW_MATERIALS).addOptionalTag(new Identifier("c", "raw_ores"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.INGOTS_IRON).addOptionalTag(new Identifier("c", "iron_ingots"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.INGOTS_COPPER).addOptionalTag(new Identifier("c", "copper_ingots"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.INGOTS_GOLD).addOptionalTag(new Identifier("c", "gold_ingots"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.INGOTS_NETHERITE).addOptionalTag(new Identifier("c", "netherite_ingots"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.DUSTS_REDSTONE).addOptionalTag(new Identifier("c", "dusts"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.ORES_QUARTZ).addOptionalTag(new Identifier("c", "quartz_ores"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.RAW_BLOCKS_IRON).addOptionalTag(new Identifier("c", "raw_iron_blocks"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.RAW_BLOCKS_COPPER).addOptionalTag(new Identifier("c", "raw_copper_blocks"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.RAW_BLOCKS_GOLD).addOptionalTag(new Identifier("c", "raw_gold_blocks"));
		getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS_IRON).addOptionalTag(new Identifier("c", "raw_iron_ores"));
		getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS_COPPER).addOptionalTag(new Identifier("c", "raw_copper_ores"));
		getOrCreateTagBuilder(ConventionalItemTags.RAW_MATERIALS_GOLD).addOptionalTag(new Identifier("c", "raw_gold_ores"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.COAL);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.GEMS_EMERALD);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.GEMS_LAPIS);
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.GEMS_DIAMOND);
		getOrCreateTagBuilder(ConventionalItemTags.GEMS_DIAMOND).addOptionalTag(new Identifier("c", "diamonds"));
		getOrCreateTagBuilder(ConventionalItemTags.GEMS_LAPIS).addOptionalTag(new Identifier("c", "lapis"));
		getOrCreateTagBuilder(ConventionalItemTags.GEMS_EMERALD).addOptionalTag(new Identifier("c", "emeralds"));
		getOrCreateTagBuilder(ConventionalItemTags.GEMS_QUARTZ).addOptionalTag(new Identifier("c", "quartz"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.TOOLS_SHEARS).addOptionalTag(new Identifier("c", "shears"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.TOOLS_SPEARS).addOptionalTag(new Identifier("c", "spears"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.TOOLS_BOWS).addOptionalTag(new Identifier("c", "bows"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.TOOLS_SHIELDS).addOptionalTag(new Identifier("c", "shields"));
		getOrCreateTagBuilderWithOptionalLegacy(ConventionalItemTags.VILLAGER_JOB_SITES);
	}

	private FabricTagBuilder getOrCreateTagBuilderWithOptionalLegacy(TagKey<Item> tag)
	{
		return getOrCreateTagBuilder(tag).addOptionalTag(new Identifier("c", tag.id().getPath()));
	}
}
