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

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

import net.fabricmc.fabric.impl.content.registry.VillagerPlantableRegistryImpl;

/**
 * Registry of items that farmer villagers can plant on farmland.
 *
 * @see VillagerCollectablesRegistry to allow villagers to collect the item
 */
public interface VillagerPlantableRegistry {
	VillagerPlantableRegistry INSTANCE = new VillagerPlantableRegistryImpl();

	/**
	 * Register a BlockItem to be plantable, using the default state.
	 * @param item the item to register
	 */
	void add(ItemConvertible item);

	/**
	 * Register am item to be plantable with a specified BlockState.
	 * @param item the item to register
	 * @param plantState the state that will be planted
	 */
	void add(ItemConvertible item, BlockState plantState);

	/**
	 * Remove an item from being plantable.
	 * @param item the item to remove
	 * @return the state that the item was assigned to
	 */
	BlockState remove(ItemConvertible item);

	/**
	 * Test an item is plantable.
	 * @param item the item to test
	 * @return true if the item is plantable
	 */
	boolean contains(ItemConvertible item);

	/**
	 * Get the state what will be planted by an item.
	 * @param item the item to get
	 * @return the state associated with the item
	 */
	BlockState getPlantState(ItemConvertible item);

	/**
	 * Get a set of all items which are plantable.
	 * @return a set of all plantable items
	 */
	Set<Item> getItems();
}
