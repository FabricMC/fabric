package net.fabricmc.fabric.impl.tools;

import net.fabricmc.fabric.api.tools.ToolActor;
import net.minecraft.entity.LivingEntity;

public class EntityToolActor implements ToolActor<LivingEntity> {
	private LivingEntity actor;

	public EntityToolActor(LivingEntity actor) {
		this.actor = actor;
	}

	@Override
	public LivingEntity getActor() {
		return actor;
	}
}
