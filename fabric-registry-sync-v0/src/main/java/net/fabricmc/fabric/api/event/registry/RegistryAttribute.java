package net.fabricmc.fabric.api.event.registry;

public enum RegistryAttribute {

	/**
	 * Registry will be saved to disk
	 */
	PERSISTENT,

	/**
	 * Registry will be synced to the client
	 */
	SYNC
}
