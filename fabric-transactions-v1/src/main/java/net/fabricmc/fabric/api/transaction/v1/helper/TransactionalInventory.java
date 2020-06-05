package net.fabricmc.fabric.api.transaction.v1.helper;

import net.minecraft.inventory.Inventory;

import net.fabricmc.fabric.api.transaction.v1.TransactionParticipant;

/**
 * Vanilla {@Inventory} with default support for transaction control.
 *
 * <p>The default implementation is naive and allocates on prepare each time. Mods that need
 * performance at scale or that have bespoke {@code Inventory} implementations are encouraged to
 * implement {@link TransactionParticipant} directly.
 */
public interface TransactionalInventory extends Inventory, TransactionParticipant {
	@Override
	default boolean isSelfEnlisting() {
		return false;
	}

	@Override
	default TransactionDelegate getTransactionDelegate() {
		return InventoryHelper.prepareDelegate(this);
	}
}
