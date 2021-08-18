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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;

import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.item.CursorSlotWrapper;

/**
 * A {@code Storage<ItemVariant>} implementation for a {@link PlayerInventory}.
 * This is a specialized version of {@link InventoryStorage},
 * with an additional transactional wrapper for {@link PlayerInventory#offerOrDrop}.
 *
 * <p>Note that this is a wrapper around all the slots of the player inventory.
 * This may cause direct insertion to insert arbitrary items into equipment slots or other unexpected behavior.
 * To prevent this, {@link #offerOrDrop} is recommended for simple insertions.
 * {@link #getSlots} can also be used and combined with {@link CombinedStorage} to retrieve a wrapper around a specific range of slots.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
@ApiStatus.NonExtendable
public interface PlayerInventoryStorage extends InventoryStorage {
	/**
	 * Return an instance for the passed player's inventory.
	 */
	static PlayerInventoryStorage of(PlayerEntity player) {
		return of(player.inventory);
	}

	/**
	 * Return an instance for the passed player inventory.
	 */
	static PlayerInventoryStorage of(PlayerInventory playerInventory) {
		return (PlayerInventoryStorage) InventoryStorage.of(playerInventory, null);
	}

	/**
	 * Return a wrapper around the cursor slot of a player,
	 * i.e. the stack that can be manipulated with {@link PlayerInventory#getCursorStack} and {@link PlayerInventory#setCursorStack}.
	 */
	static SingleSlotStorage<ItemVariant> getCursorStorage(PlayerEntity player) {
		return getCursorStorage(player.inventory);
	}

	/**
	 * Return a wrapper around the cursor slot of a player inventory,
	 * i.e. the stack that can be manipulated with {@link PlayerInventory#getCursorStack} and {@link PlayerInventory#setCursorStack}.
	 */
	static SingleSlotStorage<ItemVariant> getCursorStorage(PlayerInventory playerInventory) {
		return CursorSlotWrapper.get(playerInventory);
	}

	/**
	 * Add items to the inventory if possible, and drop any leftover items in the world, similar to {@link PlayerInventory#offerOrDrop}
	 *
	 * <p>Note: This function has full transaction support, and will not actually drop the items until the outermost transaction is committed.
	 *
	 * @param variant The variant to insert.
	 * @param amount How many of the variant to insert.
	 * @param transaction The transaction this operation is part of.
	 */
	void offerOrDrop(ItemVariant variant, long amount, TransactionContext transaction);
}
