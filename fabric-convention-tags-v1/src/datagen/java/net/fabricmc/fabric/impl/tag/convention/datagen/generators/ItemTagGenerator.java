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

import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;

public class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
	/**
	 * @deprecated Use {@link ConventionalItemTags#PICKAXES}
	 */
	@Deprecated
	private static final Identifier FABRIC_PICKAXES = createFabricId("pickaxes");
	/**
	 * @deprecated Use {@link ConventionalItemTags#SHOVELS}
	 */
	@Deprecated
	private static final Identifier FABRIC_SHOVELS = createFabricId("shovels");
	/**
	 * @deprecated Use {@link ConventionalItemTags#HOES}
	 */
	@Deprecated
	private static final Identifier FABRIC_HOES = createFabricId("hoes");
	/**
	 * @deprecated Use {@link ConventionalItemTags#AXES}
	 */
	@Deprecated
	private static final Identifier FABRIC_AXES = createFabricId("axes");
	/**
	 * @deprecated Use {@link ConventionalItemTags#SHEARS}
	 */
	@Deprecated
	private static final Identifier FABRIC_SHEARS = createFabricId("shears");
	/**
	 * @deprecated Use {@link ConventionalItemTags#SWORDS}
	 */
	@Deprecated
	private static final Identifier FABRIC_SWORDS = createFabricId("swords");

	public ItemTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateTags() {
		generateToolTags();
		generateBucketTags();
		generateOreAndRelatedTags();
		generateConsumableTags();
		generateGlassTags();
		generateShulkerTag();
		generateDyeTags();
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

	private void generateShulkerTag() {
		getOrCreateTagBuilder(ConventionalItemTags.SHULKER_BOXES)
				.add(Items.SHULKER_BOX)
				.add(Items.BLUE_SHULKER_BOX)
				.add(Items.BROWN_SHULKER_BOX)
				.add(Items.CYAN_SHULKER_BOX)
				.add(Items.GRAY_SHULKER_BOX)
				.add(Items.GREEN_SHULKER_BOX)
				.add(Items.LIGHT_BLUE_SHULKER_BOX)
				.add(Items.LIGHT_GRAY_SHULKER_BOX)
				.add(Items.LIME_SHULKER_BOX)
				.add(Items.MAGENTA_SHULKER_BOX)
				.add(Items.ORANGE_SHULKER_BOX)
				.add(Items.PINK_SHULKER_BOX)
				.add(Items.PURPLE_SHULKER_BOX)
				.add(Items.RED_SHULKER_BOX)
				.add(Items.WHITE_SHULKER_BOX)
				.add(Items.YELLOW_SHULKER_BOX)
				.add(Items.BLACK_SHULKER_BOX);
	}

	private void generateGlassTags() {
		getOrCreateTagBuilder(ConventionalItemTags.GLASS_BLOCKS)
				.add(Items.GLASS)
				.add(Items.GRAY_STAINED_GLASS)
				.add(Items.BLACK_STAINED_GLASS)
				.add(Items.ORANGE_STAINED_GLASS)
				.add(Items.BLUE_STAINED_GLASS)
				.add(Items.BROWN_STAINED_GLASS)
				.add(Items.CYAN_STAINED_GLASS)
				.add(Items.GREEN_STAINED_GLASS)
				.add(Items.LIGHT_BLUE_STAINED_GLASS)
				.add(Items.LIGHT_GRAY_STAINED_GLASS)
				.add(Items.LIME_STAINED_GLASS)
				.add(Items.MAGENTA_STAINED_GLASS)
				.add(Items.PINK_STAINED_GLASS)
				.add(Items.PURPLE_STAINED_GLASS)
				.add(Items.RED_STAINED_GLASS)
				.add(Items.TINTED_GLASS)
				.add(Items.WHITE_STAINED_GLASS)
				.add(Items.YELLOW_STAINED_GLASS);
		getOrCreateTagBuilder(ConventionalItemTags.GLASS_PANES)
				.add(Items.GLASS_PANE)
				.add(Items.GRAY_STAINED_GLASS_PANE)
				.add(Items.BLACK_STAINED_GLASS_PANE)
				.add(Items.ORANGE_STAINED_GLASS_PANE)
				.add(Items.BLUE_STAINED_GLASS_PANE)
				.add(Items.BROWN_STAINED_GLASS_PANE)
				.add(Items.CYAN_STAINED_GLASS_PANE)
				.add(Items.GREEN_STAINED_GLASS_PANE)
				.add(Items.LIGHT_BLUE_STAINED_GLASS_PANE)
				.add(Items.LIGHT_GRAY_STAINED_GLASS_PANE)
				.add(Items.LIME_STAINED_GLASS_PANE)
				.add(Items.MAGENTA_STAINED_GLASS_PANE)
				.add(Items.PINK_STAINED_GLASS_PANE)
				.add(Items.PURPLE_STAINED_GLASS_PANE)
				.add(Items.RED_STAINED_GLASS_PANE)
				.add(Items.WHITE_STAINED_GLASS_PANE)
				.add(Items.YELLOW_STAINED_GLASS_PANE);
	}

	private void generateConsumableTags() {
		Registry.ITEM.forEach(item -> {
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
		getOrCreateTagBuilder(ConventionalItemTags.WATER_BUCKETS)
				.add(Items.AXOLOTL_BUCKET)
				.add(Items.COD_BUCKET)
				.add(Items.PUFFERFISH_BUCKET)
				.add(Items.TROPICAL_FISH_BUCKET)
				.add(Items.SALMON_BUCKET)
				.add(Items.WATER_BUCKET);
		getOrCreateTagBuilder(ConventionalItemTags.MILK_BUCKETS)
				.add(Items.MILK_BUCKET);
	}

	private void generateOreAndRelatedTags() {
		getOrCreateTagBuilder(ConventionalItemTags.ORES)
				.addOptionalTag(ItemTags.IRON_ORES)
				.addOptionalTag(ItemTags.COPPER_ORES)
				.addOptionalTag(ItemTags.REDSTONE_ORES)
				.addOptionalTag(ItemTags.GOLD_ORES)
				.addOptionalTag(ItemTags.COAL_ORES)
				.addOptionalTag(ItemTags.DIAMOND_ORES)
				.addOptionalTag(ItemTags.LAPIS_ORES)
				.addOptionalTag(ConventionalItemTags.QUARTZ_ORES)
				.addOptionalTag(ItemTags.EMERALD_ORES);
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
		getOrCreateTagBuilder(ConventionalItemTags.SHEARS)
				.addOptionalTag(FABRIC_SHEARS)
				.add(Items.SHEARS);
		getOrCreateTagBuilder(ConventionalItemTags.SPEARS)
				.add(Items.TRIDENT);
		getOrCreateTagBuilder(ConventionalItemTags.BOWS)
				.add(Items.CROSSBOW)
				.add(Items.BOW);
	}

	private static Identifier createFabricId(String id) {
		return new Identifier("fabric", id);
	}
}
