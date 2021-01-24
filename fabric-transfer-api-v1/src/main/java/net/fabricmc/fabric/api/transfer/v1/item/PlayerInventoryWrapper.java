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

package net.fabricmc.fabric.api.transfer.v1.item;

import org.jetbrains.annotations.ApiStatus;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * A wrapper around a PlayerInventory.
 *
 * <p>Do not implement. Obtain an instance through {@link InventoryWrappers#ofPlayerInventory} instead.
 */
@ApiStatus.NonExtendable
public interface PlayerInventoryWrapper {
	/**
	 * Return a wrapper around a specific slot of the player inventory.
	 *
	 * <p>Slots 0 to 35 are for the main inventory, slots 36 to 39 are for the armor, and slot 40 is the offhand slot.
	 */
	Storage<ItemKey> slotWrapper(int index);

	/**
	 * Return a wrapper around the cursor slot of the player inventory.
	 */
	Storage<ItemKey> cursorSlotWrapper();

	/**
	 * Add items to the inventory if possible, and drop any leftover items in the world.
	 */
	void offerOrDrop(ItemKey key, long amount, Transaction transaction);
}
