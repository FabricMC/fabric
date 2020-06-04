package net.fabricmc.fabric.api.fluid.v1.item;

import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.fluid.v1.container.FluidContainer;

/**
 * A fluid container implemented by items.
 */
public interface ItemFluidContainer {
	/**
	 * Get a mutable fluid container for the given itemstack.
	 *
	 * @param waste a place for empty containers to go, or for empty containers to come from
	 * @return the fluid container for the item
	 */
	FluidContainer getContainer(ItemSink waste, ItemStack stack);
}
