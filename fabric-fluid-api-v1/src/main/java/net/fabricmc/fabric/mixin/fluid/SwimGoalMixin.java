/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.fluid;

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

	@Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
	private void canStart(CallbackInfoReturnable<Boolean> cir) {
		//If the entity is touching a swimmable fluid, can start swimming
		FabricFluidEntity entity = (FabricFluidEntity)this.mob;
		if (entity.isTouchingSwimmableFluid() && entity.getFabricFluidHeight() > this.mob.getSwimHeight()) {
			cir.setReturnValue(true);
		}
	}
}
