package net.fabricmc.fabric.api.event.registry;

public enum RegistryAttribute {
	/**
	 * Registry will be saved to disk when modded.
	 */
	PERSISTENT,

	/**
	 * Registry will be synced to the client when modded.
	 */
	SYNC,

	/**
	 * Registry has been modded.
	 */
	MODDED
}
