package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Callback for ticking a player's equipment.
 * Takes a player, the inventory to check, the slot ID, and the stack.
 * Can be extended to use any Inventory for use by Baubles-likes. Don't always assume a PlayerInventory.
 *
 * NOTE: The Identifier for vanilla equipment slots is "minecraft:{@link EquipmentSlot#getName()}".
 * Use {@link EquipmentSlot#byName(String)} to get the slot from the ID's path.
 */
public interface PlayerTickCallback {
	public static final Event<PlayerTickCallback> EVENT = EventFactory.createArrayBacked(PlayerTickCallback.class,
			(listeners) -> (player) -> {
				for (PlayerTickCallback event : listeners) {
					event.tick(player);
				}
			}
	);

	void tick(PlayerEntity player);
}
