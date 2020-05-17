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

package net.fabricmc.fabric.impl.object.builder;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;

public final class FabricBlockInternals {
	private FabricBlockInternals() {
	}

	public static ExtraData computeExtraData(Block.Settings settings) {
		BlockSettingsInternals internals = (BlockSettingsInternals) settings;

		if (internals.getExtraData() == null) {
			internals.setExtraData(new ExtraData(settings));
		}

		return internals.getExtraData();
	}

	public static void onBuild(Block.Settings settings, Block block) {
		// TODO: Load only if fabric-tool-attribute-api present
		ExtraData data = ((BlockSettingsInternals) settings).getExtraData();

		if (data != null) {
			if (data.breakByHand != null) {
				ToolManagerImpl.entry(block).setBreakByHand(data.breakByHand);
			}

			for (MiningLevel tml : data.miningLevels) {
				ToolManagerImpl.entry(block).putBreakByTool(tml.tag, tml.level);
			}
		}
	}

	public static final class ExtraData {
		private final List<MiningLevel> miningLevels = new ArrayList<>();
		/* @Nullable */ private Boolean breakByHand;

		public ExtraData(Block.Settings settings) {
		}

		public void breakByHand(boolean breakByHand) {
			this.breakByHand = breakByHand;
		}

		public void addMiningLevel(Tag<Item> tag, int level) {
			miningLevels.add(new MiningLevel(tag, level));
		}
	}

	public static final class MiningLevel {
		private final Tag<Item> tag;
		private final int level;

		MiningLevel(Tag<Item> tag, int level) {
			this.tag = tag;
			this.level = level;
		}
	}
}
