package net.fabricmc.fabric.test.attachment.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;

@Mixin(ZombieEntity.class)
public interface ZombieEntityAccessor {
	@Invoker("convertTo")
	void convertTo(EntityType<? extends ZombieEntity> entityType);
}
