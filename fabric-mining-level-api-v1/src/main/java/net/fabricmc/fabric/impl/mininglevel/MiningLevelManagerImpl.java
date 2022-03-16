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

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;

import net.fabricmc.yarn.constants.MiningLevels;

public final class MiningLevelManagerImpl {
	private static final Logger LOGGER = LoggerFactory.getLogger("fabric-mining-level-api-v1/MiningLevelManagerImpl");
	private static final String TOOL_TAG_NAMESPACE = "fabric";
	private static final Pattern TOOL_TAG_PATTERN = Pattern.compile("^needs_tool_level_([0-9]+)$");

	// A cache of block state mining levels. Cleared by
	// - MiningLevelCacheInvalidator when tags are reloaded
	// - ClientPlayNetworkHandlerMixin when tags are synced
	private static final ThreadLocal<Reference2IntMap<BlockState>> CACHE = ThreadLocal.withInitial(Reference2IntOpenHashMap::new);

	public static int getRequiredMiningLevel(BlockState state) {
		return CACHE.get().computeIfAbsent(state, s -> {
			int miningLevel = MiningLevels.HAND;

			// Handle #fabric:needs_tool_level_N
			for (TagKey<Block> tagId : state.streamTags().toList()) {
				if (!tagId.id().getNamespace().equals(TOOL_TAG_NAMESPACE)) {
					continue;
				}

				Matcher matcher = TOOL_TAG_PATTERN.matcher(tagId.id().getPath());

				if (matcher.matches()) {
					try {
						int tagMiningLevel = Integer.parseInt(matcher.group(1));
						miningLevel = Math.max(miningLevel, tagMiningLevel);
					} catch (NumberFormatException e) {
						LOGGER.error("Could not read mining level from tag #{}", tagId, e);
					}
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

	public static void clearCache() {
		CACHE.get().clear();
	}
}
