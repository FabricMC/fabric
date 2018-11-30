/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.tools;

import net.fabricmc.fabric.util.TriState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.tag.Tag;

import java.util.HashMap;
import java.util.Map;

public final class ToolManager {
	private static class Entry {
		@SuppressWarnings("unchecked")
		private Tag<Item>[] tags = (Tag<Item>[]) new Tag[0];
		private int[] tagLevels = new int[0];
		private TriState defaultValue = TriState.DEFAULT;

		public void setDefaultValue(TriState defaultValue) {
			this.defaultValue = defaultValue;
		}

		public void addTag(Tag<Item> tag, int miningLevel) {
			for (int i = 0; i < tags.length; i++) {
				if (tags[i] == tag) {
					tagLevels[i] = miningLevel;
					return;
				}
			}

			//noinspection unchecked
			Tag<Item>[] newTags = (Tag<Item>[]) new Tag[tags.length + 1];
			int[] newTagLevels = new int[tagLevels.length + 1];
			System.arraycopy(tags, 0, newTags, 0, tags.length);
			System.arraycopy(tagLevels, 0, newTagLevels, 0, tagLevels.length);
			newTags[tags.length] = tag;
			newTagLevels[tagLevels.length] = miningLevel;
			tags = newTags;
			tagLevels = newTagLevels;
		}
	}

	private static final Map<Block, Entry> entries = new HashMap<>();

	private ToolManager() {

	}

	private static Entry computeEntry(Block b) {
		return entries.computeIfAbsent(b, (bb) -> new Entry());
	}

	public static void registerBreakByHand(Block block, boolean value) {
		computeEntry(block).defaultValue = TriState.of(value);
	}

	public static void registerBreakByTool(Block block, Tag<Item> tag, int miningLevel) {
		computeEntry(block).defaultValue = TriState.FALSE;
		computeEntry(block).addTag(tag, miningLevel);
	}

	public static int getMiningLevel(ItemStack stack) {
		if (stack.getItem() instanceof ToolItem) {
			return ((ToolItem) stack.getItem()).getType().getMiningLevel();
		} else {
			return 0;
		}
	}

	public static TriState handleIsEffectiveOn(ItemStack stack, BlockState state) {
		Entry entry = entries.get(state.getBlock());
		if (entry != null) {
			Item item = stack.getItem();
			for (int i = 0; i < entry.tags.length; i++) {
				if (item.matches(entry.tags[i])) {
					return TriState.of(getMiningLevel(stack) >= entry.tagLevels[i]);
				}
			}

			return entry.defaultValue;
		} else {
			return TriState.DEFAULT;
		}
	}
}
