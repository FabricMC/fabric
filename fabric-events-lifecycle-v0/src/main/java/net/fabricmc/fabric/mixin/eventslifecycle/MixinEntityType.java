package net.fabricmc.fabric.mixin.eventslifecycle;

import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.entity.EntityTickCallback;
import net.fabricmc.fabric.impl.event.EntityTypeCaller;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityType.class)
public class MixinEntityType<T extends Entity> implements EntityTypeCaller {

	@Override
	public EntityTickCallback getEntityEvent() {
		return (EntityTickCallback<T>)EventFactory.createArrayBacked(EntityTickCallback.class,
			(listeners) -> {
				if (EventFactory.isProfilingEnabled()) {
					return (player) -> {
						Profiler profiler = player.getServer().getProfiler();
						profiler.push("fabricEntityTick");
						for (EntityTickCallback event : listeners) {
							profiler.push(EventFactory.getHandlerName(event));
							event.tick(player);
							profiler.pop();
						}
						profiler.pop();
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
