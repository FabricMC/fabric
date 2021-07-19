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

package net.fabricmc.fabric.impl.mininglevel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.yarn.constants.MiningLevels;

public final class MiningLevelManagerImpl {
	private static final String TOOL_TAG_NAMESPACE = "fabric";
	private static final Pattern TOOL_TAG_PATTERN = Pattern.compile("^needs_tool_level_([0-9]+)$");
	private static final Object2IntMap<BlockState> CACHE = new Object2IntOpenHashMap<>();

	public static int getRequiredMiningLevel(BlockState state) {
		return CACHE.computeIntIfAbsent(state, s -> {
			TagGroup<Block> blockTags = ServerTagManagerHolder.getTagManager().getOrCreateTagGroup(Registry.BLOCK_KEY);
			int miningLevel = MiningLevels.HAND;

			// Handle #fabric:needs_tool_level_N
			for (Identifier tagId : blockTags.getTagsFor(state.getBlock())) {
				if (!tagId.getNamespace().equals(TOOL_TAG_NAMESPACE)) {
					continue;
				}

				Matcher matcher = TOOL_TAG_PATTERN.matcher(tagId.getPath());

				if (matcher.matches()) {
					miningLevel = Math.max(miningLevel, Integer.parseInt(matcher.group(1)));
				}
			}

			// Handle vanilla tags
			if (state.isIn(BlockTags.NEEDS_DIAMOND_TOOL)) {
				miningLevel = Math.max(miningLevel, MiningLevels.DIAMOND);
			} else if (state.isIn(BlockTags.NEEDS_IRON_TOOL)) {
				miningLevel = Math.max(miningLevel, MiningLevels.IRON);
			} else if (state.isIn(BlockTags.NEEDS_STONE_TOOL)) {
				miningLevel = Math.max(miningLevel, MiningLevels.STONE);
			}

			return miningLevel;
		});
	}

	static void clearCache() {
		CACHE.clear();
	}
}
