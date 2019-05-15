package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.profiler.Profiler;

/**
 * Callback for ticking a player. Useful for updating effects given by a player's equipped items.
 */
public interface PlayerTickCallback {
	public static final Event<PlayerTickCallback> EVENT = EventFactory.createArrayBacked(PlayerTickCallback.class,
		(listeners) -> {
			if (EventFactory.isProfilingEnabled()) {
				return (player) -> {
					Profiler profiler = player.getServer().getProfiler();
					profiler.push("fabricPlayerTick");
					for (PlayerTickCallback event : listeners) {
						profiler.push(EventFactory.getHandlerName(event));
						event.tick(player);
						profiler.pop();
					}
					profiler.pop();
				};
			} else {
				return (player) -> {
					for (PlayerTickCallback event : listeners) {
						event.tick(player);
					}
				};
			}
		}
	);

	void tick(PlayerEntity player);
}
