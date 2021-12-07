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

package net.fabricmc.fabric.mixin.fluid.breathing;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.mob.WaterCreatureEntity;

import net.fabricmc.fabric.impl.fluid.LivingEntityFluidExtensions;

@Mixin(WaterCreatureEntity.class)
public class WaterCreatureEntityMixin {
	@Redirect(method = "tickWaterBreathingAir", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/WaterCreatureEntity;isInsideWaterOrBubbleColumn()Z"))
	private boolean isInsideWaterOrBubbleColumnRedirect(WaterCreatureEntity entity) {
		//Checks if the entity is touching a fluid breathable by aquatic entities, so it will not lose air
		return ((LivingEntityFluidExtensions) entity).isTouchingBreathableByAquaticFluid(false);
	}
}
