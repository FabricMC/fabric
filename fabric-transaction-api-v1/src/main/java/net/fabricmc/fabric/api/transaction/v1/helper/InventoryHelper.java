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

package net.fabricmc.fabric.api.transaction.v1.helper;

import java.util.function.Consumer;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.transaction.v1.TransactionContext;
import net.fabricmc.fabric.api.transaction.v1.TransactionParticipant.TransactionDelegate;

/**
 * Wrapper to enable transaction support on vanilla inventories.
 * Meant to be used via default methods of {@link TransactionalInventory} but
 * exposed for direct by use by other implementations.
 *
 * <p>This wrapper is naive and allocates on prepare each time. Mods that need
 * performance at scale or that have bespoke {@code Inventory} implementations are encouraged to
 * implement {@link TransactionDelegate} directly.
 */
public class InventoryHelper {
	private static class InventoryDelegate implements TransactionDelegate, Consumer<TransactionContext> {
		private final Inventory inventory;

		private InventoryDelegate(Inventory inventory) {
			this.inventory = inventory;
		}

		@Override
		public Consumer<TransactionContext> prepareCompletionHandler(TransactionContext context) {
			final int size = inventory.size();
			final ItemStack[] state = new ItemStack[size];

			for (int i = 0; i < size; i++) {
				state[i] = inventory.getStack(i).copy();
			}

			context.setState(state);
			return this;
		}

		@Override
		public void accept(TransactionContext context) {
			if (!context.isCommited()) {
				final int size = inventory.size();
				final ItemStack[] stacks = (ItemStack[]) context.getState();

				for (int i = 0; i < size; i++) {
					final ItemStack myStack = inventory.getStack(i);
					final ItemStack stateStack = stacks[i];

					if ((myStack.isEmpty() && stateStack.isEmpty()) || (myStack.isItemEqual(stateStack) && myStack.getCount() == stateStack.getCount()) ) {
						continue;
					}

					inventory.setStack(i, stateStack);
				}
			}
		}
	}

	/**
	 * Creates a transaction delegate for the given inventory to use within
	 * transactions. The delegate is not self-enlisting and should require no
	 * special handling for nested transactions.
	 *
	 * @param inventory Inventory for which changes should be made atomic
	 * @return the delegate
	 */
	public static TransactionDelegate prepareDelegate(Inventory inventory) {
		return new InventoryDelegate(inventory);
	}
}
