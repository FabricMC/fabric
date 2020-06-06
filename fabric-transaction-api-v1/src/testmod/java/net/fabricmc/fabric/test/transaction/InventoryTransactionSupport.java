package net.fabricmc.fabric.test.transaction;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.fabricmc.fabric.api.transaction.v1.FullCopyDataKey;
import net.fabricmc.fabric.api.transaction.v1.Transaction;

import java.util.Objects;

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
