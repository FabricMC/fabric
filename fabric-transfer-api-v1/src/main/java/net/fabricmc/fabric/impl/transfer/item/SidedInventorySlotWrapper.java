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

import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

// Wraps an InventorySlotWrapper with SidedInventory#canInsert and SidedInventory#canExtract checks for a given direction.
class SidedInventorySlotWrapper implements Storage<ItemKey> {
	private final InventorySlotWrapper slotWrapper;
	private final SidedInventory sidedInventory; // TODO: should we just cast slotWrapper.inventory instead?
	private final Direction direction;

	SidedInventorySlotWrapper(InventorySlotWrapper slotWrapper, SidedInventory sidedInventory, Direction direction) {
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
