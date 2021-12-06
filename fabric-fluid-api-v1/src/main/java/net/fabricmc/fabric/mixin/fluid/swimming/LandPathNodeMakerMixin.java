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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.Tag;

import net.fabricmc.fabric.api.fluid.v1.util.FluidUtils;
import net.fabricmc.fabric.impl.fluid.FabricFluidEntity;

@Mixin(LandPathNodeMaker.class)
public class LandPathNodeMakerMixin {
	@Redirect(method = "getStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;isTouchingWater()Z"))
	private boolean isTouchingWaterRedirect(MobEntity mob) {
		/* If the entity is touching a swimmable fabric fluid, returns true,
			this will make the entity behave as if it were in water */
		return ((FabricFluidEntity) mob).isTouchingSwimmableFluid();
	}

	@Redirect(method = "getStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
	private boolean isOfRedirect(BlockState state, Block block) {
		//Makes the path maker algorithm able to find the Y level of the surface, in a lake of a swimmable fluid
		return FluidUtils.isSwimmable(state.getFluidState());
	}

	@Redirect(method = "getStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getFluidState()Lnet/minecraft/fluid/FluidState;", ordinal = 1))
	private FluidState getFluidStateRedirect(BlockState state) {
		//Makes the path maker algorithm able to find the Y level of the surface, in a lake of a swimmable fluid
		FluidState fluidState = state.getFluidState();

		if (fluidState.isStill() && !fluidState.get(FlowableFluid.FALLING) && FluidUtils.isSwimmable(fluidState)) {
			return Fluids.WATER.getStill(false);
		}

		return fluidState;
	}

	@Redirect(method = "getNodeTypeFromNeighbors", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private static boolean isInRedirect1(FluidState state, Tag<Fluid> tag) {
		//Make possible to detect if a neighbor position is a swimmable fluid
		return FluidUtils.isSwimmable(state);
	}

	@Redirect(method = "getCommonNodeType", at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z", ordinal = 1))
	private static boolean isInRedirect2(FluidState state, Tag<Fluid> tag) {
		/* Makes the path maker algorithm detect the swimmable fabric fluids as water
			so entities can move on them like they are in water */
		return FluidUtils.isSwimmable(state);
	}
}
