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

import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A wrapper around a single slot of an inventory.
 * We must ensure that only one instance of this class exists for every inventory slot,
 * or the transaction logic will not work correctly.
 * This is handled by the Map in InventoryStorageImpl.
 */
class InventorySlotWrapper extends SingleStackStorage {
	/**
	 * The strong reference to the InventoryStorageImpl ensures that the weak value doesn't get GC'ed when individual slots are still being accessed.
	 */
	private final InventoryStorageImpl storage;
	final int slot;
	private ItemStack lastReleasedSnapshot = null;

	InventorySlotWrapper(InventoryStorageImpl storage, int slot) {
		this.storage = storage;
		this.slot = slot;
	}

	@Override
	protected ItemStack getStack() {
		return storage.inventory.getStack(slot);
	}

	@Override
	protected void setStack(ItemStack stack) {
		storage.inventory.setStack(slot, stack);
	}

	@Override
	protected boolean canInsert(ItemVariant itemVariant) {
		return storage.inventory.isValid(slot, itemVariant.toStack());
	}

	@Override
	public int getCapacity(ItemVariant variant) {
		return Math.min(storage.inventory.getMaxCountPerStack(), variant.getItem().getMaxCount());
	}

	// We override updateSnapshots to also schedule a markDirty call for the backing inventory.
	@Override
	public void updateSnapshots(TransactionContext transaction) {
		storage.markDirtyParticipant.updateSnapshots(transaction);
		super.updateSnapshots(transaction);
	}

	@Override
	protected void releaseSnapshot(ItemStack snapshot) {
		lastReleasedSnapshot = snapshot;
	}

	@Override
	protected void onFinalCommit() {
		// Try to apply the change to the original stack
		ItemStack original = lastReleasedSnapshot;
		ItemStack currentStack = getStack();

		if (!original.isEmpty() && original.getItem() == currentStack.getItem()) {
			// None is empty and the items match: just update the amount and NBT, and reuse the original stack.
			original.setCount(currentStack.getCount());
			original.setTag(currentStack.hasTag() ? currentStack.getTag().copy() : null);
			setStack(original);
		} else {
			// Otherwise assume everything was taken from original so empty it.
			original.setCount(0);
		}
	}
}
