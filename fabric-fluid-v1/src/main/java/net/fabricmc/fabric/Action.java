package net.fabricmc.fabric;

/**
 * describes the behavior of a fluid transaction
 */
public enum Action {
	/**
	 * actually mutate and update the container's state.
	 */
	PERFORM,

	/**
	 * simulate the transaction, do not mutate the state of the container itself.
	 */
	SIMULATE;

	public boolean simulate() {
		return this == SIMULATE;
	}

	public boolean perform() {
		return this == PERFORM;
	}
}
