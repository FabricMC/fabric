package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Callback for ticking a player. Useful for updating effects given by a player's equipped items.
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
