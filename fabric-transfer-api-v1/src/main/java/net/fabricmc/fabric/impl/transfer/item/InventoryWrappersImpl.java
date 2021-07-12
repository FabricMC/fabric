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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.MapMaker;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

public class InventoryWrappersImpl {
	/**
	 * Each InventoryStorage[] contains 7 values.
	 * The first 6 are for the various directions, and the 7th is for a null direction.
	 *
	 * <p>A note on GC: weak keys alone are not suitable as the InventoryStorage slots strongly reference the Inventory keys.
	 * Weak values are suitable, but we have to ensure that the InventoryStorage[] remains strongly reachable as long as
	 * one of the slot wrappers refers to it, hence the {@code strongRef} field in {@link InventorySlotWrapper}.
	 */
	private static final Map<Inventory, InventoryStorage[]> WRAPPERS = new MapMaker().weakValues().makeMap();

	public static InventoryStorage of(Inventory inventory, @Nullable Direction direction) {
		InventoryStorage[] storages = WRAPPERS.computeIfAbsent(inventory, InventoryWrappersImpl::buildWrappers);
		return direction != null ? storages[direction.ordinal()] : storages[6];
	}

	private static InventoryStorage[] buildWrappers(Inventory inventory) {
		InventoryStorage[] result = new InventoryStorage[7]; // 6 directions + null

		// Wrapper around the whole inventory
		InventorySlotWrapper[] slots = createInventorySlots(inventory, result);

		if (inventory instanceof PlayerInventory playerInventory) {
			result[6] = new PlayerInventoryStorageImpl(Arrays.asList(slots), playerInventory);
		} else {
			result[6] = new InventoryStorageImpl(Arrays.asList(slots));
		}

		if (inventory instanceof SidedInventory sidedInventory) {
			// Sided logic: only use the slots returned by SidedInventory#getAvailableSlots, and check canInsert/canExtract.
			for (Direction direction : Direction.values()) {
				SidedInventorySlotWrapper[] sidedSlots = createSidedInventorySlots(slots, sidedInventory, direction);
				result[direction.ordinal()] = new InventoryStorageImpl(Arrays.asList(sidedSlots));
			}
		} else {
			// Unsided logic: just use the same wrapper seven times.
			for (int i = 0; i < 6; ++i) { // 6 directions
				result[i] = result[6];
			}
		}

		return result;
	}

	private static InventorySlotWrapper[] createInventorySlots(Inventory inventory, InventoryStorage[] strongRef) {
		InventorySlotWrapper[] slots = new InventorySlotWrapper[inventory.size()];

		for (int slot = 0; slot < inventory.size(); ++slot) {
			slots[slot] = new InventorySlotWrapper(inventory, slot, strongRef);
		}

		return slots;
	}

	private static SidedInventorySlotWrapper[] createSidedInventorySlots(InventorySlotWrapper[] unsidedWrappers, SidedInventory inventory, Direction direction) {
		int[] availableSlots = inventory.getAvailableSlots(direction);
		SidedInventorySlotWrapper[] slots = new SidedInventorySlotWrapper[availableSlots.length];

		for (int i = 0; i < availableSlots.length; ++i) {
			slots[i] = new SidedInventorySlotWrapper(unsidedWrappers[availableSlots[i]], inventory, direction);
		}

		return slots;
	}

	private static class InventoryStorageImpl extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> implements InventoryStorage {
		InventoryStorageImpl(List<SingleSlotStorage<ItemVariant>> parts) {
			super(parts);
		}

		@Override
		public List<SingleSlotStorage<ItemVariant>> getSlots() {
			return parts;
		}
	}
}
