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

package net.fabricmc.fabric.api.tag.v1;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.TagKey;

import net.fabricmc.fabric.impl.tag.common.TagRegistration;

/**
 * See {@link net.minecraft.tag.BlockTags} for vanilla tags.
 * Note that addition to some vanilla tags implies having certain functionality.
 */
public class CommonBlockTags {
	public static final TagKey<Block> IRON_ORES = register("iron_ores");
	public static final TagKey<Block> GOLD_ORES = register("gold_ores");
	public static final TagKey<Block> REDSTONE_ORES = register("redstone_ores");
	public static final TagKey<Block> COPPER_ORES = register("copper_ores");
	public static final TagKey<Block> QUARTZ_ORES = register("quartz_ores");
	public static final TagKey<Block> LAPIS_ORES = register("lapis_ores");
	public static final TagKey<Block> DIAMOND_ORES = register("diamond_ores");
	public static final TagKey<Block> EMERALD_ORES = register("emerald_ores");
	public static final TagKey<Block> COAL_ORES = register("coal_ores");
	public static final TagKey<Block> ORES = register("ores");
	public static final TagKey<Block> NETHERITE_ORES = register("netherite_ores");
	public static final TagKey<Block> CHESTS = register("chests");
	public static final TagKey<Block> BOOKSHELVES = register("bookshelves");
	public static final TagKey<Block> GLASS_BLOCKS = register("glass_blocks");
	public static final TagKey<Block> GLASS_PANES = register("glass_panes");
	/**
	 * Blocks should be included in this tag if their movement can cause serious issues such as world corruption
	 * upon being moved, such as chunk loaders or pipes,
	 * for mods that move blocks but do not respect {@link AbstractBlock.AbstractBlockState#getPistonBehavior}.
	 */
	public static final TagKey<Block> MOVEMENT_RESTRICTED = register("movement_restricted");

	private static TagKey<Block> register(String tagID) {
		return TagRegistration.BLOCK_TAG_REGISTRATION.registerCommon(tagID);
	}

	private static TagKey<Block> registerFabric(String tagID) {
		return TagRegistration.BLOCK_TAG_REGISTRATION.registerFabric(tagID);
	}
}
