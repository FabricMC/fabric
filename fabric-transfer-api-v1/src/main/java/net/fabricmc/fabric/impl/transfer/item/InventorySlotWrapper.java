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

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.item.ItemPreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Participant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionResult;

// A wrapper around a single slot of an inventory
// We must ensure that only one instance of this class exists for every inventory slot,
// or the transaction logic will not work correct.
class InventorySlotWrapper implements Storage<ItemKey>, StorageView<ItemKey>, Participant<ItemStack> {
	final Inventory inventory;
	final int slot;

	InventorySlotWrapper(Inventory inventory, int slot) {
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
	public boolean forEach(Storage.Visitor<ItemKey> visitor, Transaction transaction) {
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
		inventory.markDirty();
	}
}
