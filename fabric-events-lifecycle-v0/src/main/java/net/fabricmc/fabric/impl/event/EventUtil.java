package net.fabricmc.fabric.impl.event;

import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.entity.EntityTickCallback;
import net.minecraft.util.profiler.Profiler;

public class EventUtil {
	public static EntityTickCallback createEntityEvent() {
		return (EntityTickCallback)EventFactory.createArrayBacked(EntityTickCallback.class,
			(listeners) -> {
				if (EventFactory.isProfilingEnabled()) {
					return (player) -> {
						if (player.getServer() != null) {
							Profiler profiler = player.getServer().getProfiler();
							profiler.push("fabricEntityTick");
							for (EntityTickCallback event : listeners) {
								profiler.push(EventFactory.getHandlerName(event));
								event.tick(player);
								profiler.pop();
							}
							profiler.pop();
						}
					};
				} else {
					return (player) -> {
						for (EntityTickCallback event : listeners) {
							event.tick(player);
						}
					};
				}
			});
	}
}
