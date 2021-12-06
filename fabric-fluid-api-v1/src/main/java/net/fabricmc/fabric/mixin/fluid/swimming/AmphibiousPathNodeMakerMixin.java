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

package net.fabricmc.fabric.mixin.fluid.swimming;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.mob.MobEntity;

import net.fabricmc.fabric.impl.fluid.FabricFluidEntity;

@Mixin(AmphibiousPathNodeMaker.class)
public class AmphibiousPathNodeMakerMixin {
	@Redirect(method = "method_37003", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;isTouchingWater()Z"))
	private boolean isTouchingWaterRedirect(MobEntity mob) {
		//Adds the behaviour of water to the swimmable fabric fluids, so the function can return the correct Y pos
		return ((FabricFluidEntity) mob).isTouchingSwimmableFluid();
	}
}
