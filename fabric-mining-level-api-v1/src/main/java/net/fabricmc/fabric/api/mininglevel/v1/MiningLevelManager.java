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

package net.fabricmc.fabric.api.mininglevel.v1;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.impl.mininglevel.MiningLevelManagerImpl;

/**
 * Provides access to block mining levels.
 *
 * <h2>Mining level tags</h2>
 * {@code MiningLevelManager} supports the vanilla minimum mining level tags:
 * {@link net.minecraft.tag.BlockTags#NEEDS_STONE_TOOL #needs_stone_tool},
 * {@link net.minecraft.tag.BlockTags#NEEDS_IRON_TOOL #needs_iron_tool} and
 * {@link net.minecraft.tag.BlockTags#NEEDS_DIAMOND_TOOL #needs_diamond_tool}.
 * In addition to them, you can use dynamic mining level tags for any mining level (such as wood, netherite
 * or a custom one). The dynamic tags are checked automatically.
 *
 * <p>Dynamic mining level tags are in the format {@code #fabric:needs_tool_level_N}, where {@code N}
 * is the wanted tool level as an integer. For example, a mining level tag for netherite (mining level 4) would be
 * {@code #fabric:needs_tool_level_4}.
 */
public final class MiningLevelManager {
	private MiningLevelManager() {
	}

	/**
	 * Gets the tool mining level required to effectively mine and drop a block state.
	 *
	 * <p>Note: this method does not take into account tool-specific mining levels declared
	 * with the tool attribute API.
	 *
	 * <p>The default mining level of blocks not modified with mining level tags
	 * is -1 (the hand mining level).
	 *
	 * @param state the block state
	 * @return the mining level of the block state
	 */
	public static int getRequiredMiningLevel(BlockState state) {
		return MiningLevelManagerImpl.getRequiredMiningLevel(state);
	}

	/**
	 * Gets the mining level block tag corresponding to a given integer mining level.
	 * More precisely, return the corresponding vanilla tag ({@code #minecraft:needs_x_tool}) for levels 1 to 3,
	 * and the Fabric tag ({@code #fabric:needs_tool_level_N}) for levels above 3.
	 *
	 * @param miningLevel the integer mining level
	 * @return the corresponding mining level block tag
	 * @throws IllegalArgumentException if a negative or zero mining level is passed
	 */
	public static TagKey<Block> getBlockTag(int miningLevel) {
		if (miningLevel <= 0) {
			throw new IllegalArgumentException("Mining level tags only exist for mining levels > 0, received: " + miningLevel);
		}

		return switch (miningLevel) {
		case 1 -> BlockTags.NEEDS_STONE_TOOL;
		case 2 -> BlockTags.NEEDS_IRON_TOOL;
		case 3 -> BlockTags.NEEDS_DIAMOND_TOOL;
		default -> TagKey.of(Registry.BLOCK_KEY, new Identifier("fabric", "needs_tool_level_" + miningLevel));
		};
	}
}
