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

import net.fabricmc.fabric.impl.item.FuelRegistryImpl;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.tag.Tag;

/**
 * Registry for item fuel values.
 */
public interface FuelRegistry {
	public static final FuelRegistry INSTANCE = FuelRegistryImpl.INSTANCE;

	void add(ItemProvider item, int cookTime);
	void add(Tag<Item> tag, int cookTime);

	default void remove(ItemProvider item) {
		add(item, 0);
	}

	default void remove(Tag<Item> tag) {
		add(tag, 0);
	}
}
