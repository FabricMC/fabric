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
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Hand;

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
 * However, {@link #insert} is overridden to behave like {@link #offer}.
 * For simple insertions, {@link #offer} or {@link #offerOrDrop} is recommended.
 * {@link #getSlots} can also be used and combined with {@link CombinedStorage} to retrieve a wrapper around a specific range of slots.
 *
 * <p><b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@ApiStatus.NonExtendable
// TODO: Consider explicitly syncing stacks by sending a ScreenHandlerSlotUpdateS2CPacket if that proves to be necessary.
// TODO: Vanilla doesn't seem to be doing it reliably, so we ignore it for now.
public interface PlayerInventoryStorage extends InventoryStorage {
	/**
	 * Return an instance for the passed player's inventory.
	 */
	static PlayerInventoryStorage of(PlayerEntity player) {
		return of(player.getInventory());
	}

	/**
	 * Return an instance for the passed player inventory.
	 */
	static PlayerInventoryStorage of(PlayerInventory playerInventory) {
		return (PlayerInventoryStorage) InventoryStorage.of(playerInventory, null);
	}

	/**
	 * Return a wrapper around the cursor slot of a screen handler,
	 * i.e. the stack that can be manipulated with {@link ScreenHandler#getCursorStack()} and {@link ScreenHandler#setCursorStack}.
	 */
	static SingleSlotStorage<ItemVariant> getCursorStorage(ScreenHandler screenHandler) {
		return CursorSlotWrapper.get(screenHandler);
	}

	/**
	 * Insert items into this player inventory. Behaves the same as {@link #offer}.
	 * More fine-tuned insertion, for example over a specific range of slots, is possible with {@linkplain #getSlots() the slot list}.
	 *
	 * @see #offer
	 */
	@Override
	long insert(ItemVariant resource, long maxAmount, TransactionContext transaction);

	/**
	 * Add items to the inventory if possible, and drop any leftover items in the world, similar to {@link PlayerInventory#offerOrDrop}.
	 *
	 * <p>Note: This function has full transaction support, and will not actually drop the items until the outermost transaction is committed.
	 *
	 * @param variant The variant to insert.
	 * @param amount How many of the variant to insert.
	 * @param transaction The transaction this operation is part of.
	 */
	default void offerOrDrop(ItemVariant variant, long amount, TransactionContext transaction) {
		long offered = offer(variant, amount, transaction);
		drop(variant, amount - offered, transaction);
	}

	/**
	 * Try to add items to the inventory if possible, stacking like {@link PlayerInventory#offer}.
	 * Unlike {@link #offerOrDrop}, this function will not drop excess items.
	 *
	 * <p>The exact behavior is:
	 * <ol>
	 *     <li>Try to stack inserted items with existing items in the main hand, then the offhand.</li>
	 *     <li>Try to stack remaining inserted items with existing items in the player main inventory.</li>
	 *     <li>Try to insert the remainder into empty slots of the player main inventory.</li>
	 * </ol>
	 *
	 * @param variant The variant to insert.
	 * @param maxAmount How many of the variant to insert, at most.
	 * @param transaction The transaction this operation is part of.
	 * @return How many items could be inserted.
	 */
	long offer(ItemVariant variant, long maxAmount, TransactionContext transaction);

	/**
	 * Throw items in the world from the player's location.
	 *
	 * <p>Note: This function has full transaction support, and will not actually drop the items until the outermost transaction is committed.
	 *
	 * @param variant The variant to drop.
	 * @param amount How many of the variant to drop.
	 * @param throwRandomly If true, the variant will be thrown in a random direction from the entity regardless of which direction the entity is facing.
	 * @param retainOwnership If true, set the {@code Thrower} NBT data to the player's UUID.
	 * @param transaction The transaction this operation is part of.
	 * @see PlayerEntity#dropItem(ItemStack, boolean, boolean)
	 */
	void drop(ItemVariant variant, long amount, boolean throwRandomly, boolean retainOwnership, TransactionContext transaction);

	/**
	 * Throw items in the world from the player's location.
	 *
	 * <p>Note: This function has full transaction support, and will not actually drop the items until the outermost transaction is committed.
	 *
	 * @param variant The variant to drop.
	 * @param amount How many of the variant to drop.
	 * @param retainOwnership If true, set the {@code Thrower} NBT data to the player's UUID.
	 * @param transaction The transaction this operation is part of.
	 * @see PlayerEntity#dropItem(ItemStack, boolean, boolean)
	 */
	default void drop(ItemVariant variant, long amount, boolean retainOwnership, TransactionContext transaction) {
		drop(variant, amount, false, retainOwnership, transaction);
	}

	/**
	 * Throw items in the world from the player's location.
	 *
	 * <p>Note: This function has full transaction support, and will not actually drop the items until the outermost transaction is committed.
	 *
	 * @param variant The variant to drop.
	 * @param amount How many of the variant to drop.
	 * @param transaction The transaction this operation is part of.
	 * @see PlayerEntity#dropItem(ItemStack, boolean, boolean)
	 */
	default void drop(ItemVariant variant, long amount, TransactionContext transaction) {
		drop(variant, amount, false, transaction);
	}

	/**
	 * Return a wrapper around the current slot of the passed hand.
	 */
	SingleSlotStorage<ItemVariant> getHandSlot(Hand hand);
}
