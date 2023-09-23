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

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.tag.TagKey;

import net.fabricmc.fabric.impl.tag.convention.TagRegistration;

/**
 * See {@link net.minecraft.registry.tag.BlockTags} for vanilla tags.
 * Note that addition to some vanilla tags implies having certain functionality.
 */
public final class ConventionalBlockTags {
	private ConventionalBlockTags() {
	}

	// Ores and ingots - broad categories
	public static final TagKey<Block> ORES = register("ores");

	// Ores and ingots - vanilla instances
	public static final TagKey<Block> QUARTZ_ORES = register("ores/quartz");
	public static final TagKey<Block> NETHERITE_SCRAP_ORES = register("ores/netherite_scrap");

	public static final TagKey<Block> BARRELS = register("barrels");
	public static final TagKey<Block> WOODEN_BARRELS = register("barrels/wooden");
	public static final TagKey<Block> BOOKSHELVES = register("bookshelves");
	public static final TagKey<Block> CHESTS = register("chests");
	public static final TagKey<Block> GLASS_BLOCKS = register("glass_blocks");
	public static final TagKey<Block> GLASS_PANES = register("glass_panes");
	public static final TagKey<Block> SHULKER_BOXES = register("shulker_boxes");

	// Related to budding mechanics
	public static final TagKey<Block> BUDDING_BLOCKS = register("budding_blocks");
	public static final TagKey<Block> BUDS = register("buds");
	public static final TagKey<Block> CLUSTERS = register("clusters");

	public static final TagKey<Block> VILLAGER_JOB_SITES = register("villager_job_sites");

	// Sandstone
	public static final TagKey<Block> BLOCKS_SANDSTONE = register("sandstone/blocks");
	public static final TagKey<Block> SLABS_SANDSTONE = register("sandstone/slabs");
	public static final TagKey<Block> STAIRS_SANDSTONE = register("sandstone/stairs");
	public static final TagKey<Block> RED_BLOCKS_SANDSTONE = register("sandstone/red_blocks");
	public static final TagKey<Block> RED_SLABS_SANDSTONE = register("sandstone/red_slabs");
	public static final TagKey<Block> RED_STAIRS_SANDSTONE = register("sandstone/red_stairs");
	public static final TagKey<Block> UNCOLORED_BLOCKS_SANDSTONE = register("sandstone/uncolored_blocks");
	public static final TagKey<Block> UNCOLORED_SLABS_SANDSTONE = register("sandstone/uncolored_slabs");
	public static final TagKey<Block> UNCOLORED_STAIRS_SANDSTONE = register("sandstone/uncolored_stairs");

	// Blocks created with dyes
	public static final TagKey<Block> DYED_BLOCKS = register("dyed_blocks");
	public static final TagKey<Block> BLACK_DYED_BLOCKS = register("dyed_blocks/black");
	public static final TagKey<Block> BLUE_DYED_BLOCKS = register("dyed_blocks/blue");
	public static final TagKey<Block> BROWN_DYED_BLOCKS = register("dyed_blocks/brown");
	public static final TagKey<Block> CYAN_DYED_BLOCKS = register("dyed_blocks/cyan");
	public static final TagKey<Block> GRAY_DYED_BLOCKS = register("dyed_blocks/gray");
	public static final TagKey<Block> GREEN_DYED_BLOCKS = register("dyed_blocks/green");
	public static final TagKey<Block> LIGHT_BLUE_DYED_BLOCKS = register("dyed_blocks/light_blue");
	public static final TagKey<Block> LIGHT_GRAY_DYED_BLOCKS = register("dyed_blocks/light_gray");
	public static final TagKey<Block> LIME_DYED_BLOCKS = register("dyed_blocks/lime");
	public static final TagKey<Block> MAGENTA_DYED_BLOCKS = register("dyed_blocks/magenta");
	public static final TagKey<Block> ORANGE_DYED_BLOCKS = register("dyed_blocks/orange");
	public static final TagKey<Block> PINK_DYED_BLOCKS = register("dyed_blocks/pink");
	public static final TagKey<Block> PURPLE_DYED_BLOCKS = register("dyed_blocks/purple");
	public static final TagKey<Block> RED_DYED_BLOCKS = register("dyed_blocks/red");
	public static final TagKey<Block> WHITE_DYED_BLOCKS = register("dyed_blocks/white");
	public static final TagKey<Block> YELLOW_DYED_BLOCKS = register("dyed_blocks/yellow");

	/**
	 * Blocks should be included in this tag if their movement can cause serious issues such as world corruption
	 * upon being moved, such as chunk loaders or pipes,
	 * for mods that move blocks but do not respect {@link AbstractBlock.AbstractBlockState#getPistonBehavior}.
	 */
	public static final TagKey<Block> RELOCATION_NOT_SUPPORTED = register("relocation_not_supported");

	private static TagKey<Block> register(String tagID) {
		return TagRegistration.BLOCK_TAG_REGISTRATION.registerC(tagID);
	}
}
