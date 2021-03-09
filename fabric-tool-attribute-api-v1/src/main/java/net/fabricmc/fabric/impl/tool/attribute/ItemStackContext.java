package net.fabricmc.fabric.impl.tool.attribute;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.LivingEntity;

public interface ItemStackContext {
	void fabricToolAttributes_setContext(@Nullable LivingEntity contextEntity);
}
