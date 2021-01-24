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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemPreconditions;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryWrapper;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Participant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionResult;

// A wrapper around a PlayerInventory with the additional functions in PlayerInventoryWrapper.
class PlayerInventoryWrapperImpl extends CombinedStorage<ItemKey, InventorySlotWrapper>
		implements Participant<Integer>, PlayerInventoryWrapper {
	private final PlayerInventory playerInventory;
	private final CursorSlotWrapper cursorSlotWrapper;
	private final List<ItemKey> droppedKeys = new ArrayList<>();
	private final List<Long> droppedCounts = new ArrayList<>();

	PlayerInventoryWrapperImpl(List<InventorySlotWrapper> slots, PlayerInventory playerInventory) {
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
	public void offerOrDrop(ItemKey resource, long amount, Transaction tx) {
		ItemPreconditions.notEmptyNotNegative(resource, amount);

		for (int iteration = 0; iteration < 2; iteration++) {
			boolean allowEmptySlots = iteration == 1;

			for (InventorySlotWrapper slot : parts) {
				if (!slot.inventory.getStack(slot.slot).isEmpty() || allowEmptySlots) {
					amount -= slot.insert(resource, amount, tx);
				}
			}
		}

		// Drop leftover in the world on the server side (will be synced by the game with the client).
		// Dropping items is server-side only because it involves randomness.
		if (amount > 0 && playerInventory.player.world.isClient()) {
			tx.enlist(this);
			droppedKeys.add(resource);
			droppedCounts.add(amount);
		}
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
