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

import java.util.Objects;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.transaction.v1.FullCopyDataKey;
import net.fabricmc.fabric.api.transaction.v1.Transaction;

public class InventoryTransactionSupport {
	private final DataKey key;

	private InventoryTransactionSupport(Inventory inventory) {
		this.key = new DataKey(inventory);
	}

	public static InventoryTransactionSupport create(Inventory inventory) {
		return new InventoryTransactionSupport(inventory);
	}

	public Inventory getCurrentState(Transaction ta) {
		return this.key.getCurrentState(ta);
	}

	private static final class DataKey implements FullCopyDataKey<Inventory> {
		private final Inventory target;

		private DataKey(Inventory target) {
			this.target = target;
		}

		@Override
		public Inventory getPersistentState() {
			return this.target;
		}

		@Override
		public Inventory copy(Inventory value) {
			return InventoryProxy.copyOf(value);
		}

		@Override
		public void applyChanges(Inventory changes) {
			for (int i = 0; i < this.target.size(); i++) {
				ItemStack stack = changes.getStack(i);

				if (!ItemStack.areEqual(stack, this.target.getStack(i))) {
					this.target.setStack(i, stack);
				}
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			DataKey dataKey = (DataKey) o;
			return target.equals(dataKey.target);
		}

		@Override
		public int hashCode() {
			return Objects.hash(target);
		}
	}
}
