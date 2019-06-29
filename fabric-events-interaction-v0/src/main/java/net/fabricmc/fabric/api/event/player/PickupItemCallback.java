package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

/**
 * Callback for picking up an item.
 * Is hooked in before the item is picked up.
 * <p>
 * Upon return:
 * - SUCCESS cancels further processing and picks up the item.
 * - PASS falls back to further processing. If all listeners return PASS, the item is picked up.
 * - FAIL cancels further processing and does not pick up the item.
 */
public interface PickupItemCallback {
	public static final Event<PickupItemCallback> EVENT = EventFactory.createArrayBacked(PickupItemCallback.class,
		(listeners) -> (player, entity) -> {
			for (PickupItemCallback event : listeners) {
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
