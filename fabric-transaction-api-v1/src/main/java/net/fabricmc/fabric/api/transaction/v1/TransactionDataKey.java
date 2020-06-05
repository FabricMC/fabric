package net.fabricmc.fabric.api.transaction.v1;

/**
 * Classes implementing this interface should implement equals/hashCode accordingly.
 *
 * @param <T> the type of data the {@link Transaction} object stores for this instance
 */
public interface TransactionDataKey<T> {
	/**
	 * Apply changes to the parent transaction. This gets called when a nested
	 * transaction gets committed.
	 *
	 * @param ta      the parent transaction
	 * @param changes the changes to apply
	 */
	void applyChangesToTransaction(Transaction ta, T changes);

	/**
	 * Apply changes to the target. This gets called when a top level
	 * transaction gets committed.
	 *
	 * @param changes the changes to apply to the target
	 */
	void applyChanges(T changes);
}
