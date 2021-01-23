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

package net.fabricmc.fabric.api.transfer.v1.item;

import com.google.common.base.Preconditions;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;

public class ItemPreconditions {
	public static void notEmpty(ItemKey key) {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("ItemKey may not be empty or null.");
		}
	}

	public static void notEmpty(Item item) {
		if (item == null || item == Items.AIR) {
			throw new IllegalArgumentException("Item may not be empty or null.");
		}
	}

	public static void notEmptyNotNegative(ItemKey key, long amount) {
		ItemPreconditions.notEmpty(key);
		Preconditions.checkArgument(amount >= 0);
	}
}
