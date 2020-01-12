package net.fabricmc.fabric.api.crafting.v1;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface FabricRecipeRemainder {
	/**
	 * An {@link ItemStack} aware version of {@link Item#getRecipeRemainder()}.
	 *
	 * @param stack The input {@link ItemStack} for the current item
	 * @param craftingInventory the {@link CraftingInventory} that the stack is part of
	 * @param playerEntity the {@link PlayerEntity} that is doing the craft, may be null if automation is used.
	 * @return The {@link ItemStack} to remain in the crafting inventory
	 */
	ItemStack getRemainder(ItemStack stack, CraftingInventory craftingInventory, PlayerEntity playerEntity);
}
