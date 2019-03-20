package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * Callback for ticking a player's equipment.
 * Takes a player, the inventory to check, the slot ID, and the stack.
 * Can be extended to use any Inventory for use by Baubles-likes. Don't always assume a PlayerInventory.
 *
 * NOTE: The Identifier for vanilla equipment slots is "minecraft:{@link EquipmentSlot#getName()}".
 * Use {@link EquipmentSlot#byName(String)} to get the slot from the ID's path.
 */
public interface EquipmentTickCallback {
	public static final Event<EquipmentTickCallback> EVENT = EventFactory.createArrayBacked(EquipmentTickCallback.class,
			(listeners) -> (player, inv, slotId, stack) -> {
				for (EquipmentTickCallback event : listeners) {
					event.tick(player, inv, slotId, stack);
				}
			}
	);

	void tick(PlayerEntity player, Inventory inv, Identifier slotId, ItemStack stack);
}
