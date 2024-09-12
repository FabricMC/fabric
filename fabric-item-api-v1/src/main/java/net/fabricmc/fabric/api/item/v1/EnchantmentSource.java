package net.fabricmc.fabric.api.item.v1;

/**
 * Determines where an enchantment has been loaded from
 */
public enum EnchantmentSource {
	/**
	 * An enchantment loaded from the vanilla data pack
	 */
	VANILLA(true),
	/**
	 * An enchantment loaded from mods' bundled resources.
	 *
	 * <p>This includes the additional builtin data packs registered by mods
	 * with Fabric Resource Loader.
	 */
	MOD(true),
	/**
	 * An enchantment loaded from an external data pack.
	 */
	DATA_PACK(false);

	private final boolean builtin;

	EnchantmentSource(boolean builtin) {
		this.builtin = builtin;
	}

	/**
	 * Returns whether this enchantment source is builtin and bundled in the vanilla or mod resources.
	 *
	 * <p>{@link #VANILLA} and {@link #MOD} are builtin.
	 *
	 * @return {@code true} if builtin, {@code false} otherwise
	 */
	public boolean isBuiltin() {
		return builtin;
	}
}
