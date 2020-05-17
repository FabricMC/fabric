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

package net.fabricmc.fabric.test.screenhandler.item;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

/**
 * A simple {@code SidedInventory} implementation with only default methods + an item list getter.
 *
 * <h2>Reading and writing to tags</h2>
 * Use {@link Inventories#fromTag(CompoundTag, DefaultedList)} and {@link Inventories#toTag(CompoundTag, DefaultedList)}
 * on {@linkplain #getItems() the item list}.
 *
 * @author Juuz
 */
@FunctionalInterface
public interface ImplementedInventory extends SidedInventory {
	/**
	 * Gets the item list of this inventory.
	 * Must return the same instance every time it's called.
	 *
	 * @return the item list
	 */
	DefaultedList<ItemStack> getItems();

	// Creation

	/**
	 * Creates an inventory from the item list.
	 *
	 * @param items the item list
	 * @return a new inventory
	 */
	static ImplementedInventory of(DefaultedList<ItemStack> items) {
		return () -> items;
	}

	/**
	 * Creates a new inventory with the size.
	 *
	 * @param size the inventory size
	 * @return a new inventory
	 */
	static ImplementedInventory ofSize(int size) {
		return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
	}

	// SidedInventory

	/**
	 * Gets the available slots to automation on the side.
	 *
	 * <p>The default implementation returns an array of all slots.
	 *
	 * @param side the side
	 * @return the available slots
	 */
	@Override
	default int[] getAvailableSlots(Direction side) {
		int[] result = new int[getItems().size()];

		for (int i = 0; i < result.length; i++) {
			result[i] = i;
		}

		return result;
	}

	/**
	 * Returns true if the stack can be inserted in the slot at the side.
	 *
	 * <p>The default implementation returns true.
	 *
	 * @param slot  the slot
	 * @param stack the stack
	 * @param side  the side
	 * @return true if the stack can be inserted
	 */
	@Override
	default boolean canInsert(int slot, ItemStack stack, Direction side) {
		return true;
	}

	/**
	 * Returns true if the stack can be extracted from the slot at the side.
	 *
	 * <p>The default implementation returns true.
	 *
	 * @param slot  the slot
	 * @param stack the stack
	 * @param side  the side
	 * @return true if the stack can be extracted
	 */
	@Override
	default boolean canExtract(int slot, ItemStack stack, Direction side) {
		return true;
	}

	// Inventory

	/**
	 * Returns the inventory size.
	 *
	 * <p>The default implementation returns the size of {@link #getItems()}.
	 *
	 * @return the inventory size
	 */
	@Override
	default int size() {
		return getItems().size();
	}

	/**
	 * @return true if this inventory has only empty stacks, false otherwise
	 */
	@Override
	default boolean isEmpty() {
		for (int i = 0; i < size(); i++) {
			ItemStack stack = getStack(i);

			if (!stack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Gets the item in the slot.
	 *
	 * @param slot the slot
	 * @return the item in the slot
	 */
	@Override
	default ItemStack getStack(int slot) {
		return getItems().get(slot);
	}

	/**
	 * Takes a stack of the size from the slot.
	 *
	 * <p>(default implementation) If there are less items in the slot than what are requested,
	 * takes all items in that slot.
	 *
	 * @param slot  the slot
	 * @param count the item count
	 * @return a stack
	 */
	@Override
	default ItemStack removeStack(int slot, int count) {
		ItemStack result = Inventories.splitStack(getItems(), slot, count);

		if (!result.isEmpty()) {
			markDirty();
		}

		return result;
	}

	/**
	 * Removes the current stack in the {@code slot} and returns it.
	 *
	 * <p>The default implementation uses {@link Inventories#removeStack(List, int)}
	 *
	 * @param slot the slot
	 * @return the removed stack
	 */
	@Override
	default ItemStack removeStack(int slot) {
		return Inventories.removeStack(getItems(), slot);
	}

	/**
	 * Replaces the current stack in the {@code slot} with the provided stack.
	 *
	 * <p>If the stack is too big for this inventory ({@link Inventory#getMaxCountPerStack()} ()}),
	 * it gets resized to this inventory's maximum amount.
	 *
	 * @param slot  the slot
	 * @param stack the stack
	 */
	@Override
	default void setStack(int slot, ItemStack stack) {
		getItems().set(slot, stack);

		if (stack.getCount() > getMaxCountPerStack()) {
			stack.setCount(getMaxCountPerStack());
		}
	}

	/**
	 * Clears {@linkplain #getItems() the item list}}.
	 */
	@Override
	default void clear() {
		getItems().clear();
	}

	@Override
	default void markDirty() {
		// Override if you want behavior.
	}

	@Override
	default boolean canPlayerUse(PlayerEntity player) {
		return true;
	}
}
