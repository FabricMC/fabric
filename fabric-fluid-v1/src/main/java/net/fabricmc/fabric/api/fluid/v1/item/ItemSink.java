package net.fabricmc.fabric.api.fluid.v1.item;

import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.fluid.v1.Action;

/**
 * An interface through which items can be taken and removed from.
 * implementors: {@link net.minecraft.entity.player.PlayerInventory}
 */
public interface ItemSink {
	/**
	 * Take an amount of items from the item sink.
	 *
	 * @param stack the items to take
	 * @return the amount actually taken
	 */
	ItemStack take(ItemStack stack, Action action);

	/**
	 * Add an amount of items to the item sink.
	 *
	 * @param stack the items to add
	 */
	void push(ItemStack stack, Action action);
}
