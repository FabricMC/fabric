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

package net.fabricmc.fabric.api.tags;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

/**
 * Helper methods for registering Tags.
 */
public final class TagRegistry {
	private TagRegistry() {

	}

	public static Tag<Block> block(Identifier id) {
		return new TagDelegate<Block>(id, null) {
			private TagContainer<Block> container;

			@Override
			protected void onAccess() {
				if (container != BlockTags.getContainer()) {
					container = BlockTags.getContainer();
					delegate = container.getOrCreate(this.getId());
				}
			}
		};
	}

	public static Tag<Item> item(Identifier id) {
		return new ItemTags.class_3490(id);
	}
}
