package net.fabricmc.fabric.api.event.registry;

public enum RegistryAttribute {
	/**
	 * Registry will be saved to disk when modded.
	 */
	PERSISTED,

	/**
	 * Registry will be synced to the client when modded.
	 */
	SYNCED,

	/**
	 * Registry has been modded.
	 */
	MODDED
}
