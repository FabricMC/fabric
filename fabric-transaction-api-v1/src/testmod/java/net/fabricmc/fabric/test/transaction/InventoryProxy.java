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

package net.fabricmc.fabric.test.transaction;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class InventoryProxy implements Inventory {
	private final Inventory delegate;
	private final DefaultedList<ItemStack> stacks;

	private InventoryProxy(Inventory delegate, DefaultedList<ItemStack> stacks) {
		this.delegate = delegate;
		this.stacks = stacks;
	}

	public static InventoryProxy copyOf(Inventory inv) {
		Inventory delegate = inv;

		while (delegate instanceof InventoryProxy) {
			delegate = ((InventoryProxy) delegate).delegate;
		}

		DefaultedList<ItemStack> stacks = DefaultedList.ofSize(inv.size(), ItemStack.EMPTY);

		for (int i = 0; i < inv.size(); i++) {
			stacks.set(i, inv.getStack(i).copy());
		}

		return new InventoryProxy(delegate, stacks);
	}

	@Override
	public int size() {
		return this.stacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : this.stacks) {
			if (!stack.isEmpty()) return false;
		}

		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.stacks.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return Inventories.splitStack(this.stacks, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack itemStack = this.stacks.get(slot);

		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.stacks.set(slot, ItemStack.EMPTY);
			return itemStack;
		}
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.stacks.set(slot, stack);

		if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}
	}

	@Override
	public int getMaxCountPerStack() {
		return this.delegate.getMaxCountPerStack();
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return this.delegate.canPlayerUse(player);
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		return this.delegate.isValid(slot, stack);
	}

	@Override
	public void clear() {
		this.stacks.clear();
	}
}
