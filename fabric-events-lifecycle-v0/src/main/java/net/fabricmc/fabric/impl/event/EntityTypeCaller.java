package net.fabricmc.fabric.impl.event;

import net.fabricmc.fabric.api.event.entity.EntityTickCallback;
import net.minecraft.entity.Entity;

public interface EntityTypeCaller<T extends Entity> {
	EntityTickCallback<T> getEntityEvent();
}
