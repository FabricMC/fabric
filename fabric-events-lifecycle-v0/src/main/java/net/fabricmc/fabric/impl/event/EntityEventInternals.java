package net.fabricmc.fabric.impl.event;

import com.google.common.base.Preconditions;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.entity.EntityTickCallback;
import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;

public class EntityEventInternals {

	/**Cache of entity class to {@code Event}'*/
	private static final Map<Class<?>, CascadingEvent> TICK_EVENTS = new HashMap<>();

	/**
	 * Retrieve an entity tick event for a given class.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Entity> CascadingEvent<EntityTickCallback<E>> getOrCreateEntityEvent(Class<?> entityClass) {
		Preconditions.checkArgument(Entity.class.isAssignableFrom(entityClass));
		CascadingEvent<EntityTickCallback<E>> event = TICK_EVENTS.get(entityClass);
		if (event == null) {
			event = new CascadingEvent<>(createEntityEvent());
			if (entityClass != Entity.class) {	// entities cannot inherit callbacks from anything
				Class<?> superclass = entityClass.getSuperclass();
				getOrCreateEntityEvent(superclass).registerCascading((Event) event);	// recursive call to init every parent
			}
			TICK_EVENTS.put(entityClass, event);
		}
		return event;
	}

	/**
	 * Create an {@code Event} for entity tick callbacks.
	 *
	 * This method has no side effects.
	 * @return a new Event to which {@code EntityTickCallback} can be registered
	 */
	@SuppressWarnings("unchecked")
	private static <E extends Entity> Event<EntityTickCallback<E>> createEntityEvent() {
		return (Event) EventFactory.createArrayBacked(EntityTickCallback.class,
			(EntityTickCallback[] listeners) -> {
				if (EventFactory.isProfilingEnabled()) {
					return (Entity player) -> {
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
					return (Entity player) -> {
						for (EntityTickCallback event : listeners) {
							event.tick(player);
						}
					};
				}
			});
	}
}
