package net.fabricmc.fabric.api.fluid.v1;

/**
 * Describes the behavior of a fluid transaction.
 */
public enum Action {
	/**
	 * Actually mutate and update the container's state.
	 */
	PERFORM,

	/**
	 * Simulate the transaction, do not mutate the state of the container itself.
	 */
	SIMULATE;

	public boolean isSimulation() {
		return this == SIMULATE;
	}

	public boolean shouldPerform() {
		return this == PERFORM;
	}
}
