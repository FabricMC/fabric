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

package net.fabricmc.fabric.api.tag.convention.v1;

import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

import net.fabricmc.fabric.impl.tag.convention.TagRegistration;

/**
 * See {@link net.minecraft.registry.tag.ItemTags} for vanilla tags.
 * Note that addition to some vanilla tags implies having certain functionality.
 */
public final class ConventionalItemTags {
	private ConventionalItemTags() {
	}

	// Tool tags
	public static final TagKey<Item> TOOLS = register("tools");
	public static final TagKey<Item> TOOLS_SHEARS = register("tools/shears");
	/**
	 * For throwable weapons, like Minecraft tridents.
	 */
	public static final TagKey<Item> TOOLS_SPEARS = register("tools/spears");
	public static final TagKey<Item> TOOLS_BOWS = register("tools/bows");
	public static final TagKey<Item> TOOLS_CROSSBOWS = register("tools/crossbows");
	public static final TagKey<Item> TOOLS_SHIELDS = register("tools/shields");
	public static final TagKey<Item> TOOLS_FISHING_RODS = register("tools/fishing_rods");

	// Ores and ingots - categories
	public static final TagKey<Item> DUSTS = register("dusts");
	public static final TagKey<Item> GEMS = register("gems");
	public static final TagKey<Item> INGOTS = register("ingots");
	public static final TagKey<Item> NUGGETS = register("nuggets");
	public static final TagKey<Item> ORES = register("ores");
	public static final TagKey<Item> RAW_MATERIALS = register("raw_materials");
	public static final TagKey<Item> RAW_BLOCKS = register("raw_blocks");

	// Raw material and blocks - vanilla instances
	public static final TagKey<Item> RAW_MATERIALS_IRON = register("raw_materials/iron");
	public static final TagKey<Item> RAW_MATERIALS_GOLD = register("raw_materials/gold");
	public static final TagKey<Item> RAW_MATERIALS_COPPER = register("raw_materials/copper");
	public static final TagKey<Item> RAW_BLOCKS_IRON = register("raw_blocks/iron");
	public static final TagKey<Item> RAW_BLOCKS_GOLD = register("raw_blocks/gold");
	public static final TagKey<Item> RAW_BLOCKS_COPPER = register("raw_blocks/copper");

	// Ingots - vanilla instances
	public static final TagKey<Item> INGOTS_IRON = register("ingots/iron");
	public static final TagKey<Item> INGOTS_GOLD = register("ingots/gold");
	public static final TagKey<Item> INGOTS_COPPER = register("ingots/copper");
	public static final TagKey<Item> INGOTS_NETHERITE = register("ingots/netherite");

	// Ores - vanilla instances
	public static final TagKey<Item> ORES_NETHERITE_SCRAP = register("ores/netherite_scrap");
	public static final TagKey<Item> ORES_QUARTZ = register("ores/quartz");

	// Gems - vanilla instances
	public static final TagKey<Item> GEMS_QUARTZ = register("gems/quartz");
	public static final TagKey<Item> GEMS_LAPIS = register("gems/lapis");
	public static final TagKey<Item> GEMS_DIAMOND = register("gems/diamond");
	public static final TagKey<Item> GEMS_AMETHYST = register("gems/amethyst");
	public static final TagKey<Item> GEMS_EMERALD = register("gems/emerald");
	public static final TagKey<Item> GEMS_PRISMARINE = register("gems/prismarine");

	// Dusts and Misc - vanilla instances
	public static final TagKey<Item> DUSTS_REDSTONE = register("dusts/redstone");
	public static final TagKey<Item> DUSTS_GLOWSTONE = register("dusts/glowstone");
	public static final TagKey<Item> COAL = register("coal");

	// Consumables
	public static final TagKey<Item> FOODS = register("foods");
	public static final TagKey<Item> POTIONS = register("potions");
	// Buckets
	/**
	 * Does not include entity water buckets.
	 */
	public static final TagKey<Item> BUCKETS_WATER = register("buckets/water");
	public static final TagKey<Item> BUCKETS_ENTITY_WATER = register("buckets/entity_water");
	public static final TagKey<Item> BUCKETS_LAVA = register("buckets/lava");
	public static final TagKey<Item> BUCKETS_MILK = register("buckets/milk");
	public static final TagKey<Item> BUCKETS_EMPTY = register("buckets/empty");

	public static final TagKey<Item> BARRELS = register("barrels");
	public static final TagKey<Item> BARRELS_WOODEN = register("barrels/wooden");
	public static final TagKey<Item> BOOKSHELVES = register("bookshelves");
	public static final TagKey<Item> CHESTS = register("chests");
	public static final TagKey<Item> GLASS_BLOCKS = register("glass_blocks");
	public static final TagKey<Item> GLASS_PANES = register("glass_panes");
	public static final TagKey<Item> SHULKER_BOXES = register("shulker_boxes");

