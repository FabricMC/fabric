package net.fabricmc.fabric.api.crafting.v1;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ItemCraftCallback {
	Event<ItemCraftCallback> EVENT = EventFactory.createArrayBacked(ItemCraftCallback.class, (listeners) -> (stack, craftingInventory, playerEntity) -> {
		for (ItemCraftCallback callback : listeners) {
			callback.onCraft(stack, craftingInventory, playerEntity);
		}
	});

	/**
	 * Called when an item is crafted in a crafting table.
	 *
	 * @param stack the {@link ItemStack} that is the output of the crafting recipe
	 * @param craftingInventory the {@link CraftingInventory} that will still contain the ingredients for the crafting recipe
	 * @param playerEntity the {@link PlayerEntity} who is crafting the item, can be null if an automated method of crafting is used.
	 */
	void onCraft(ItemStack stack, CraftingInventory craftingInventory, PlayerEntity playerEntity);
}
