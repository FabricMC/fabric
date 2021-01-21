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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.primitives.Ints;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.base.IntegerStorageFunction;
import net.fabricmc.fabric.api.transfer.v1.base.IntegerStorageView;
import net.fabricmc.fabric.api.transfer.v1.base.PredicateStorageFunction;
import net.fabricmc.fabric.api.transfer.v1.item.ItemPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageFunction;
import net.fabricmc.fabric.api.transfer.v1.transaction.Participant;

// TODO: better insertion logic? (check if the item already exists in a non-empty slot before inserting it into an empty slot)
public class InventoryWrapperImpl {
	public static List<Storage<ItemKey>> ofInventory(Inventory inventory) {
		List<Storage<ItemKey>> result = new ArrayList<>(7); // 6 directions + null

		// wrapper around the whole inventory
		List<SlotWrapper> slots = IntStream.range(0, inventory.size())
				.mapToObj(i -> new SlotWrapper(inventory, i))
				.collect(Collectors.toList());
		Storage<ItemKey> fullWrapper = new CombinedStorage<>(slots);

		if (inventory instanceof SidedInventory) {
			// sided logic, only use the slots returned by SidedInventory#getAvailableSlots, and check canInsert/canExtract
			SidedInventory sidedInventory = (SidedInventory) inventory;

			for (Direction direction : Direction.values()) {
				List<SidedSlotWrapper> sideSlots = IntStream.of(sidedInventory.getAvailableSlots(direction))
						.mapToObj(slot -> new SidedSlotWrapper(slots.get(slot), sidedInventory, direction))
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

	// Wraps a single slot of an unsided Inventory.
	private static class SlotWrapper implements Storage<ItemKey>, IntegerStorageView<ItemKey>, Participant<ItemStack> {
		private final Inventory inventory;
		private final int slot;
		private final IntegerStorageFunction<ItemKey> insertionFunction;
		private final IntegerStorageFunction<ItemKey> extractionFunction;

		private SlotWrapper(Inventory inventory, int slot) {
			this.inventory = inventory;
			this.slot = slot;
			this.insertionFunction = (itemKey, longCount, tx) -> {
				ItemPreconditions.notEmpty(itemKey);
				int count = Math.min(Ints.saturatedCast(longCount), inventory.getMaxCountPerStack());
				ItemStack stack = inventory.getStack(slot);

				if (stack.isEmpty()) {
					ItemStack keyStack = itemKey.toStack(count);

					if (inventory.isValid(slot, keyStack)) {
						int inserted = Math.min(keyStack.getMaxCount(), count);
						tx.enlist(this);
						keyStack.setCount(inserted);
						inventory.setStack(slot, keyStack);
						return inserted;
					}
				} else if (itemKey.matches(stack)) {
					int inserted = Math.min(stack.getMaxCount() - stack.getCount(), count);
					tx.enlist(this);
					stack.increment(inserted);
					return inserted;
				}

				return 0;
			};
			this.extractionFunction = (itemKey, longCount, tx) -> {
				ItemPreconditions.notEmpty(itemKey);
				int count = Ints.saturatedCast(longCount);
				ItemStack stack = inventory.getStack(slot);

				if (itemKey.matches(stack)) {
					int extracted = Math.min(stack.getCount(), count);
					tx.enlist(this);
					stack.decrement(extracted);
					return extracted;
				}

				return 0;
			};
		}

		public IntegerStorageFunction<ItemKey> insertionFunction() {
			return insertionFunction;
		}

		@Override
		public StorageFunction<ItemKey> extractionFunction() {
			return extractionFunction;
		}

		@Override
		public boolean forEach(Visitor<ItemKey> visitor) {
			if (!inventory.getStack(slot).isEmpty()) {
				return visitor.visit(this);
			}

			return false;
		}

		@Override
		public ItemKey resource() {
			return ItemKey.of(inventory.getStack(slot));
		}

		@Override
		public long amount() {
			return inventory.getStack(slot).getCount();
		}

		@Override
		public ItemStack onEnlist() {
			return inventory.getStack(slot).copy();
		}

		@Override
		public void onClose(ItemStack state, boolean success) {
			if (!success) {
				inventory.setStack(slot, state);
			}
		}

		@Override
		public void onFinalSuccess() {
			inventory.markDirty();
		}
	}

	// Wraps a SlotWrapper with SidedInventory#canInsert and SidedInventory#canExtract checks.
	private static class SidedSlotWrapper implements Storage<ItemKey> {
		private final SlotWrapper slotWrapper;
		private final StorageFunction<ItemKey> insertionFunction;
		private final StorageFunction<ItemKey> extractionFunction;

		private SidedSlotWrapper(SlotWrapper slotWrapper, SidedInventory sidedInventory, Direction direction) {
			this.slotWrapper = slotWrapper;
			this.insertionFunction = new PredicateStorageFunction<>(slotWrapper.insertionFunction,
					itemKey -> sidedInventory.canInsert(slotWrapper.slot, itemKey.toStack(), direction));
			this.extractionFunction = new PredicateStorageFunction<>(slotWrapper.extractionFunction,
					itemKey -> sidedInventory.canExtract(slotWrapper.slot, itemKey.toStack(), direction));
		}

		@Override
		public StorageFunction<ItemKey> insertionFunction() {
			return insertionFunction;
		}

		@Override
		public StorageFunction<ItemKey> extractionFunction() {
			return extractionFunction;
		}

		@Override
		public boolean forEach(Visitor<ItemKey> visitor) {
			return slotWrapper.forEach(visitor);
		}
	}
}
