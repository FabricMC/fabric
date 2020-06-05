package net.fabricmc.fabric.api.transaction.v1;

public class TransactionTracker {
	public static final TransactionTracker INSTANCE = new TransactionTracker();

	private Transaction current;

	/**
	 * Create a new transaction.
	 *
	 * <p>Calling this will invalidate any existing transactions.
	 *
	 * @return the new transaction
	 */
	public Transaction create() {
		if (this.current != null) {
			this.current.invalidate();
		}

		Transaction ta = new Transaction(null);
		this.current = ta;
		return ta;
	}
}
