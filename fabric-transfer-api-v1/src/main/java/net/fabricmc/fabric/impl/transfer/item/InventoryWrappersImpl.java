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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryWrapper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
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
				? new PlayerInventoryWrapperImpl(slots, (PlayerInventory) inventory) : new InventoryWrapper(slots, inventory);

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

		private SlotWrapper(Inventory inventory, int slot) {
			this.inventory = inventory;
			this.slot = slot;
		}

		@Override
		public boolean supportsInsertion() {
			return true;
		}

		@Override
		public long insert(ItemKey key, long maxAmount, Transaction transaction) {
			// TODO: clean this up
			ItemPreconditions.notEmpty(key);
			int count = (int) Math.min(maxAmount, inventory.getMaxCountPerStack());
			ItemStack stack = inventory.getStack(slot);

			if (stack.isEmpty()) {
				ItemStack keyStack = key.toStack(count);

				if (inventory.isValid(slot, keyStack)) {
					int inserted = Math.min(keyStack.getMaxCount(), count);
					transaction.enlist(this);
					keyStack.setCount(inserted);
					inventory.setStack(slot, keyStack);
					return inserted;
				}
			} else if (key.matches(stack)) {
				int inserted = Math.min(stack.getMaxCount() - stack.getCount(), count);
				transaction.enlist(this);
				stack.increment(inserted);
				return inserted;
			}

			return 0;
		}

		@Override
		public boolean supportsExtraction() {
			return true;
		}

		@Override
		public long extract(ItemKey key, long maxAmount, Transaction transaction) {
			ItemPreconditions.notEmpty(key);
			ItemStack stack = inventory.getStack(slot);

			if (key.matches(stack)) {
				int extracted = (int) Math.min(stack.getCount(), maxAmount);
				transaction.enlist(this);
				stack.decrement(extracted);
				return extracted;
			}

			return 0;
		}

		@Override
		public boolean forEach(Visitor<ItemKey> visitor, Transaction transaction) {
			if (!inventory.getStack(slot).isEmpty()) {
				return visitor.accept(this);
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
			// TODO: is this necessary for player inventories? in what circumstances?
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
		private final SidedInventory sidedInventory;
		private final Direction direction;

		private SidedSlotWrapper(SlotWrapper slotWrapper, SidedInventory sidedInventory, Direction direction) {
			this.slotWrapper = slotWrapper;
			this.sidedInventory = sidedInventory;
			this.direction = direction;
		}

		@Override
		public boolean supportsInsertion() {
			return true;
		}

		@Override
		public long insert(ItemKey resource, long maxAmount, Transaction transaction) {
			if (!sidedInventory.canInsert(slotWrapper.slot, resource.toStack(), direction)) {
				return 0;
			} else {
				return slotWrapper.insert(resource, maxAmount, transaction);
			}
		}

		@Override
		public boolean supportsExtraction() {
			return true;
		}

		@Override
		public long extract(ItemKey resource, long maxAmount, Transaction transaction) {
			if (!sidedInventory.canExtract(slotWrapper.slot, resource.toStack(), direction)) {
				return 0;
			} else {
				return slotWrapper.insert(resource, maxAmount, transaction);
			}
		}

		@Override
		public boolean forEach(Visitor<ItemKey> visitor, Transaction transaction) {
			return slotWrapper.forEach(visitor, transaction);
		}
	}

	// Wraps an inventory by wrapping a list of SidedSlotWrappers
	// This exists so that markDirty can be called at the end of a successful transaction.
	private static class InventoryWrapper extends CombinedStorage<ItemKey, SlotWrapper> implements Participant<Void> {
		private final Inventory inventory;

		private InventoryWrapper(List<SlotWrapper> slots, Inventory inventory) {
			super(slots);
			this.inventory = inventory;
		}

		@Override
		public long insert(ItemKey resource, long maxAmount, Transaction transaction) {
			transaction.enlist(this);
			return super.insert(resource, maxAmount, transaction);
		}

		@Override
		public long extract(ItemKey resource, long maxAmount, Transaction transaction) {
			transaction.enlist(this);
			return super.extract(resource, maxAmount, transaction);
		}

		@Override
		public boolean forEach(Visitor<ItemKey> visitor, Transaction transaction) {
			transaction.enlist(this);
			return super.forEach(visitor, transaction);
		}

		@Override
		public Void onEnlist() {
			return null;
		}

		@Override
		public void onClose(Void state, TransactionResult result) {
		}

		@Override
		public void onFinalCommit() {
			inventory.markDirty();
		}
	}

	// Wraps a PlayerInventory with an extra function to be used as an `offerOrDrop` replacement.
	private static class PlayerInventoryWrapperImpl extends CombinedStorage<ItemKey, SlotWrapper> implements Participant<Integer>, PlayerInventoryWrapper {
		private final PlayerInventory playerInventory;
		private final CursorSlotWrapper cursorSlotWrapper;
		private final List<ItemKey> droppedKeys = new ArrayList<>();
		private final List<Long> droppedCounts = new ArrayList<>();

		private PlayerInventoryWrapperImpl(List<SlotWrapper> slots, PlayerInventory playerInventory) {
			super(slots);
			this.playerInventory = playerInventory;
			this.cursorSlotWrapper = new CursorSlotWrapper();
		}

		@Override
		public long insert(ItemKey resource, long maxAmount, Transaction transaction) {
			transaction.enlist(this);
			return super.insert(resource, maxAmount, transaction);
		}

		@Override
		public long extract(ItemKey resource, long maxAmount, Transaction transaction) {
			transaction.enlist(this);
			return super.extract(resource, maxAmount, transaction);
		}

		@Override
		public boolean forEach(Visitor<ItemKey> visitor, Transaction transaction) {
			transaction.enlist(this);
			return super.forEach(visitor, transaction);
		}

		@Override
		public long offerOrDrop(ItemKey resource, long maxAmount, Transaction tx) {
			ItemPreconditions.notEmptyNotNegative(resource, maxAmount);

			// Always succeeds, but the actual modification has to happen server-side.
			if (playerInventory.player.world.isClient()) return maxAmount;

			long initialAmount = maxAmount;

			for (int iteration = 0; iteration < 2; iteration++) {
				boolean allowEmptySlots = iteration == 1;

				for (SlotWrapper slot : parts) {
					if (!slot.inventory.getStack(slot.slot).isEmpty() || allowEmptySlots) {
						maxAmount -= slot.insert(resource, maxAmount, tx);
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
		public Storage<ItemKey> slotWrapper(int slot) {
			return parts.get(slot);
		}

		@Override
		public Storage<ItemKey> cursorSlotWrapper() {
			return cursorSlotWrapper;
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
			// drop the stacks and mark dirty
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
			playerInventory.markDirty();
		}

		private class CursorSlotWrapper implements Storage<ItemKey>, StorageView<ItemKey>, Participant<ItemStack> {
			@Override
			public boolean supportsInsertion() {
				return true;
			}

			@Override
			public long insert(ItemKey itemKey, long maxAmount, Transaction transaction) {
				ItemPreconditions.notEmptyNotNegative(itemKey, maxAmount);
				ItemStack stack = playerInventory.getCursorStack();
				int inserted = (int) Math.min(maxAmount, Math.min(64, itemKey.getItem().getMaxCount()) - stack.getCount());

				if (stack.isEmpty()) {
					ItemStack keyStack = itemKey.toStack(inserted);
					transaction.enlist(this);
					playerInventory.setCursorStack(keyStack);
					return inserted;
				} else if (itemKey.matches(stack)) {
					transaction.enlist(this);
					stack.increment(inserted);
					return inserted;
				}

				return 0;
			}

			@Override
			public boolean supportsExtraction() {
				return true;
			}

			@Override
			public long extract(ItemKey itemKey, long maxAmount, Transaction transaction) {
				ItemPreconditions.notEmptyNotNegative(itemKey, maxAmount);
				ItemStack stack = playerInventory.getCursorStack();

				if (itemKey.matches(stack)) {
					int extracted = (int) Math.min(stack.getCount(), maxAmount);
					transaction.enlist(this);
					stack.decrement(extracted);
					return extracted;
				}

				return 0;
			}

			@Override
			public boolean forEach(Visitor<ItemKey> visitor, Transaction transaction) {
				if (!playerInventory.getCursorStack().isEmpty()) {
					return visitor.accept(this);
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
