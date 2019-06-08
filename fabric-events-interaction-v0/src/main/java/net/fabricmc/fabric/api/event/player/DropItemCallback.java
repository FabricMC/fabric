package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

/**
 * Callback for dropping an item.
 * Is hooked in before the item is dropped.
 * <p>
 * Upon return:
 * - SUCCESS cancels further processing and drops the item.
 * - PASS falls back to further processing.
 * - FAIL cancels further processing and does not drop the item.
 */
public interface DropItemCallback {
	public static final Event<DropItemCallback> EVENT = EventFactory.createArrayBacked(DropItemCallback.class,
		(listeners) -> (player, world, stack) -> {
			for (DropItemCallback event : listeners) {
				ActionResult result = event.interact(player, world, stack);
				if (result != ActionResult.PASS) {
					return result;
				}
			}

			return ActionResult.PASS;
		}
	);

	ActionResult interact(PlayerEntity player, World world, ItemStack stack);
}
