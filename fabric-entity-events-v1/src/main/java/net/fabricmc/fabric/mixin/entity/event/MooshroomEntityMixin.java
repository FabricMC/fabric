package net.fabricmc.fabric.mixin.entity.event;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.MooshroomEntity;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

@Mixin(MooshroomEntity.class)
class MooshroomEntityMixin {
	@ModifyArg(
			method = "sheared",
			at = @At(ordinal = 0, value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z")
	)
	private Entity afterMooshroomConversion(Entity converted) {
		ServerLivingEntityEvents.MOB_CONVERSION.invoker().onConversion((MooshroomEntity) (Object) this, (MobEntity) converted, false);
		return converted;
	}
}
