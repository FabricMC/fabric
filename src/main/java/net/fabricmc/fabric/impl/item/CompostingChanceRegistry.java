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

package net.fabricmc.fabric.impl.item;

import net.fabricmc.fabric.api.util.Item2ObjectMap;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.tag.Tag;

public class CompostingChanceRegistry implements Item2ObjectMap<Float> {
	public static final CompostingChanceRegistry INSTANCE = new CompostingChanceRegistry();

	@Override
	public Float get(ItemProvider item) {
		return ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.getOrDefault(item.getItem(), 0.0F);
	}

	@Override
	public void add(ItemProvider item, Float value) {
		ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(item.getItem(), value.floatValue());
	}

	@Override
	public void add(Tag<Item> tag, Float value) {
		throw new UnsupportedOperationException("Tags currently not supported!");
	}

	@Override
	public void remove(ItemProvider item) {
		ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.removeFloat(item.getItem());
	}

	@Override
	public void remove(Tag<Item> tag) {
		throw new UnsupportedOperationException("Tags currently not supported!");
	}
}
