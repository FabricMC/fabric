package net.fabricmc.fabric.api.enchantment;

import net.minecraft.item.Item;

/**
 * Interface for an enchantment target
 */
public interface FabricEnchantmentTarget {
	/**
	 * Checks whether enchantments with this target can be applied to this item
	 * @param item the item to be decided on
	 * @return whether these enchantments are allowed to be applied to this item
	 */
	boolean isAcceptableItem(Item item);
}
