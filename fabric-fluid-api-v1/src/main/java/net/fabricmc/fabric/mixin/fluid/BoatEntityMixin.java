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

import net.fabricmc.fabric.api.fluid.v1.util.FluidUtils;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.Tag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends EntityMixin {
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
		//If the boat falls on a navigable fluid it can prevent fall damage.
		return FluidUtils.isNavigable(state);
	}

	@Redirect(method = "updatePassengerForDismount", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isWater(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean isWaterRedirect(World world, BlockPos pos) {
		//Enable dismounting passengers on navigable fluids
		return FluidUtils.isNavigable(world.getFluidState(pos));
	}

	@Redirect(method = "canAddPassenger", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;isSubmergedIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isSubmergedInRedirect(BoatEntity boat, Tag<Fluid> tag) {
		//If the boat is submerged by any fluid it cannot get passengers
		return this.isSubmerged();
	}

	@Unique
	private boolean isSubmerged() {
		return this.submergedFluid != null;
	}
}
