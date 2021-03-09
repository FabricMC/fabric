package net.fabricmc.fabric.api.tool.attribute.v1;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.LivingEntity;

public interface ItemStackContext {
	void fabricToolAttributes_setContext(@Nullable LivingEntity contextEntity);
}
