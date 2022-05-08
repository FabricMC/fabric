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

import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

import net.fabricmc.fabric.api.util.ImmutableCollectionUtils;
import net.fabricmc.fabric.mixin.content.registry.VillagerEntityAccessor;

/**
 * Registry for controlling items that all villagers can
 * pick up from the ground.
 */
public class VillagerCollectableRegistry {
	/**
	 * Registers an item to be collectable by villagers.
	 * @param item the item to register
	 */
	public static void register(ItemConvertible item) {
		getRegistry().add(item.asItem());
	}

	private static Set<Item> getRegistry() {
		return ImmutableCollectionUtils.getAsMutableSet(VillagerEntityAccessor::getGatherableItems, VillagerEntityAccessor::setGatherableItems);
	}
}
