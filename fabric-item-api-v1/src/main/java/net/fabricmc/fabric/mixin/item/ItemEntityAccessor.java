package net.fabricmc.fabric.mixin.item;

import net.minecraft.entity.ItemEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntity.class)
public interface ItemEntityAccessor {
	@Accessor("DESPAWN_AGE")
	static int getDespawnAge() {
		throw new AssertionError();
	}
}
