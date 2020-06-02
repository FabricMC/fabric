package net.fabricmc.fabric.api.fluids.items;

import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.fluids.containers.FluidContainer;

/**
 * a fluid container implemented by items
 * <p>
 * todo vanilla fluid containers:
 * item name             | fluid type
 * -----------------------------------
 * splash potion         : potion
 * bottle of enchanting  : experience
 * bucket                : empty
 * bucket of cod/puff/sal: water (fish byproduct) todo
 * lava bucket           : lava
 * water bucket          : water
 * lingering potion      : potion
 * potion bottle         : potion
 * milk bucket           : milk
 * glass bottle          : empty
 * mushroom stew         : mushroom_stew
 * bowl                  : empty
 * suspicious stew       : suspicious_stew
 * honey bottle          : honey
 * dragon's breath       : dragon_breath todo
 */
public interface ItemFluidContainer {

	/**
	 * get a mutable fluid container for the given itemstack
	 *
	 * @param waste a place for empty containers to go, or for empty containers to come from
	 * @return the fluid container for the item
	 */
	FluidContainer getContainer(ItemSink waste, ItemStack stack);
}
