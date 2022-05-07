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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.tag.TagKey;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

import net.fabricmc.fabric.api.registry.VillagerFoodRegistry;
import net.fabricmc.fabric.mixin.content.registry.VillagerEntityAccessor;

public class VillagerFoodRegistryImpl implements VillagerFoodRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(VillagerFoodRegistry.class);

	@Override
	public Integer get(ItemConvertible item) {
		return VillagerEntity.ITEM_FOOD_VALUES.getOrDefault(item.asItem(), 0);
	}

	@Override
	public void add(ItemConvertible item, Integer value) {
		makeMapMutable();

		Integer old = VillagerEntity.ITEM_FOOD_VALUES.put(item.asItem(), value);

		if (old != null) {
			LOGGER.info("Villager food {} replaced, was: {}, now {}.", item.asItem(), old, value);
		}
	}

	@Override
	public void add(TagKey<Item> tag, Integer value) {
		throw new UnsupportedOperationException("Tags currently not supported!");
	}

	@Override
	public void remove(ItemConvertible item) {
		makeMapMutable();

		VillagerEntity.ITEM_FOOD_VALUES.put(item.asItem(), 0);
	}

	@Override
	public void remove(TagKey<Item> tag) {
		throw new UnsupportedOperationException("Tags currently not supported!");
	}

	@Override
	public void clear(ItemConvertible item) {
		makeMapMutable();

		VillagerEntity.ITEM_FOOD_VALUES.remove(item.asItem());
	}

	@Override
	public void clear(TagKey<Item> tag) {
		throw new UnsupportedOperationException("Tags currently not supported!");
	}

	private static void makeMapMutable() {
		Map<Item, Integer> foodValuesMap = VillagerEntity.ITEM_FOOD_VALUES;

		if (!(foodValuesMap instanceof HashMap)) {
			VillagerEntityAccessor.setItemFoodValues(new HashMap<>(foodValuesMap));
		}
	}
}