	// Related to budding mechanics
	public static final TagKey<Item> BUDDING_BLOCKS = register("budding_blocks");
	public static final TagKey<Item> BUDS = register("buds");
	public static final TagKey<Item> CLUSTERS = register("clusters");

	public static final TagKey<Item> VILLAGER_JOB_SITES = register("villager_job_sites");

	// Sandstone
	public static final TagKey<Item> SANDSTONE_BLOCKS = register("sandstone/blocks");
	public static final TagKey<Item> SANDSTONE_SLABS = register("sandstone/slabs");
	public static final TagKey<Item> SANDSTONE_STAIRS = register("sandstone/stairs");
	public static final TagKey<Item> RED_SANDSTONE_BLOCKS = register("sandstone/red_blocks");
	public static final TagKey<Item> RED_SANDSTONE_SLABS = register("sandstone/red_slabs");
	public static final TagKey<Item> RED_SANDSTONE_STAIRS = register("sandstone/red_stairs");
	public static final TagKey<Item> UNCOLORED_SANDSTONE_BLOCKS = register("sandstone/uncolored_blocks");
	public static final TagKey<Item> UNCOLORED_SANDSTONE_SLABS = register("sandstone/uncolored_slabs");
	public static final TagKey<Item> UNCOLORED_SANDSTONE_STAIRS = register("sandstone/uncolored_stairs");

	// Dyes
	public static final TagKey<Item> DYES = register("dyes");
	public static final TagKey<Item> DYES_BLACK = register("dyes/black");
	public static final TagKey<Item> DYES_BLUE = register("dyes/blue");
	public static final TagKey<Item> DYES_BROWN = register("dyes/brown");
	public static final TagKey<Item> DYES_CYAN = register("dyes/cyan");
	public static final TagKey<Item> DYES_GRAY = register("dyes/gray");
	public static final TagKey<Item> DYES_GREEN = register("dyes/green");
	public static final TagKey<Item> DYES_LIGHT_BLUE = register("dyes/light_blue");
	public static final TagKey<Item> DYES_LIGHT_GRAY = register("dyes/light_gray");
	public static final TagKey<Item> DYES_LIME = register("dyes/lime");
	public static final TagKey<Item> DYES_MAGENTA = register("dyes/magenta");
	public static final TagKey<Item> DYES_ORANGE = register("dyes/orange");
	public static final TagKey<Item> DYES_PINK = register("dyes/pink");
	public static final TagKey<Item> DYES_PURPLE = register("dyes/purple");
	public static final TagKey<Item> DYES_RED = register("dyes/red");
	public static final TagKey<Item> DYES_WHITE = register("dyes/white");
	public static final TagKey<Item> DYES_YELLOW = register("dyes/yellow");

	// Items created with dyes
	public static final TagKey<Item> DYED_ITEMS = register("dyed_items");
	public static final TagKey<Item> DYED_ITEMS_BLACK = register("dyed_items/black");
	public static final TagKey<Item> DYED_ITEMS_BLUE = register("dyed_items/blue");
	public static final TagKey<Item> DYED_ITEMS_BROWN = register("dyed_items/brown");
	public static final TagKey<Item> DYED_ITEMS_CYAN = register("dyed_items/cyan");
	public static final TagKey<Item> DYED_ITEMS_GRAY = register("dyed_items/gray");
	public static final TagKey<Item> DYED_ITEMS_GREEN = register("dyed_items/green");
	public static final TagKey<Item> DYED_ITEMS_LIGHT_BLUE = register("dyed_items/light_blue");
	public static final TagKey<Item> DYED_ITEMS_LIGHT_GRAY = register("dyed_items/light_gray");
	public static final TagKey<Item> DYED_ITEMS_LIME = register("dyed_items/lime");
	public static final TagKey<Item> DYED_ITEMS_MAGENTA = register("dyed_items/magenta");
	public static final TagKey<Item> DYED_ITEMS_ORANGE = register("dyed_items/orange");
	public static final TagKey<Item> DYED_ITEMS_PINK = register("dyed_items/pink");
	public static final TagKey<Item> DYED_ITEMS_PURPLE = register("dyed_items/purple");
	public static final TagKey<Item> DYED_ITEMS_RED = register("dyed_items/red");
	public static final TagKey<Item> DYED_ITEMS_WHITE = register("dyed_items/white");
	public static final TagKey<Item> DYED_ITEMS_YELLOW = register("dyed_items/yellow");

	private static TagKey<Item> register(String tagID) {
		return TagRegistration.ITEM_TAG_REGISTRATION.registerCommon(tagID);
	}
}
