package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

/**
 * Callback for the player picking up an item on the ground.
 * Is hooked in before the item is picked up.
 * <p>
 * Upon return:
 * - SUCCESS cancels further processing and picks up the item.
 * - PASS falls back to further processing. If all listeners return PASS, the item is picked up.
 * - FAIL cancels further processing and does not pick up the item.
 */
public interface PlayerPickupItemCallback {
	public static final Event<PlayerPickupItemCallback> EVENT = EventFactory.createArrayBacked(PlayerPickupItemCallback.class,
		(listeners) -> (player, entity) -> {
			for (PlayerPickupItemCallback event : listeners) {
				ActionResult result = event.interact(player, entity);
				if (result != ActionResult.PASS) {
					return result;
				}
			}

			return ActionResult.PASS;
		}
	);

	ActionResult interact(PlayerEntity player, ItemEntity pickupEntity);
}
