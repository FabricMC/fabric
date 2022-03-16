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

import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.v1.CommonItemTags;

public class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
	/**
	 * @deprecated Use {@link CommonItemTags#PICKAXES}
	 */
	@Deprecated
	private static final Identifier FABRIC_PICKAXES = createFabricId("pickaxes");
	/**
	 * @deprecated Use {@link CommonItemTags#SHOVELS}
	 */
	@Deprecated
	private static final Identifier FABRIC_SHOVELS = createFabricId("shovels");
	/**
	 * @deprecated Use {@link CommonItemTags#HOES}
	 */
	@Deprecated
	private static final Identifier FABRIC_HOES = createFabricId("hoes");
	/**
	 * @deprecated Use {@link CommonItemTags#AXES}
	 */
	@Deprecated
	private static final Identifier FABRIC_AXES = createFabricId("axes");
	/**
	 * @deprecated Use {@link CommonItemTags#SHEARS}
	 */
	@Deprecated
	private static final Identifier FABRIC_SHEARS = createFabricId("shears");
	/**
	 * @deprecated Use {@link CommonItemTags#SWORDS}
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
		getOrCreateTagBuilder(CommonItemTags.DYES)
				.addOptionalTag(CommonItemTags.BLACK_DYES)
				.addOptionalTag(CommonItemTags.BLUE_DYES)
				.addOptionalTag(CommonItemTags.BROWN_DYES)
				.addOptionalTag(CommonItemTags.GREEN_DYES)
				.addOptionalTag(CommonItemTags.RED_DYES)
				.addOptionalTag(CommonItemTags.WHITE_DYES)
				.addOptionalTag(CommonItemTags.YELLOW_DYES)
				.addOptionalTag(CommonItemTags.LIGHT_GRAY_DYES)
				.addOptionalTag(CommonItemTags.LIGHT_BLUE_DYES)
				.addOptionalTag(CommonItemTags.LIME_DYES)
				.addOptionalTag(CommonItemTags.MAGENTA_DYES)
				.addOptionalTag(CommonItemTags.ORANGE_DYES)
				.addOptionalTag(CommonItemTags.PINK_DYES)
				.addOptionalTag(CommonItemTags.CYAN_DYES)
				.addOptionalTag(CommonItemTags.GRAY_DYES)
				.addOptionalTag(CommonItemTags.PURPLE_DYES);
		getOrCreateTagBuilder(CommonItemTags.BLACK_DYES)
				.add(Items.BLACK_DYE);
		getOrCreateTagBuilder(CommonItemTags.BLUE_DYES)
				.add(Items.BLUE_DYE);
		getOrCreateTagBuilder(CommonItemTags.BROWN_DYES)
				.add(Items.PURPLE_DYE);
		getOrCreateTagBuilder(CommonItemTags.GREEN_DYES)
				.add(Items.GREEN_DYE);
		getOrCreateTagBuilder(CommonItemTags.RED_DYES)
				.add(Items.RED_DYE);
		getOrCreateTagBuilder(CommonItemTags.WHITE_DYES)
				.add(Items.WHITE_DYE);
		getOrCreateTagBuilder(CommonItemTags.YELLOW_DYES)
				.add(Items.YELLOW_DYE);
		getOrCreateTagBuilder(CommonItemTags.LIGHT_BLUE_DYES)
				.add(Items.LIGHT_BLUE_DYE);
		getOrCreateTagBuilder(CommonItemTags.LIGHT_GRAY_DYES)
				.add(Items.LIGHT_GRAY_DYE);
		getOrCreateTagBuilder(CommonItemTags.LIME_DYES)
				.add(Items.LIME_DYE);
		getOrCreateTagBuilder(CommonItemTags.MAGENTA_DYES)
				.add(Items.MAGENTA_DYE);
		getOrCreateTagBuilder(CommonItemTags.ORANGE_DYES)
				.add(Items.ORANGE_DYE);
		getOrCreateTagBuilder(CommonItemTags.PINK_DYES)
				.add(Items.PINK_DYE);
		getOrCreateTagBuilder(CommonItemTags.CYAN_DYES)
				.add(Items.CYAN_DYE);
		getOrCreateTagBuilder(CommonItemTags.GRAY_DYES)
				.add(Items.GRAY_DYE);
		getOrCreateTagBuilder(CommonItemTags.PURPLE_DYES)
				.add(Items.PURPLE_DYE);
	}

	private void generateShulkerTag() {
		getOrCreateTagBuilder(CommonItemTags.SHULKER_BOXES)
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
		getOrCreateTagBuilder(CommonItemTags.GLASS_BLOCKS)
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
		getOrCreateTagBuilder(CommonItemTags.GLASS_PANES)
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
		getOrCreateTagBuilder(CommonItemTags.FOODS)
				.add(Items.BEEF)
				.add(Items.COOKED_BEEF)
				.add(Items.APPLE)
				.add(Items.BAKED_POTATO)
				.add(Items.BEETROOT)
				.add(Items.BEETROOT_SOUP)
				.add(Items.BREAD)
				.add(Items.CARROT)
				.add(Items.CHORUS_FRUIT)
				.add(Items.COOKED_CHICKEN)
				.add(Items.COOKED_COD)
				.add(Items.COOKED_MUTTON)
				.add(Items.COOKED_PORKCHOP)
				.add(Items.COOKED_RABBIT)
				.add(Items.COOKED_SALMON)
				.add(Items.COOKIE)
				.add(Items.DRIED_KELP)
				.add(Items.ENCHANTED_GOLDEN_APPLE)
				.add(Items.GOLDEN_APPLE)
				.add(Items.GOLDEN_CARROT)
				.add(Items.GLOW_BERRIES)
				.add(Items.HONEY_BOTTLE)
				.add(Items.MELON_SLICE)
				.add(Items.MUSHROOM_STEW)
				.add(Items.POISONOUS_POTATO)
				.add(Items.POTATO)
				.add(Items.PUFFERFISH)
				.add(Items.PUMPKIN_PIE)
				.add(Items.RABBIT_STEW)
				.add(Items.CHICKEN)
				.add(Items.COD)
				.add(Items.MUTTON)
				.add(Items.PORKCHOP)
				.add(Items.RABBIT)
				.add(Items.SALMON)
				.add(Items.ROTTEN_FLESH)
				.add(Items.SPIDER_EYE)
				.add(Items.SUSPICIOUS_STEW)
				.add(Items.SWEET_BERRIES)
				.add(Items.TROPICAL_FISH);
		getOrCreateTagBuilder(CommonItemTags.POTIONS)
				.add(Items.LINGERING_POTION)
				.add(Items.SPLASH_POTION)
				.add(Items.POTION);
	}

	private void generateBucketTags() {
		getOrCreateTagBuilder(CommonItemTags.EMPTY_BUCKET)
				.add(Items.BUCKET);
		getOrCreateTagBuilder(CommonItemTags.LAVA_BUCKET)
				.add(Items.LAVA_BUCKET);
		getOrCreateTagBuilder(CommonItemTags.WATER_BUCKET)
				.add(Items.AXOLOTL_BUCKET)
				.add(Items.COD_BUCKET)
				.add(Items.PUFFERFISH_BUCKET)
				.add(Items.TROPICAL_FISH_BUCKET)
				.add(Items.SALMON_BUCKET)
				.add(Items.WATER_BUCKET);
		getOrCreateTagBuilder(CommonItemTags.MILK_BUCKET)
				.add(Items.MILK_BUCKET);
	}

	private void generateOreAndRelatedTags() {
		getOrCreateTagBuilder(CommonItemTags.IRON_ORES)
				.addOptionalTag(ItemTags.IRON_ORES);
		getOrCreateTagBuilder(CommonItemTags.GOLD_ORES)
				.addOptionalTag(ItemTags.GOLD_ORES);
		getOrCreateTagBuilder(CommonItemTags.REDSTONE_ORES)
				.addOptionalTag(ItemTags.REDSTONE_ORES);
		getOrCreateTagBuilder(CommonItemTags.COPPER_ORES)
				.addOptionalTag(ItemTags.COPPER_ORES);
		getOrCreateTagBuilder(CommonItemTags.NETHERITE_ORES);
		getOrCreateTagBuilder(CommonItemTags.ORES)
				.addOptionalTag(CommonItemTags.NETHERITE_ORES)
				.addOptionalTag(CommonItemTags.IRON_ORES)
				.addOptionalTag(CommonItemTags.COPPER_ORES)
				.addOptionalTag(CommonItemTags.REDSTONE_ORES)
				.addOptionalTag(CommonItemTags.GOLD_ORES)
				.addOptionalTag(CommonItemTags.COAL_ORES)
				.addOptionalTag(CommonItemTags.DIAMOND_ORES)
				.addOptionalTag(CommonItemTags.LAPIS_ORES)
				.addOptionalTag(CommonItemTags.QUARTZ_ORES)
				.addOptionalTag(CommonItemTags.EMERALD_ORES);
		getOrCreateTagBuilder(CommonItemTags.IRON_INGOTS)
				.add(Items.IRON_INGOT);
		getOrCreateTagBuilder(CommonItemTags.COPPER_INGOTS)
				.add(Items.COPPER_INGOT);
		getOrCreateTagBuilder(CommonItemTags.GOLD_INGOTS)
				.add(Items.GOLD_INGOT);
		getOrCreateTagBuilder(CommonItemTags.NETHERITE_INGOTS)
				.add(Items.NETHERITE_INGOT);
		getOrCreateTagBuilder(CommonItemTags.REDSTONE_DUSTS)
				.add(Items.REDSTONE);
		getOrCreateTagBuilder(CommonItemTags.QUARTZ_ORES)
				.add(Items.NETHER_QUARTZ_ORE);
		getOrCreateTagBuilder(CommonItemTags.QUARTZ)
				.add(Items.QUARTZ);
		getOrCreateTagBuilder(CommonItemTags.RAW_IRON_ORES)
				.add(Items.RAW_IRON);
		getOrCreateTagBuilder(CommonItemTags.RAW_COPPER_ORES)
				.add(Items.RAW_COPPER);
		getOrCreateTagBuilder(CommonItemTags.RAW_GOLD_ORES)
				.add(Items.RAW_GOLD);
		getOrCreateTagBuilder(CommonItemTags.RAW_IRON_BLOCKS)
				.add(Items.RAW_IRON_BLOCK);
		getOrCreateTagBuilder(CommonItemTags.RAW_COPPER_BLOCKS)
				.add(Items.RAW_COPPER_BLOCK);
		getOrCreateTagBuilder(CommonItemTags.RAW_GOLD_BLOCKS)
				.add(Items.RAW_GOLD_BLOCK);
		getOrCreateTagBuilder(CommonItemTags.COAL_ORES)
				.addOptionalTag(ItemTags.COAL_ORES);
		getOrCreateTagBuilder(CommonItemTags.COAL)
				.addOptionalTag(ItemTags.COALS);
		getOrCreateTagBuilder(CommonItemTags.EMERALD_ORES)
				.addOptionalTag(ItemTags.EMERALD_ORES);
		getOrCreateTagBuilder(CommonItemTags.EMERALDS)
				.add(Items.EMERALD);
		getOrCreateTagBuilder(CommonItemTags.LAPIS_ORES)
				.addOptionalTag(ItemTags.LAPIS_ORES);
		getOrCreateTagBuilder(CommonItemTags.LAPIS)
				.add(Items.LAPIS_LAZULI);
		getOrCreateTagBuilder(CommonItemTags.DIAMOND_ORES)
				.addOptionalTag(ItemTags.DIAMOND_ORES);
		getOrCreateTagBuilder(CommonItemTags.DIAMONDS)
				.add(Items.DIAMOND);
	}

	private void generateToolTags() {
		getOrCreateTagBuilder(CommonItemTags.AXES)
				.addOptionalTag(FABRIC_AXES)
				.add(Items.DIAMOND_AXE)
				.add(Items.GOLDEN_AXE)
				.add(Items.WOODEN_AXE)
				.add(Items.STONE_AXE)
				.add(Items.IRON_AXE)
				.add(Items.NETHERITE_AXE);
		getOrCreateTagBuilder(CommonItemTags.PICKAXES)
				.addOptionalTag(FABRIC_PICKAXES)
				.add(Items.DIAMOND_PICKAXE)
				.add(Items.GOLDEN_PICKAXE)
				.add(Items.WOODEN_PICKAXE)
				.add(Items.STONE_PICKAXE)
				.add(Items.IRON_PICKAXE)
				.add(Items.NETHERITE_PICKAXE);
		getOrCreateTagBuilder(CommonItemTags.HOES)
				.addOptionalTag(FABRIC_HOES)
				.add(Items.DIAMOND_HOE)
				.add(Items.GOLDEN_HOE)
				.add(Items.WOODEN_HOE)
				.add(Items.STONE_HOE)
				.add(Items.IRON_HOE)
				.add(Items.NETHERITE_HOE);
		getOrCreateTagBuilder(CommonItemTags.SWORDS)
				.addOptionalTag(FABRIC_SWORDS)
				.add(Items.DIAMOND_SWORD)
				.add(Items.GOLDEN_SWORD)
				.add(Items.WOODEN_SWORD)
				.add(Items.STONE_SWORD)
				.add(Items.IRON_SWORD)
				.add(Items.NETHERITE_SWORD);
		getOrCreateTagBuilder(CommonItemTags.SHOVELS)
				.addOptionalTag(FABRIC_SHOVELS)
				.add(Items.DIAMOND_SHOVEL)
				.add(Items.GOLDEN_SHOVEL)
				.add(Items.WOODEN_SHOVEL)
				.add(Items.STONE_SHOVEL)
				.add(Items.IRON_SHOVEL)
				.add(Items.NETHERITE_SHOVEL);
		getOrCreateTagBuilder(CommonItemTags.SHEARS)
				.addOptionalTag(FABRIC_SHEARS)
				.add(Items.SHEARS);
		getOrCreateTagBuilder(CommonItemTags.SPEARS)
				.add(Items.TRIDENT);
		getOrCreateTagBuilder(CommonItemTags.BOWS)
				.add(Items.CROSSBOW)
				.add(Items.BOW);
	}

	private static Identifier createFabricId(String id) {
		return new Identifier("fabric", id);
	}
}
