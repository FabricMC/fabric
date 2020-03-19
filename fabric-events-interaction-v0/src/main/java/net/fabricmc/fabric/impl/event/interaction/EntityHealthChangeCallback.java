package net.fabricmc.fabric.impl.event.interaction;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;

public interface EntityHealthChangeCallback {
	/**
	 * Callback for entity health change. Triggered whenever the game updates the entity's health. Returns the entity
	 * and it's new health
	 */
	Event<EntityHealthChangeCallback> EVENT = EventFactory.createArrayBacked(EntityHealthChangeCallback.class,
			(listeners) -> (entity, health) -> {
				for (EntityHealthChangeCallback event : listeners) {
					ActionResult result = event.health(entity, health);
					if (result != ActionResult.PASS) {
						return result;
					}
				}
				return ActionResult.PASS;
			});

	ActionResult health(LivingEntity entity, float health);
}
