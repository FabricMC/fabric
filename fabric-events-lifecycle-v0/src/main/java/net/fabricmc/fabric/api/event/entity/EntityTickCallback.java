package net.fabricmc.fabric.api.event.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.event.EntityEventInternals;
import net.minecraft.entity.Entity;

/**
 * Callback for ticking an entity.
 */
public interface EntityTickCallback<E extends Entity> {
	public static <E extends Entity> Event<EntityTickCallback<E>> event(Class<E> type) {
		return EntityEventInternals.getOrCreateEntityEvent(type);
	}

	void tick(E entity);
}
