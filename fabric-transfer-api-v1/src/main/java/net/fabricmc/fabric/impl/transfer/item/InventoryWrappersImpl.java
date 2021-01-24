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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.base.FilteredStorageFunction;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryWrapper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageFunction;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Participant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionResult;

// TODO: better insertion logic? (check if the item already exists in a non-empty slot before inserting it into an empty slot)
public class InventoryWrappersImpl {
	public static List<Storage<ItemKey>> ofInventory(Inventory inventory) {
		List<Storage<ItemKey>> result = new ArrayList<>(7); // 6 directions + null

		// wrapper around the whole inventory
		List<SlotWrapper> slots = IntStream.range(0, inventory.size())
				.mapToObj(i -> new SlotWrapper(inventory, i))
				.collect(Collectors.toList());
		Storage<ItemKey> fullWrapper = inventory instanceof PlayerInventory
				? new PlayerInventoryWrapperImpl(slots, (PlayerInventory) inventory) : new CombinedStorage<>(slots);

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
	private static class SlotWrapper implements Storage<ItemKey>, StorageView<ItemKey>, Participant<ItemStack> {
		private final Inventory inventory;
		private final int slot;
		private final StorageFunction<ItemKey> insertionFunction;
		private final StorageFunction<ItemKey> extractionFunction;

		private SlotWrapper(Inventory inventory, int slot) {
			this.inventory = inventory;
			this.slot = slot;
			this.insertionFunction = (itemKey, longCount, tx) -> {
				// TODO: clean this up
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

		public StorageFunction<ItemKey> insertionFunction() {
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
		public void onClose(ItemStack state, TransactionResult result) {
			if (result.wasAborted()) {
				inventory.setStack(slot, state);
			}
		}

		@Override
		public void onFinalCommit() {
			inventory.markDirty();

			// TODO: is this necessary for player inventories?
			/*if (inventory instanceof PlayerInventory) {
				PlayerInventory playerInventory = (PlayerInventory) inventory;

				if (playerInventory.player instanceof ServerPlayerEntity) {
					ServerPlayerEntity player = (ServerPlayerEntity) playerInventory.player;
					player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, slot, inventory.getStack(slot)));
				}
			}*/
		}
	}

	// Wraps a SlotWrapper with SidedInventory#canInsert and SidedInventory#canExtract checks.
	private static class SidedSlotWrapper implements Storage<ItemKey> {
		private final SlotWrapper slotWrapper;
		private final StorageFunction<ItemKey> insertionFunction;
		private final StorageFunction<ItemKey> extractionFunction;

		private SidedSlotWrapper(SlotWrapper slotWrapper, SidedInventory sidedInventory, Direction direction) {
			this.slotWrapper = slotWrapper;
			this.insertionFunction = new FilteredStorageFunction<>(slotWrapper.insertionFunction,
					itemKey -> sidedInventory.canInsert(slotWrapper.slot, itemKey.toStack(), direction));
			this.extractionFunction = new FilteredStorageFunction<>(slotWrapper.extractionFunction,
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

	// Wraps a PlayerInventory with an extra function to be used as an `offerOrDrop` replacement.
	private static class PlayerInventoryWrapperImpl extends CombinedStorage<ItemKey, SlotWrapper> implements PlayerInventoryWrapper {
		private final PlayerInventory playerInventory;
		private final OfferOrDropFunction offerOrDropFunction;
		private final CursorSlotWrapper cursorSlotWrapper;

		private PlayerInventoryWrapperImpl(List<SlotWrapper> parts, PlayerInventory playerInventory) {
			super(parts);
			this.playerInventory = playerInventory;
			this.offerOrDropFunction = new OfferOrDropFunction();
			this.cursorSlotWrapper = new CursorSlotWrapper();
		}

		@Override
		public StorageFunction<ItemKey> offerOrDropFunction() {
			return offerOrDropFunction;
		}

		@Override
		public Storage<ItemKey> slotWrapper(int slot) {
			return parts.get(slot);
		}

		@Override
		public Storage<ItemKey> cursorSlotWrapper() {
			return cursorSlotWrapper;
		}

		private class OfferOrDropFunction implements StorageFunction<ItemKey>, Participant<Integer> {
			private final List<ItemKey> droppedKeys = new ArrayList<>();
			private final List<Long> droppedCounts = new ArrayList<>();

			@Override
			public long apply(ItemKey resource, long maxAmount, Transaction tx) {
				ItemPreconditions.notEmptyNotNegative(resource, maxAmount);

				// Always succeeds, but the actual modification has to happen server-side.
				if (playerInventory.player.world.isClient()) return maxAmount;

				long initialAmount = maxAmount;

				for (int iteration = 0; iteration < 2; iteration++) {
					boolean allowEmptySlots = iteration == 1;

					for (SlotWrapper slot : parts) {
						if (!slot.inventory.getStack(slot.slot).isEmpty() || allowEmptySlots) {
							maxAmount -= slot.insertionFunction.apply(resource, maxAmount, tx);
						}
					}
				}

				// Drop leftover in the world
				if (maxAmount > 0) {
					tx.enlist(this);
					droppedKeys.add(resource);
					droppedCounts.add(maxAmount);
				}

				return initialAmount; // always fully succeeds
			}

			@Override
			public Integer onEnlist() {
				return droppedKeys.size();
			}

			@Override
			public void onClose(Integer integer, TransactionResult result) {
				if (result.wasAborted()) {
					int previousSize = integer;

					// effectively cancel dropping the stacks
					while (droppedKeys.size() > previousSize) {
						droppedKeys.remove(droppedKeys.size() - 1);
						droppedCounts.remove(droppedCounts.size() - 1);
					}
				}
			}

			@Override
			public void onFinalCommit() {
				// drop the stacks
				for (int i = 0; i < droppedKeys.size(); ++i) {
					ItemKey key = droppedKeys.get(i);

					while (droppedCounts.get(i) > 0) {
						int dropped = (int) Math.min(key.getItem().getMaxCount(), droppedCounts.get(i));
						playerInventory.player.dropStack(key.toStack(dropped));
						droppedCounts.set(i, droppedCounts.get(i) - dropped);
					}
				}

				droppedKeys.clear();
				droppedCounts.clear();
			}
		}

		private class CursorSlotWrapper implements Storage<ItemKey>, StorageView<ItemKey>, Participant<ItemStack> {
			private final StorageFunction<ItemKey> insertionFunction;
			private final StorageFunction<ItemKey> extractionFunction;

			private CursorSlotWrapper() {
				this.insertionFunction = (itemKey, count, tx) -> {
					ItemPreconditions.notEmptyNotNegative(itemKey, count);
					ItemStack stack = playerInventory.getCursorStack();
					int inserted = (int) Math.min(count, Math.min(64, itemKey.getItem().getMaxCount()) - stack.getCount());

					if (stack.isEmpty()) {
						ItemStack keyStack = itemKey.toStack(inserted);
						tx.enlist(this);
						playerInventory.setCursorStack(keyStack);
						return inserted;
					} else if (itemKey.matches(stack)) {
						tx.enlist(this);
						stack.increment(inserted);
						return inserted;
					}

					return 0;
				};
				this.extractionFunction = (itemKey, maxCount, tx) -> {
					ItemPreconditions.notEmptyNotNegative(itemKey, maxCount);
					ItemStack stack = playerInventory.getCursorStack();

					if (itemKey.matches(stack)) {
						int extracted = (int) Math.min(stack.getCount(), maxCount);
						tx.enlist(this);
						stack.decrement(extracted);
						return extracted;
					}

					return 0;
				};
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
				if (!playerInventory.getCursorStack().isEmpty()) {
					return visitor.visit(this);
				}

				return false;
			}

			@Override
			public ItemKey resource() {
				return ItemKey.of(playerInventory.getCursorStack());
			}

			@Override
			public long amount() {
				return playerInventory.getCursorStack().getCount();
			}

			@Override
			public ItemStack onEnlist() {
				return playerInventory.getCursorStack().copy();
			}

			@Override
			public void onClose(ItemStack previousStack, TransactionResult result) {
				if (result.wasAborted()) {
					playerInventory.setCursorStack(previousStack);
				}
			}

			@Override
			public void onFinalCommit() {
				playerInventory.markDirty();
			}
		}
	}
}
