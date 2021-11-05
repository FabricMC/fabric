package net.fabricmc.fabric.mixin.fluid;

import net.fabricmc.fabric.impl.fluid.FabricFluidEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityNavigation.class)
public class EntityNavigationMixin {
	@Shadow @Final protected MobEntity entity;

	@Inject(method = "isInLiquid", at = @At("HEAD"), cancellable = true)
	private void isInLiquid(CallbackInfoReturnable<Boolean> cir) {
		//Add the fabric_fluid to the checklist of liquids
		if (((FabricFluidEntity)this.entity).isTouchingFabricFluid()) cir.setReturnValue(true);
	}
}
