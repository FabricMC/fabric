package net.fabricmc.fabric.impl.registry;

public final class RegistryAttributeTracking {
	/**
	 * Set to true after vanilla's bootstrap has completed.
	 *
	 * <p>This field should not be modified by mods
	 */
	private static boolean postBootstrap = false;

	public static void bootstrapRegistries() {
		postBootstrap = true;
	}

	public static boolean isBootstrapped() {
		return postBootstrap;
	}
}
