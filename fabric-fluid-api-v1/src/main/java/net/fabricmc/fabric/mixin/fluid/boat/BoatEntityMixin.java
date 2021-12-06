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

package net.fabricmc.fabric.mixin.fluid.boat;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;
import net.fabricmc.fabric.api.fluid.v1.util.FluidUtils;
import net.fabricmc.fabric.impl.fluid.FabricFluidEntity;

@Mixin(BoatEntity.class)
public class BoatEntityMixin {
	@Redirect(method = "method_7544", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInRedirect1(FluidState state, Tag<Fluid> tag) {
		//Enable boat floating on navigable fluids
		return FluidUtils.isNavigable(state);
	}

	@Redirect(method = "checkBoatInWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInRedirect2(FluidState state, Tag<Fluid> tag) {
		//Adds the navigable fabric fluids to the valid fluids for checking if the boat is on it
		return FluidUtils.isNavigable(state);
	}

	@Redirect(method = "getUnderWaterLocation", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInRedirect3(FluidState state, Tag<Fluid> tag) {
		//Adds the navigable fabric fluids to the valid fluids for checking if the boat is submerged by it
		return FluidUtils.isNavigable(state);
	}

	@Redirect(method = "fall", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInRedirect4(FluidState state, Tag<Fluid> tag) {
		//If the boat falls on a navigable fluid, it can prevent fall damage
		return FluidUtils.isNavigable(state);
	}

	@Redirect(method = "updatePassengerForDismount", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isWater(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean isWaterRedirect(World world, BlockPos pos) {
		//Enable dismounting passengers on navigable fluids
		return FluidUtils.isNavigable(world.getFluidState(pos));
	}

	@Redirect(method = "canAddPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;isSubmergedIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isSubmergedInRedirect(BoatEntity boat, Tag<Fluid> tag) {
		//If the boat is submerged by any fluid, it cannot get passengers
		return ((FabricFluidEntity) getThis()).isSubmergedInFluid();
	}

	@Inject(method = "getPaddleSoundEvent", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
	private void getPaddleSoundEvent(CallbackInfoReturnable<SoundEvent> cir) {
		//If the boat is touching a fabric fluid, gets the paddle sound and returns it
		if (((FabricFluidEntity) getThis()).isTouchingFabricFluid()) {
			//Gets the paddle sound
			FabricFlowableFluid fluid = (FabricFlowableFluid) ((FabricFluidEntity) getThis()).getFirstTouchedFabricFluid().getFluid();
			Optional<SoundEvent> paddleSound = fluid.getPaddleSound();
			paddleSound.ifPresentOrElse(cir::setReturnValue, () -> cir.setReturnValue(null));
		}
	}

	@Unique
	private BoatEntity getThis() {
		return (BoatEntity) (Object) this;
	}
}
