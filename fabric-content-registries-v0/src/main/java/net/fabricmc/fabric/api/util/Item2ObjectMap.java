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

package net.fabricmc.fabric.api.util;

import net.minecraft.class_6862;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

public interface Item2ObjectMap<V> {
	V get(ItemConvertible item);

	void add(ItemConvertible item, V value);

	void add(class_6862<Item> tag, V value);

	void remove(ItemConvertible item);

	void remove(class_6862<Item> tag);

	void clear(ItemConvertible item);

	void clear(class_6862<Item> tag);
}
