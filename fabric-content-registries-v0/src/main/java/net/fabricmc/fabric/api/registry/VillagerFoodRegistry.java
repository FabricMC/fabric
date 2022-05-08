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

package net.fabricmc.fabric.api.registry;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

import net.fabricmc.fabric.api.util.ImmutableCollectionUtils;
import net.fabricmc.fabric.mixin.content.registry.VillagerEntityAccessor;

/**
 * Registry for controlling which items villagers see as valid food items,
 * and how much breeding power they have.
 */
public class VillagerFoodRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(VillagerFoodRegistry.class);

	/**
	 * Registers an item to be edible by villagers.
	 * @param item      the item to register
	 * @param foodValue the amount of breeding power the item has (1 = normal food item, 4 = bread)
	 */
	public static void register(ItemConvertible item, int foodValue) {
		Integer oldValue = getRegistry().put(item.asItem(), foodValue);

		if (oldValue != null) {
			LOGGER.info("Overriding previous food value of {}, was: {}, now: {}", item.asItem().toString(), oldValue, foodValue);
		}
	}

	private static Map<Item, Integer> getRegistry() {
		return ImmutableCollectionUtils.getAsMutableMap(() -> VillagerEntity.ITEM_FOOD_VALUES, VillagerEntityAccessor::setItemFoodValues);
	}
}
