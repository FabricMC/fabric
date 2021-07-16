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

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

/**
 * A wrapper around a single slot of an inventory
 * We must ensure that only one instance of this class exists for every inventory slot,
 * or the transaction logic will not work correctly.
 * This is handled by the Map in InventoryWrappersImpl.
 */
class InventorySlotWrapper extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {
	/**
	 * Strong reference to the InventoryStorage array to ensure that the weak values don't get GC'ed when InventoryStorages are still being accessed.
	 */
	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final InventoryStorage[] strongRef;
	final Inventory inventory;
	final int slot;

	InventorySlotWrapper(Inventory inventory, int slot, InventoryStorage[] strongRef) {
		this.inventory = inventory;
		this.slot = slot;
		this.strongRef = strongRef;
	}

	@Override
	public long insert(ItemVariant variant, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(variant, maxAmount);
		int maxCountPerStack = Math.min(variant.getItem().getMaxCount(), inventory.getMaxCountPerStack());
		ItemStack stack = inventory.getStack(slot);

		if (stack.isEmpty()) {
			ItemStack variantStack = variant.toStack();

			if (inventory.isValid(slot, variantStack)) {
				int inserted = (int) Math.min(maxCountPerStack, maxAmount);
				this.updateSnapshots(transaction);
				variantStack.setCount(inserted);
				inventory.setStack(slot, variantStack);
				return inserted;
			}
		} else if (variant.matches(stack)) {
			int inserted = (int) Math.min(maxCountPerStack - stack.getCount(), maxAmount);

			if (inserted > 0) {
				this.updateSnapshots(transaction);
				stack.increment(inserted);
			}

			return inserted;
		}

		return 0;
	}

	@Override
	public long extract(ItemVariant variant, long maxAmount, TransactionContext transaction) {
		StoragePreconditions.notBlankNotNegative(variant, maxAmount);
		ItemStack stack = inventory.getStack(slot);

		if (variant.matches(stack)) {
			int extracted = (int) Math.min(stack.getCount(), maxAmount);

			if (extracted > 0) {
				this.updateSnapshots(transaction);
				stack.decrement(extracted);
			}

			return extracted;
		}

		return 0;
	}

	@Override
	public ItemVariant getResource() {
		return ItemVariant.of(inventory.getStack(slot));
	}

	@Override
	public boolean isResourceBlank() {
		return inventory.getStack(slot).isEmpty();
	}

	@Override
	public long getAmount() {
		return inventory.getStack(slot).getCount();
	}

	@Override
	public long getCapacity() {
		return inventory.getStack(slot).getMaxCount();
	}

	@Override
	protected ItemStack createSnapshot() {
		return inventory.getStack(slot).copy();
	}

	@Override
	protected void readSnapshot(ItemStack snapshot) {
		inventory.setStack(slot, snapshot);
	}

	@Override
	public void onFinalCommit() {
		inventory.markDirty();
	}
}
