package net.fabricmc.fabric.api.event.entity;

import net.fabricmc.fabric.impl.event.EntityTypeCaller;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

/**
 * Callback for ticking an entity.
 */
public interface EntityTickCallback<T extends Entity> {
	public static <T extends Entity> EntityTickCallback<T> event(EntityType<T> type) {
		return ((EntityTypeCaller)type).getEntityEvent();
	}

	void tick(Entity entity);
}
