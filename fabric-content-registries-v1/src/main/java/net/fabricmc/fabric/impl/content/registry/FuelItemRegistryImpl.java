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

import com.google.common.base.Preconditions;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.content.registry.v1.util.ItemContentRegistry;

public class FuelItemRegistryImpl extends ContentRegistryImpl<Item, Integer> implements ItemContentRegistry<Integer> {
	public static final Map<Item, Integer> FUELS = new HashMap<>();

	public static final ItemContentRegistry<Integer> INSTANCE = new FuelItemRegistryImpl();

	public FuelItemRegistryImpl() {
		super("fuel_item_registry", FUELS::put, fuel -> FUELS.put(fuel, 0), item -> AbstractFurnaceBlockEntity.createFuelTimeMap().get(item));
	}

	@Override
	public void add(Tag<Item> tag, Integer value) {
		Preconditions.checkArgument(value >= 0 && value <= Short.MAX_VALUE, "Fuel value " + value + " for tag " + tag.getId() + " is out of range. Must be between 0 and 32767 (inclusive)");
		super.add(tag, value);
	}

	@Override
	public void add(Item item, Integer value) {
		Preconditions.checkArgument(value >= 0 && value <= Short.MAX_VALUE, "Fuel value " + value + " for item " + Registry.ITEM.getId(item) + " is out of range. Must be between 0 and 32767 (inclusive)");
		super.add(item, value);
	}

	public static void addFuels(Map<Item, Integer> map) {
		map.putAll(FUELS);
	}
}
