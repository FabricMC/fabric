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

package net.fabricmc.fabric.api.item;

import net.fabricmc.fabric.impl.item.CompostingChanceRegistry;
import net.fabricmc.fabric.impl.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.tag.Tag;

public interface ItemPropertyRegistry<V> {
	public static final ItemPropertyRegistry<Integer> FUEL = FuelRegistry.INSTANCE;
	public static final ItemPropertyRegistry<Float> COMPOSTING_CHANCE = CompostingChanceRegistry.INSTANCE;

	void add(ItemProvider item, V value);
	void add(Tag<Item> tag, V value);
	void remove(ItemProvider item);
	void remove(Tag<Item> tag);
}
