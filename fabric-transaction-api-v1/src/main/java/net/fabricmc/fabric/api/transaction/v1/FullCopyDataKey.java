package net.fabricmc.fabric.api.transaction.v1;

/**
 * A {@link TransactionDataKey} that keeps track of the current state of the
 * target object by keeping a full copy of it. This is useful for implementing
 * transaction support for immutable objects such as block states, but will also
 * work the same for mutable objects.
 *
 * @param <T> the target object type
 */
public interface FullCopyDataKey<T> extends TransactionDataKey<T> {
	/**
	 * Gets the current state of the target object.
	 *
	 * @param ta the transaction to retrieve the object state from
	 * @return the current state of the object
	 */
	default T getCurrentState(Transaction ta) {
		ta.initWithParent(this, this::copy);
		return ta.computeIfAbsent(this, _k -> this.copy(this.getPersistentState()));
	}

	/**
	 * Gets the persistent state (the state outside of the transaction) of the
	 * target object.
	 *
	 * @return the persistent object state
	 */
	T getPersistentState();

	/**
	 * Returns a copy of the passed in value. May also return the original
	 * object if it is immutable.
	 *
	 * @param value the value to copy
	 * @return the new copy
	 */
	T copy(T value);

	@Override
	default void applyChangesToTransaction(Transaction ta, T changes) {
		ta.put(this, changes);
	}
}
