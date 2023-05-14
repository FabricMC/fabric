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
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;

public class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
	/** @deprecated Replaced with {@link ItemTags#PICKAXES}. */
	@Deprecated
	private static final Identifier FABRIC_PICKAXES = createFabricId("pickaxes");
	/** @deprecated Replaced with {@link ItemTags#SHOVELS}. */
	@Deprecated
	private static final Identifier FABRIC_SHOVELS = createFabricId("shovels");
	/** @deprecated Replaced with {@link ItemTags#HOES}. */
	@Deprecated
	private static final Identifier FABRIC_HOES = createFabricId("hoes");
	/** @deprecated Replaced with {@link ItemTags#AXES}. */
	@Deprecated
	private static final Identifier FABRIC_AXES = createFabricId("axes");
	/** @deprecated Replaced with {@link ConventionalItemTags#SHEARS}. */
	@Deprecated
	private static final Identifier FABRIC_SHEARS = createFabricId("shears");
	/** @deprecated Replaced with {@link ItemTags#SWORDS}. */
	@Deprecated
	private static final Identifier FABRIC_SWORDS = createFabricId("swords");

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
		generateVillagerJobSites();
		copyItemTags();
	}

	private void copyItemTags() {
		copy(ConventionalBlockTags.BOOKSHELVES, ConventionalItemTags.BOOKSHELVES);
		copy(ConventionalBlockTags.CHESTS, ConventionalItemTags.CHESTS);
		copy(ConventionalBlockTags.GLASS_BLOCKS, ConventionalItemTags.GLASS_BLOCKS);
		copy(ConventionalBlockTags.GLASS_PANES, ConventionalItemTags.GLASS_PANES);
		copy(ConventionalBlockTags.SHULKER_BOXES, ConventionalItemTags.SHULKER_BOXES);
		copy(ConventionalBlockTags.WOODEN_BARRELS, ConventionalItemTags.WOODEN_BARRELS);

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
	}

	private void generateDyeTags() {
		getOrCreateTagBuilder(ConventionalItemTags.DYES)
				.addOptionalTag(ConventionalItemTags.BLACK_DYES)
				.addOptionalTag(ConventionalItemTags.BLUE_DYES)
				.addOptionalTag(ConventionalItemTags.BROWN_DYES)
				.addOptionalTag(ConventionalItemTags.GREEN_DYES)
				.addOptionalTag(ConventionalItemTags.RED_DYES)
				.addOptionalTag(ConventionalItemTags.WHITE_DYES)
				.addOptionalTag(ConventionalItemTags.YELLOW_DYES)
				.addOptionalTag(ConventionalItemTags.LIGHT_GRAY_DYES)
				.addOptionalTag(ConventionalItemTags.LIGHT_BLUE_DYES)
				.addOptionalTag(ConventionalItemTags.LIME_DYES)
				.addOptionalTag(ConventionalItemTags.MAGENTA_DYES)
				.addOptionalTag(ConventionalItemTags.ORANGE_DYES)
				.addOptionalTag(ConventionalItemTags.PINK_DYES)
				.addOptionalTag(ConventionalItemTags.CYAN_DYES)
				.addOptionalTag(ConventionalItemTags.GRAY_DYES)
				.addOptionalTag(ConventionalItemTags.PURPLE_DYES);
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
		getOrCreateTagBuilder(ConventionalItemTags.EMPTY_BUCKETS)
				.add(Items.BUCKET);
		getOrCreateTagBuilder(ConventionalItemTags.LAVA_BUCKETS)
				.add(Items.LAVA_BUCKET);
		getOrCreateTagBuilder(ConventionalItemTags.ENTITY_WATER_BUCKETS)
				.add(Items.AXOLOTL_BUCKET)
				.add(Items.COD_BUCKET)
				.add(Items.PUFFERFISH_BUCKET)
				.add(Items.TROPICAL_FISH_BUCKET)
				.add(Items.SALMON_BUCKET)
				.add(Items.TADPOLE_BUCKET);
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
				.add(Items.DIAMOND, Items.EMERALD, Items.AMETHYST_SHARD, Items.LAPIS_LAZULI);
		getOrCreateTagBuilder(ConventionalItemTags.INGOTS)
				.add(Items.COPPER_INGOT, Items.GOLD_INGOT, Items.IRON_INGOT, Items.NETHERITE_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.NUGGETS)
				.add(Items.GOLD_NUGGET, Items.IRON_NUGGET);
		copy(ConventionalBlockTags.ORES, ConventionalItemTags.ORES);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_ORES)
				.addOptionalTag(ConventionalItemTags.RAW_IRON_ORES)
				.addOptionalTag(ConventionalItemTags.RAW_COPPER_ORES)
				.addOptionalTag(ConventionalItemTags.RAW_GOLD_ORES);

		// Vanilla instances
		getOrCreateTagBuilder(ConventionalItemTags.IRON_INGOTS)
				.add(Items.IRON_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.COPPER_INGOTS)
				.add(Items.COPPER_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.GOLD_INGOTS)
				.add(Items.GOLD_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.NETHERITE_INGOTS)
				.add(Items.NETHERITE_INGOT);
		getOrCreateTagBuilder(ConventionalItemTags.REDSTONE_DUSTS)
				.add(Items.REDSTONE);
		getOrCreateTagBuilder(ConventionalItemTags.QUARTZ_ORES)
				.add(Items.NETHER_QUARTZ_ORE);
		getOrCreateTagBuilder(ConventionalItemTags.QUARTZ)
				.add(Items.QUARTZ);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_IRON_ORES)
				.add(Items.RAW_IRON);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_COPPER_ORES)
				.add(Items.RAW_COPPER);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_GOLD_ORES)
				.add(Items.RAW_GOLD);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_IRON_BLOCKS)
				.add(Items.RAW_IRON_BLOCK);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_COPPER_BLOCKS)
				.add(Items.RAW_COPPER_BLOCK);
		getOrCreateTagBuilder(ConventionalItemTags.RAW_GOLD_BLOCKS)
				.add(Items.RAW_GOLD_BLOCK);
		getOrCreateTagBuilder(ConventionalItemTags.COAL)
				.addOptionalTag(ItemTags.COALS);
		getOrCreateTagBuilder(ConventionalItemTags.EMERALDS)
				.add(Items.EMERALD);
		getOrCreateTagBuilder(ConventionalItemTags.LAPIS)
				.add(Items.LAPIS_LAZULI);
		getOrCreateTagBuilder(ConventionalItemTags.DIAMONDS)
				.add(Items.DIAMOND);
	}

	private void generateToolTags() {
		getOrCreateTagBuilder(ConventionalItemTags.AXES)
				.addOptionalTag(FABRIC_AXES)
				.add(Items.DIAMOND_AXE)
				.add(Items.GOLDEN_AXE)
				.add(Items.WOODEN_AXE)
				.add(Items.STONE_AXE)
				.add(Items.IRON_AXE)
				.add(Items.NETHERITE_AXE);
		getOrCreateTagBuilder(ConventionalItemTags.PICKAXES)
				.addOptionalTag(FABRIC_PICKAXES)
				.add(Items.DIAMOND_PICKAXE)
				.add(Items.GOLDEN_PICKAXE)
				.add(Items.WOODEN_PICKAXE)
				.add(Items.STONE_PICKAXE)
				.add(Items.IRON_PICKAXE)
				.add(Items.NETHERITE_PICKAXE);
		getOrCreateTagBuilder(ConventionalItemTags.HOES)
				.addOptionalTag(FABRIC_HOES)
				.add(Items.DIAMOND_HOE)
				.add(Items.GOLDEN_HOE)
				.add(Items.WOODEN_HOE)
				.add(Items.STONE_HOE)
				.add(Items.IRON_HOE)
				.add(Items.NETHERITE_HOE);
		getOrCreateTagBuilder(ConventionalItemTags.SWORDS)
				.addOptionalTag(FABRIC_SWORDS)
				.add(Items.DIAMOND_SWORD)
				.add(Items.GOLDEN_SWORD)
				.add(Items.WOODEN_SWORD)
				.add(Items.STONE_SWORD)
				.add(Items.IRON_SWORD)
				.add(Items.NETHERITE_SWORD);
		getOrCreateTagBuilder(ConventionalItemTags.SHOVELS)
				.addOptionalTag(FABRIC_SHOVELS)
				.add(Items.DIAMOND_SHOVEL)
				.add(Items.GOLDEN_SHOVEL)
				.add(Items.WOODEN_SHOVEL)
				.add(Items.STONE_SHOVEL)
				.add(Items.IRON_SHOVEL)
				.add(Items.NETHERITE_SHOVEL);
		getOrCreateTagBuilder(ItemTags.AXES)
				.addOptionalTag(ConventionalItemTags.AXES)
				.addOptionalTag(FABRIC_AXES);
		getOrCreateTagBuilder(ItemTags.PICKAXES)
				.addOptionalTag(ConventionalItemTags.PICKAXES)
				.addOptionalTag(FABRIC_PICKAXES);
		getOrCreateTagBuilder(ItemTags.HOES)
				.addOptionalTag(ConventionalItemTags.HOES)
				.addOptionalTag(FABRIC_HOES);
		getOrCreateTagBuilder(ItemTags.SWORDS)
				.addOptionalTag(ConventionalItemTags.SWORDS)
				.addOptionalTag(FABRIC_SWORDS);
		getOrCreateTagBuilder(ItemTags.SHOVELS)
				.addOptionalTag(ConventionalItemTags.SHOVELS)
				.addOptionalTag(FABRIC_SHOVELS);
		getOrCreateTagBuilder(ConventionalItemTags.SHEARS)
				.addOptionalTag(FABRIC_SHEARS)
				.add(Items.SHEARS);
		getOrCreateTagBuilder(ConventionalItemTags.SPEARS)
				.add(Items.TRIDENT);
		getOrCreateTagBuilder(ConventionalItemTags.BOWS)
				.add(Items.CROSSBOW)
				.add(Items.BOW);
		getOrCreateTagBuilder(ConventionalItemTags.SHIELDS)
				.add(Items.SHIELD);
	}

	private void generateVillagerJobSites() {
		BlockTagGenerator.VILLAGER_JOB_SITE_BLOCKS.stream()
				.map(ItemConvertible::asItem)
				.distinct() // cauldron blocks have the same item
				.forEach(getOrCreateTagBuilder(ConventionalItemTags.VILLAGER_JOB_SITES)::add);
	}

	private static Identifier createFabricId(String id) {
		return new Identifier("fabric", id);
	}
}
