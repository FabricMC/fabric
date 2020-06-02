package net.fabricmc.fabric.api.fluids.items;

import net.minecraft.item.ItemStack;

/**
 * an interface through which items can be taken and removed from
 * <p>
 * implementors:
 * {@link net.minecraft.entity.player.PlayerInventory}
 */
public interface ItemSink {
	/**
	 * take an amount of items from the item sink
	 *
	 * @param stack the items to take
	 * @return the amount actually taken
	 */
	ItemStack take(ItemStack stack, boolean simulate);

	/**
	 * add an amount of items to the item sink
	 *
	 * @param stack the items to add
	 */
	void push(ItemStack stack, boolean simulate);
}
