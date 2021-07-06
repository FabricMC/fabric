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

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

class PlayerInventoryStorageImpl extends CombinedStorage<ItemVariant, SingleSlotStorage<ItemVariant>> implements InventoryStorage.Player {
	private final PlayerInventory playerInventory;
	private final DroppedStacks droppedStacks;

	PlayerInventoryStorageImpl(List<SingleSlotStorage<ItemVariant>> slots, PlayerInventory playerInventory) {
		super(slots);
		this.playerInventory = playerInventory;
		this.droppedStacks = new DroppedStacks();
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getSlots() {
		return parts;
	}

	@Override
	public void offerOrDrop(ItemVariant resource, long amount, TransactionContext tx) {
		StoragePreconditions.notBlankNotNegative(resource, amount);

		// TODO: should probably be limited to inventory slots (currently it accesses armor slots as well!).
		for (int iteration = 0; iteration < 2; iteration++) {
			boolean allowEmptySlots = iteration == 1;

			for (SingleSlotStorage<ItemVariant> slot : parts) {
				if (!slot.isResourceBlank() || allowEmptySlots) {
					amount -= slot.insert(resource, amount, tx);
				}
			}
		}

		// Drop leftover in the world on the server side (will be synced by the game with the client).
		// Dropping items is server-side only because it involves randomness.
		if (amount > 0 && playerInventory.player.world.isClient()) {
			droppedStacks.addDrop(resource, amount, tx);
		}
	}

	private class DroppedStacks extends SnapshotParticipant<Integer> {
		final List<ItemVariant> droppedKeys = new ArrayList<>();
		final List<Long> droppedCounts = new ArrayList<>();

		void addDrop(ItemVariant key, long count, TransactionContext transaction) {
			updateSnapshots(transaction);
			droppedKeys.add(key);
			droppedCounts.add(count);
		}

		@Override
		protected Integer createSnapshot() {
			return droppedKeys.size();
		}

		@Override
		protected void readSnapshot(Integer snapshot) {
			// effectively cancel dropping the stacks
			int previousSize = snapshot;

			while (droppedKeys.size() > previousSize) {
				droppedKeys.remove(droppedKeys.size() - 1);
				droppedCounts.remove(droppedCounts.size() - 1);
			}
		}

		@Override
		protected void onFinalCommit() {
			// drop the stacks and mark dirty
			for (int i = 0; i < droppedKeys.size(); ++i) {
				ItemVariant key = droppedKeys.get(i);

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
}
