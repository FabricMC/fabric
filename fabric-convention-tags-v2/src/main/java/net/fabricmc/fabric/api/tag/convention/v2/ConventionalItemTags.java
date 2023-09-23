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

package net.fabricmc.fabric.api.tag.convention.v2;

import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

import net.fabricmc.fabric.impl.tag.convention.v2.TagRegistration;

/**
 * See {@link net.minecraft.registry.tag.ItemTags} for vanilla tags.
 * Note that addition to some vanilla tags implies having certain functionality.
 */
public final class ConventionalItemTags {
	private ConventionalItemTags() {
	}

	/**
	 * Natural stone-like blocks that can spawn in-world such as Stone, Granite, Diorite, and others.
	 * Not variations like Stone Bricks or Diorite Slabs.
	 */
	public static final TagKey<Item> STONES = register("stones");

	// Tool tags
	public static final TagKey<Item> TOOLS = register("tools");
	public static final TagKey<Item> SHEARS_TOOLS = register("tools/shears");
	/**
	 * For throwable spear weapons, like Minecraft's tridents.
	 * Note, other throwable weapons like boomerangs and throwing knives are best put into their own tools tag.
	 */
	public static final TagKey<Item> SPEARS_TOOLS = register("tools/spears");
	public static final TagKey<Item> BOWS_TOOLS = register("tools/bows");
	public static final TagKey<Item> CROSSBOWS_TOOLS = register("tools/crossbows");
	public static final TagKey<Item> SHIELDS_TOOLS = register("tools/shields");
	public static final TagKey<Item> FISHING_RODS_TOOLS = register("tools/fishing_rods");

	// Ores and ingots - categories
	public static final TagKey<Item> DUSTS = register("dusts");
	public static final TagKey<Item> GEMS = register("gems");
	public static final TagKey<Item> INGOTS = register("ingots");
	public static final TagKey<Item> NUGGETS = register("nuggets");
	public static final TagKey<Item> ORES = register("ores");
	public static final TagKey<Item> RAW_MATERIALS = register("raw_materials");
	public static final TagKey<Item> RAW_BLOCKS = register("raw_blocks");

	// Raw material and blocks - vanilla instances
	public static final TagKey<Item> IRON_RAW_MATERIALS = register("raw_materials/iron");
	public static final TagKey<Item> GOLD_RAW_MATERIALS = register("raw_materials/gold");
	public static final TagKey<Item> COPPER_RAW_MATERIALS = register("raw_materials/copper");
	public static final TagKey<Item> IRON_RAW_BLOCKS = register("raw_blocks/iron");
	public static final TagKey<Item> GOLD_RAW_BLOCKS = register("raw_blocks/gold");
	public static final TagKey<Item> COPPER_RAW_BLOCKS = register("raw_blocks/copper");

	// Ingots - vanilla instances
	public static final TagKey<Item> IRON_INGOTS = register("ingots/iron");
	public static final TagKey<Item> GOLD_INGOTS = register("ingots/gold");
	public static final TagKey<Item> COPPER_INGOTS = register("ingots/copper");
	public static final TagKey<Item> NETHERITE_INGOTS = register("ingots/netherite");

	// Ores - vanilla instances
	public static final TagKey<Item> NETHERITE_SCRAP_ORES = register("ores/netherite_scrap");
	public static final TagKey<Item> QUARTZ_ORES = register("ores/quartz");

	// Gems - vanilla instances
	public static final TagKey<Item> QUARTZ_GEMS = register("gems/quartz");
	public static final TagKey<Item> LAPIS_GEMS = register("gems/lapis");
	public static final TagKey<Item> DIAMOND_GEMS = register("gems/diamond");
	public static final TagKey<Item> AMETHYST_GEMS = register("gems/amethyst");
	public static final TagKey<Item> EMERALD_GEMS = register("gems/emerald");
	public static final TagKey<Item> PRISMARINE_GEMS = register("gems/prismarine");

	// Dusts and Misc - vanilla instances
	public static final TagKey<Item> REDSTONE_DUSTS = register("dusts/redstone");
	public static final TagKey<Item> GLOWSTONE_DUSTS = register("dusts/glowstone");
	public static final TagKey<Item> COAL = register("coal");

	// Consumables
	public static final TagKey<Item> FOODS = register("foods");
	public static final TagKey<Item> POTIONS = register("potions");
	// Buckets
	/**
	 * Does not include entity water buckets.
	 */
	public static final TagKey<Item> WATER_BUCKETS = register("buckets/water");
	public static final TagKey<Item> ENTITY_WATER_BUCKETS = register("buckets/entity_water");
	public static final TagKey<Item> LAVA_BUCKETS = register("buckets/lava");
	public static final TagKey<Item> MILK_BUCKETS = register("buckets/milk");
	public static final TagKey<Item> EMPTY_BUCKETS = register("buckets/empty");

