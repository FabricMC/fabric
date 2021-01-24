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

package net.fabricmc.fabric.impl.transfer.item;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public class InventoryWrappersImpl {
	// List<Storage<ItemKey>> has 7 values.
	// The 6 first for the various directions, and the last element for a null direction.
	private static final WeakHashMap<Inventory, List<Storage<ItemKey>>> WRAPPERS = new WeakHashMap<>();

	public static Storage<ItemKey> of(Inventory inventory, @Nullable Direction direction) {
		List<Storage<ItemKey>> storages = WRAPPERS.computeIfAbsent(inventory, InventoryWrappersImpl::buildWrappers);
		return direction != null ? storages.get(direction.ordinal()) : storages.get(6);
	}

	private static List<Storage<ItemKey>> buildWrappers(Inventory inventory) {
		List<Storage<ItemKey>> result = new ArrayList<>(7); // 6 directions + null

		// wrapper around the whole inventory
		List<InventorySlotWrapper> slots = IntStream.range(0, inventory.size())
				.mapToObj(i -> new InventorySlotWrapper(inventory, i))
				.collect(Collectors.toList());
		Storage<ItemKey> fullWrapper = inventory instanceof PlayerInventory
				? new PlayerInventoryWrapperImpl(slots, (PlayerInventory) inventory) : new CombinedStorage<>(slots);

		if (inventory instanceof SidedInventory) {
			// sided logic, only use the slots returned by SidedInventory#getAvailableSlots, and check canInsert/canExtract
			SidedInventory sidedInventory = (SidedInventory) inventory;

			for (Direction direction : Direction.values()) {
				List<SidedInventorySlotWrapper> sideSlots = IntStream.of(sidedInventory.getAvailableSlots(direction))
						.mapToObj(slot -> new SidedInventorySlotWrapper(slots.get(slot), sidedInventory, direction))
						.collect(Collectors.toList());
				result.add(new CombinedStorage<>(sideSlots));
			}
		} else {
			// unsided logic, just use the same Storage 7 times
			for (int i = 0; i < 6; ++i) { // 6 directions
				result.add(fullWrapper);
			}
		}

		result.add(fullWrapper);
		return result;
	}
}
