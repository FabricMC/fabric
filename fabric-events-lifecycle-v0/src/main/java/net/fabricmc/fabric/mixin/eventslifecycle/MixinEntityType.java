package net.fabricmc.fabric.mixin.eventslifecycle;

import net.fabricmc.fabric.api.event.entity.EntityTickCallback;
import net.fabricmc.fabric.impl.event.EntityTypeCaller;
import net.fabricmc.fabric.impl.event.EventUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityType.class)
public class MixinEntityType<T extends Entity> implements EntityTypeCaller {
	private EntityTickCallback event;

	@Override
	public EntityTickCallback getEntityEvent() {
		if (event == null) {
			event = EventUtil.createEntityEvent();
		}
		return event;
	}
}
