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

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.tag.Tag;

import net.fabricmc.fabric.api.registry.ShieldRegistry;

public class ShieldRegistryImpl implements ShieldRegistry {
	public static final ShieldRegistryImpl INSTANCE = new ShieldRegistryImpl();

	private final Set<ItemConvertible> registeredItemEntries = new HashSet<>();
	private final Set<Tag<Item>> registeredTagEntries = new HashSet<>();

	@Override
	public void add(ItemConvertible item) {
		registeredItemEntries.add(item);
	}

	@Override
	public void add(Tag<Item> tag) {
		registeredTagEntries.add(tag);
	}

	@Override
	public void clear(ItemConvertible item) {
		registeredItemEntries.remove(item);
	}

	@Override
	public void clear(Tag<Item> tag) {
		registeredTagEntries.remove(tag);
	}

	public boolean isShield(Item item) {
		if (registeredItemEntries.contains(item)) {
			return true;
		}

		for (Tag<Item> entry : registeredTagEntries) {
			if (entry.contains(item)) {
				return true;
			}
		}

		return false;
	}
}
