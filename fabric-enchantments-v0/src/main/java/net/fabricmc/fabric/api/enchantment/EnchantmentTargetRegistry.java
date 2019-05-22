package net.fabricmc.fabric.api.enchantment;

import net.fabricmc.fabric.impl.enchantment.EnchantmentTargetRegistryImpl;

/**
 * Used to register custom enchantment targets.
 *
 * @see FabricEnchantment
 * @see FabricEnchantmentTarget
 */
public interface EnchantmentTargetRegistry {
	EnchantmentTargetRegistry INSTANCE = EnchantmentTargetRegistryImpl.INSTANCE;

	/**
	 * Registers a new enchantment target
	 * @param enchantmentTarget the custom target
	 */
	void register(FabricEnchantmentTarget enchantmentTarget);
}
