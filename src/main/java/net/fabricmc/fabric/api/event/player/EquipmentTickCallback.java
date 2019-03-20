package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

/**
 * Callback for ticking a player's equipment.
 * Takes a player, the inventory to check, the slot number (see below), and the stack.
 * Can be extended to use any Inventory for use by Baubles-likes. Don't always assume a PlayerInventory.
 *
 * NOTE: the slot number that's passed for player held items/armor is the {@link EquipmentSlot#getEntitySlotId()} value.
 * This overlaps between hand and armor equipment due to the way {@link net.minecraft.entity.player.PlayerInventory} works.
 * Use an `if stack == {@link net.minecraft.entity.player.PlayerInventory#getArmorStack(int)}` or
 * `if stack == {@link net.minecraft.entity.player.PlayerEntity#getStackInHand(Hand)} )}` to make sure you're checking
 * the proper place for the ticked item.
 */
public interface EquipmentTickCallback {
	public static final Event<EquipmentTickCallback> EVENT = EventFactory.createArrayBacked(EquipmentTickCallback.class,
			(listeners) -> (player, inv, slotNum, stack) -> {
				for (EquipmentTickCallback event : listeners) {
					event.tick(player, inv, slotNum, stack);
				}
			}
	);

	void tick(PlayerEntity player, Inventory inv, int slotNumber, ItemStack stack);
}
