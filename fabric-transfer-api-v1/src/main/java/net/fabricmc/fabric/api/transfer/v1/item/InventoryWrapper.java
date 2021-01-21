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

import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

import org.jetbrains.annotations.Nullable;

import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.impl.transfer.item.InventoryWrapperImpl;

public class InventoryWrapper {
	// List<Storage<ItemKey>> has 7 values.
	// The 6 first for the various directions, and the last element for a null direction.
	private static final WeakHashMap<Inventory, List<Storage<ItemKey>>> WRAPPERS = new WeakHashMap<>();

	public static Storage<ItemKey> of(Inventory inventory, @Nullable Direction direction) {
		Objects.requireNonNull(inventory, "Null inventory is not supported.");
		List<Storage<ItemKey>> storages = WRAPPERS.computeIfAbsent(inventory, InventoryWrapperImpl::ofInventory);

		return direction != null ? storages.get(direction.ordinal()) : storages.get(6);
	}

	private InventoryWrapper() {
	}
}