	public static final TagKey<Item> BARRELS = register("barrels");
	public static final TagKey<Item> WOODEN_BARRELS = register("barrels/wooden");
	public static final TagKey<Item> BOOKSHELVES = register("bookshelves");
	public static final TagKey<Item> CHESTS = register("chests");
	public static final TagKey<Item> WOODEN_CHESTS = register("chests/wooden");
	public static final TagKey<Item> GLASS_BLOCKS = register("glass_blocks");
	public static final TagKey<Item> GLASS_PANES = register("glass_panes");
	public static final TagKey<Item> SHULKER_BOXES = register("shulker_boxes");

	// Related to budding mechanics
	public static final TagKey<Item> BUDDING_BLOCKS = register("budding_blocks");
	public static final TagKey<Item> BUDS = register("buds");
	public static final TagKey<Item> CLUSTERS = register("clusters");

	public static final TagKey<Item> VILLAGER_JOB_SITES = register("villager_job_sites");

	// Sandstone
	public static final TagKey<Item> BLOCKS_SANDSTONE = register("sandstone/blocks");
	public static final TagKey<Item> SLABS_SANDSTONE = register("sandstone/slabs");
	public static final TagKey<Item> STAIRS_SANDSTONE = register("sandstone/stairs");
	public static final TagKey<Item> RED_BLOCKS_SANDSTONE = register("sandstone/red_blocks");
	public static final TagKey<Item> RED_SLABS_SANDSTONE = register("sandstone/red_slabs");
	public static final TagKey<Item> RED_STAIRS_SANDSTONE = register("sandstone/red_stairs");
	public static final TagKey<Item> UNCOLORED_BLOCKS_SANDSTONE = register("sandstone/uncolored_blocks");
	public static final TagKey<Item> UNCOLORED_SLABS_SANDSTONE = register("sandstone/uncolored_slabs");
	public static final TagKey<Item> UNCOLORED_STAIRS_SANDSTONE = register("sandstone/uncolored_stairs");

	// Dyes
	public static final TagKey<Item> DYES = register("dyes");
	public static final TagKey<Item> BLACK_DYES = register("dyes/black");
	public static final TagKey<Item> BLUE_DYES = register("dyes/blue");
	public static final TagKey<Item> BROWN_DYES = register("dyes/brown");
	public static final TagKey<Item> CYAN_DYES = register("dyes/cyan");
	public static final TagKey<Item> GRAY_DYES = register("dyes/gray");
	public static final TagKey<Item> GREEN_DYES = register("dyes/green");
	public static final TagKey<Item> LIGHT_BLUE_DYES = register("dyes/light_blue");
	public static final TagKey<Item> LIGHT_GRAY_DYES = register("dyes/light_gray");
	public static final TagKey<Item> LIME_DYES = register("dyes/lime");
	public static final TagKey<Item> MAGENTA_DYES = register("dyes/magenta");
	public static final TagKey<Item> ORANGE_DYES = register("dyes/orange");
	public static final TagKey<Item> PINK_DYES = register("dyes/pink");
	public static final TagKey<Item> PURPLE_DYES = register("dyes/purple");
	public static final TagKey<Item> RED_DYES = register("dyes/red");
	public static final TagKey<Item> WHITE_DYES = register("dyes/white");
	public static final TagKey<Item> YELLOW_DYES = register("dyes/yellow");

	// Items created with dyes
	public static final TagKey<Item> DYED = register("dyed");
	public static final TagKey<Item> BLACK_DYED = register("dyed/black");
	public static final TagKey<Item> BLUE_DYED = register("dyed/blue");
	public static final TagKey<Item> BROWN_DYED = register("dyed/brown");
	public static final TagKey<Item> CYAN_DYED = register("dyed/cyan");
	public static final TagKey<Item> GRAY_DYED = register("dyed/gray");
	public static final TagKey<Item> GREEN_DYED = register("dyed/green");
	public static final TagKey<Item> LIGHT_BLUE_DYED = register("dyed/light_blue");
	public static final TagKey<Item> LIGHT_GRAY_DYED = register("dyed/light_gray");
	public static final TagKey<Item> LIME_DYED = register("dyed/lime");
	public static final TagKey<Item> MAGENTA_DYED = register("dyed/magenta");
	public static final TagKey<Item> ORANGE_DYED = register("dyed/orange");
	public static final TagKey<Item> PINK_DYED = register("dyed/pink");
	public static final TagKey<Item> PURPLE_DYED = register("dyed/purple");
	public static final TagKey<Item> RED_DYED = register("dyed/red");
	public static final TagKey<Item> WHITE_DYED = register("dyed/white");
	public static final TagKey<Item> YELLOW_DYED = register("dyed/yellow");

	// Other
	public static final TagKey<Item> STRINGS = register("strings");
	public static final TagKey<Item> RODS = register("rods");
	public static final TagKey<Item> WOODEN_RODS = register("rods/wooden");

	private static TagKey<Item> register(String tagID) {
		return TagRegistration.ITEM_TAG_REGISTRATION.registerC(tagID);
	}
}
