package net.fabricmc.fabric.impl.event;

import net.fabricmc.fabric.api.event.entity.EntityTickCallback;

public interface EntityTypeCaller {
	EntityTickCallback getEntityEvent();
}
