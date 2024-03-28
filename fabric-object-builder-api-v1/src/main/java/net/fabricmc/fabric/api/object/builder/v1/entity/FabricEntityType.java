package net.fabricmc.fabric.api.object.builder.v1.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public interface FabricEntityType {
	interface Builder<T extends Entity> {
		EntityType.Builder<T> alwaysUpdateVelocity(boolean alwaysUpdateVelocity);
	}
}
