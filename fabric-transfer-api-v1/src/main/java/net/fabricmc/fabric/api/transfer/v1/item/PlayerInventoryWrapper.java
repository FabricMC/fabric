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

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

/**
 * A wrapper around a PlayerInventory.
 */
// TODO: this careful consideration to decide which wrappers are necessary
public interface PlayerInventoryWrapper {
	long offerOrDrop(ItemKey key, long maxAmount, Transaction transaction);
	Storage<ItemKey> slotWrapper(int slot);
	Storage<ItemKey> cursorSlotWrapper();
}
