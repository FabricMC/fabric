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

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.Tag;

/**
 * Interface for tools to provide information about their mining ability.
 * Allows items that don't extend {@code MiningToolItem}, such as Swords and Shears, to provide mining information.
 */
public interface FabricTool {
	/**
	 * Returns the {@code ToolMaterial} of this tool.
	 * Can be {@code null} if the tool does not utilise {@code ToolMaterial}s.
	 * Defaults to {@code null}.
	 * @return The tool material
	 */
	@Nullable
	default ToolMaterial getToolMaterial() {
		return null;
	}

	/**
	 * Returns a {@code Tag} of the blocks this tool is effective against.
	 * Can be {@code null} if the tool does not utilise a tag.
	 * Defaults to {@code null}.
	 * @return The effective blocks
	 */
	@Nullable
	default Tag<Block> getEffectiveBlocks() {
		return null;
	}

	/**
	 * Returns the mining level of this tool.
	 * Defaults to the mining level of the material returned by {@link #getToolMaterial()}, or 0 if that returns {@code null}.
	 * @return The mining level
	 */
	default int getMiningLevel() {
		ToolMaterial toolMaterial = getToolMaterial();

		if (toolMaterial != null) {
			return getToolMaterial().getMiningLevel();
		} else {
			return 0;
		}
	}

	/**
	 * Returns whether a tool of this type, if it had the mining level provided, would be suitable for mining the provided {@code BlockState}.
	 * Can be useful in circumstances where {@link net.minecraft.item.Item#isSuitableFor(BlockState)} is not.
	 * @param miningLevel the mining level of the tool
	 * @param state the blockstate being mined
	 * @return Whether the tool is suitable
	 */
	default boolean isSuitableFor(int miningLevel, BlockState state) {
		if (getEffectiveBlocks() == null) {
			return false;
		} else {
			return state.isIn(getEffectiveBlocks()) && miningLevel >= MiningLevelManager.getRequiredMiningLevel(state);
		}
	}
}
