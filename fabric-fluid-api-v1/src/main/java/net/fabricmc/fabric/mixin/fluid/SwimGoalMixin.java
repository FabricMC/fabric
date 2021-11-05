package net.fabricmc.fabric.mixin.fluid;

import net.fabricmc.fabric.api.fluid.v1.tag.FabricFluidTags;
import net.fabricmc.fabric.impl.fluid.FabricFluidEntity;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SwimGoal.class)
public class SwimGoalMixin {
	@Shadow @Final private MobEntity mob;

	@Inject(method = "canStart()Z", at = @At("HEAD"), cancellable = true)
	private void canStart(CallbackInfoReturnable<Boolean> cir) {
		//If the entity is touching a swimmable fluid, can start swimming
		if (((FabricFluidEntity)this.mob).isTouchingSwimmableFluid() && this.mob.getFluidHeight(FabricFluidTags.FABRIC_FLUID) > this.mob.getSwimHeight()) {
			cir.setReturnValue(true);
		}
	}
}
