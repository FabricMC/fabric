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

package net.fabricmc.fabric.api.content.registry.v1.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

/**
 * A registry that maps items to values.
 */
public interface ItemContentRegistry<V> extends ContentRegistry<Item, V> {
	default V get(ItemConvertible key) {
		return this.get(key.asItem());
	}

	default void add(ItemConvertible key, V value) {
		this.add(key.asItem(), value);
	}

	default void remove(ItemConvertible key) {
		this.remove(key.asItem());
	}
}
