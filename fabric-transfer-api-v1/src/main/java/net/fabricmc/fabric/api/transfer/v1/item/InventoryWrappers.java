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

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.Direction;

import net.fabricmc.fabric.api.lookup.v1.item.ItemKey;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.impl.transfer.item.InventoryWrappersImpl;

/**
 * Wraps {@link Inventory} and {@link PlayerInventory} as {@link Storage} implementations.
 */
public final class InventoryWrappers {
	/**
	 * Return a wrapper around an {@link Inventory} or a {@link SidedInventory}.
	 *
	 * <p>Note: If the inventory is a {@link PlayerInventory}, this function will return a wrapper around all
	 * the slots of the player inventory except the cursor stack.
	 * This may cause insertion to insert arbitrary items into equipment slots or other unexpected behavior.
	 * To prevent this, {@link PlayerInventoryWrapper}'s specialized functions should be used instead.
	 *
	 * @param inventory The inventory to wrap.
	 * @param direction The direction to use if the access is sided, or {@code null} if the access is not sided.
	 */
	// TODO: should we throw if we receive a PlayerInventory? (it's probably a mistake)
	public static Storage<ItemKey> of(Inventory inventory, @Nullable Direction direction) {
		Objects.requireNonNull(inventory, "Null inventory is not supported.");
		return InventoryWrappersImpl.of(inventory, direction);
	}

	/**
	 * Return a wrapper around the inventory of a player.
	 * @see PlayerInventoryWrapper
	 */
	public static PlayerInventoryWrapper ofPlayer(PlayerEntity player) {
		Objects.requireNonNull(player, "Null player is not supported.");
		return ofPlayerInventory(player.inventory);
	}

	/**
	 * Return a wrapper around the inventory of a player.
	 * @see PlayerInventoryWrapper
	 */
	public static PlayerInventoryWrapper ofPlayerInventory(PlayerInventory playerInventory) {
		return (PlayerInventoryWrapper) of(playerInventory, null);
	}

	private InventoryWrappers() {
	}
}
