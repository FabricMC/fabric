package net.fabricmc.fabric.api.enchantment;

import net.minecraft.enchantment.EnchantmentTarget;

/**
 * Implement this interface on your enchantments if you like them to use a custom enchantment type.<br />
 * Your enchantments should have a vanilla enchantment target of {@link EnchantmentTarget#ALL}.
 */
public interface FabricEnchantment {
	/**
	 * Returns a custom enchantment type
	 * @return your own enchantment type
	 */
	FabricEnchantmentTarget getEnchantmentTarget();
}
