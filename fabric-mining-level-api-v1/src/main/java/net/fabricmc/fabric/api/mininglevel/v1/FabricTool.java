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

public interface FabricTool {
	@Nullable
	default ToolMaterial getToolMaterial() {
		return null;
	}

	@Nullable
	default Tag<Block> getEffectiveBlocks() {
		return null;
	}

	default int getMiningLevel() {
		ToolMaterial toolMaterial = getToolMaterial();

		if (toolMaterial != null) {
			return getToolMaterial().getMiningLevel();
		} else {
			return 0;
		}
	}

	default boolean isSuitableFor(int miningLevel, BlockState state) {
		if (getEffectiveBlocks() == null) {
			return false;
		} else {
			return state.isIn(getEffectiveBlocks()) && miningLevel >= MiningLevelManager.getRequiredMiningLevel(state);
		}
	}
}
