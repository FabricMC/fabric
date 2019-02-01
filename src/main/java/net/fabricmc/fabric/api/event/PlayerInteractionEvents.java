package net.fabricmc.fabric.api.event;

import net.fabricmc.fabric.api.event.callbacks.PlayerInteractCallback;
import net.minecraft.util.ActionResult;

public class PlayerInteractionEvents {
	public static final Event<PlayerInteractCallback> ATTACK_BLOCK = EventFactory.arrayBacked(PlayerInteractCallback.class,
		(listeners) -> (player, world, hand, pos, direction) -> {
			for (PlayerInteractCallback event : listeners) {
				ActionResult result = event.interact(player, world, hand, pos, direction);
				if (result != ActionResult.PASS) {
					return result;
				}
			}

			return ActionResult.PASS;
		}
	);
}
