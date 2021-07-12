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

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.impl.transfer.item.CursorSlotWrapper;
import net.fabricmc.fabric.impl.transfer.item.InventoryWrappersImpl;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

/**
 * A {@code Storage<ItemVariant>} wrapper around a vanilla {@link Inventory}.
 *
 * <p>Don't implement, query with one of the {@code of(...)} functions.
 *
 * <p><b>Important note:</b> These wrappers assume that each slot belongs to the inventory, and that the inventory has a fixed size.
 * These assumptions are reasonable when dealing with vanilla inventories,
 * however modded inventories that don't own their slots or have a dynamic number of slots <b>must not</b> use these wrappers.
 *
 * @deprecated Experimental feature, we reserve the right to remove or change it without further notice.
 * The transfer API is a complex addition, and we want to be able to correct possible design mistakes.
 */
@ApiStatus.Experimental
@Deprecated
@ApiStatus.NonExtendable
public interface InventoryStorage extends Storage<ItemVariant> {
	/**
	 * Retrieve an unmodifiable list of the wrappers for the slots in this inventory.
	 * Each wrapper corresponds to a single slot in the inventory.
	 */
	List<SingleSlotStorage<ItemVariant>> getSlots();

	/**
	 * A wrapper around a player inventory.
	 *
	 * <p>Note that this is a wrapper around all the slots of the player inventory.
	 * This may cause direct insertion to insert arbitrary items into equipment slots or other unexpected behavior.
	 * To prevent this, {@link #offerOrDrop} is recommended for simple insertions.
	 * {@link #getSlots} can also be used to retrieve a wrapper around a range of slots.
	 */
	@ApiStatus.NonExtendable
	interface Player extends InventoryStorage {
		/**
		 * Add items to the inventory if possible, and drop any leftover items in the world.
		 */
		void offerOrDrop(ItemVariant key, long amount, TransactionContext transaction);
	}

	/**
	 * Return a wrapper around an {@link Inventory}.
	 * If the inventory is a {@link SidedInventory}, the wrapper wraps the sided inventory from the given direction.
	 *
	 * @param inventory The inventory to wrap.
	 * @param direction The direction to use if the access is sided, or {@code null} if the access is not sided.
	 */
	static InventoryStorage of(Inventory inventory, @Nullable Direction direction) {
		Objects.requireNonNull(inventory, "Null inventory is not supported.");
		return InventoryWrappersImpl.of(inventory, direction);
	}

	/**
	 * Return a wrapper around the inventory of a player.
	 *
	 * @see Player
	 */
	static Player ofPlayer(PlayerEntity player) {
		return ofPlayer(player.getInventory());
	}

	/**
	 * Return a wrapper around the inventory of a player.
	 *
	 * @see Player
	 */
	static Player ofPlayer(PlayerInventory playerInventory) {
		return (Player) of(playerInventory, null);
	}

	/**
	 * Return a wrapper around the cursor slot of a screen handler.
	 */
	static SingleSlotStorage<ItemVariant> ofCursor(ScreenHandler screenHandler) {
		return CursorSlotWrapper.get(screenHandler);
	}
}
