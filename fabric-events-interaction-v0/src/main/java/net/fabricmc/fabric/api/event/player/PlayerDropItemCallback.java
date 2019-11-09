package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

/**
 * Callback for the player dropping an item.
 * Is hooked in before the item is dropped.
 * <p>
 * Upon return:
 * - SUCCESS cancels further processing and drops the item.
 * - PASS falls back to further processing.
 * - FAIL cancels further processing and does not drop the item.
 */
public interface PlayerDropItemCallback {
	public static final Event<PlayerDropItemCallback> EVENT = EventFactory.createArrayBacked(PlayerDropItemCallback.class,
		(listeners) -> (player, stack) -> {
			for (PlayerDropItemCallback event : listeners) {
				ActionResult result = event.interact(player, stack);
				if (result != ActionResult.PASS) {
					return result;
				}
			}

			return ActionResult.PASS;
		}
	);

	ActionResult interact(PlayerEntity player, ItemStack stack);
}
