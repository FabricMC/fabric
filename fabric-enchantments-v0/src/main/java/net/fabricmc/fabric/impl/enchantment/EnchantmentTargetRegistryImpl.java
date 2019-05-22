package net.fabricmc.fabric.impl.enchantment;

import net.fabricmc.fabric.api.enchantment.EnchantmentTargetRegistry;
import net.fabricmc.fabric.api.enchantment.FabricEnchantmentTarget;

import java.util.HashSet;
import java.util.Set;

public class EnchantmentTargetRegistryImpl implements EnchantmentTargetRegistry {
	public static final EnchantmentTargetRegistryImpl INSTANCE = new EnchantmentTargetRegistryImpl();

    public static final Set<FabricEnchantmentTarget> ENCHANTMENT_TARGETS = new HashSet<>();

	@Override
	public void register(FabricEnchantmentTarget enchantmentTarget) {
		ENCHANTMENT_TARGETS.add(enchantmentTarget);
	}
}
