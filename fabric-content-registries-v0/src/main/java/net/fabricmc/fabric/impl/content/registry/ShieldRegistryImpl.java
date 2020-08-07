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

package net.fabricmc.fabric.impl.content.registry;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.tag.Tag;

import net.fabricmc.fabric.api.registry.ShieldRegistry;

public class ShieldRegistryImpl implements ShieldRegistry {
	public static final ShieldRegistryImpl INSTANCE = new ShieldRegistryImpl();

	private final Object2IntMap<ItemConvertible> registeredItemEntries = new Object2IntLinkedOpenHashMap<>();

	@Override
	public Integer get(ItemConvertible item) {
		if (registeredItemEntries.containsKey(item.asItem())) {
			return registeredItemEntries.getInt(item.asItem());
		}

		return null;
	}

	@Override
	public void add(ItemConvertible item, Integer axeDisableDuration) {
		registeredItemEntries.put(item, axeDisableDuration);
	}

	@Override
	public void add(Tag<Item> tag, Integer axeDisableDuration) {
		throw new UnsupportedOperationException("Tags are not supported here.");
	}

	@Override
	public void clear(ItemConvertible item) {
		throw new UnsupportedOperationException("Cannot clear from the shield registry.");
	}

	@Override
	public void clear(Tag<Item> tag) {
		throw new UnsupportedOperationException("Cannot clear from the shield registry.");
	}

	@Override
	public void remove(ItemConvertible item) {
		throw new UnsupportedOperationException("Cannot remove from the shield registry.");
	}

	@Override
	public void remove(Tag<Item> tag) {
		throw new UnsupportedOperationException("Cannot remove from the shield registry.");
	}
}
