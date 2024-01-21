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

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

import net.fabricmc.fabric.api.registry.FuelRegistry;

// TODO: Clamp values to 32767 (+ add hook for mods which extend the limit to disable the check?)
public final class FuelRegistryImpl implements FuelRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(FuelRegistryImpl.class);
	private final Object2IntMap<ItemConvertible> itemCookTimes = new Object2IntLinkedOpenHashMap<>();
	private final Object2IntMap<TagKey<Item>> tagCookTimes = new Object2IntLinkedOpenHashMap<>();

	public FuelRegistryImpl() {
	}

	public Map<Item, Integer> getFuelTimes() {
		// Cached by vanilla now
		return AbstractFurnaceBlockEntity.createFuelTimeMap();
	}

	@Override
	public Integer get(ItemConvertible item) {
		return getFuelTimes().get(item.asItem());
	}

	@Override
	public void add(ItemConvertible item, Integer cookTime) {
		if (cookTime > 32767) {
			LOGGER.warn("Tried to register an overly high cookTime: " + cookTime + " > 32767! (" + item + ")");
		}

		itemCookTimes.put(item, cookTime.intValue());
		resetCache();
	}

	@Override
	public void add(TagKey<Item> tag, Integer cookTime) {
		if (cookTime > 32767) {
			LOGGER.warn("Tried to register an overly high cookTime: " + cookTime + " > 32767! (" + getTagName(tag) + ")");
		}

		tagCookTimes.put(tag, cookTime.intValue());
		resetCache();
	}

	@Override
	public void remove(ItemConvertible item) {
		add(item, 0);
		resetCache();
	}

	@Override
	public void remove(TagKey<Item> tag) {
		add(tag, 0);
		resetCache();
	}

	@Override
	public void clear(ItemConvertible item) {
		itemCookTimes.removeInt(item);
		resetCache();
	}

	@Override
	public void clear(TagKey<Item> tag) {
		tagCookTimes.removeInt(tag);
		resetCache();
	}

	public void apply(Map<Item, Integer> map) {
		// tags take precedence before blocks
		for (TagKey<Item> tag : tagCookTimes.keySet()) {
			int time = tagCookTimes.getInt(tag);

			if (time <= 0) {
				for (RegistryEntry<Item> key : Registries.ITEM.iterateEntries(tag)) {
					final Item item = key.value();
					map.remove(item);
				}
			} else {
				AbstractFurnaceBlockEntity.addFuel(map, tag, time);
			}
		}

		for (ItemConvertible item : itemCookTimes.keySet()) {
			int time = itemCookTimes.getInt(item);

			if (time <= 0) {
				map.remove(item.asItem());
			} else {
				AbstractFurnaceBlockEntity.addFuel(map, item, time);
			}
		}
	}

	private static String getTagName(TagKey<?> tag) {
		return tag.id().toString();
	}

	public void resetCache() {
		// Note: tag reload is already handled by vanilla, see DataPackContents#refresh
		AbstractFurnaceBlockEntity.clearFuelTimes();
	}
}
