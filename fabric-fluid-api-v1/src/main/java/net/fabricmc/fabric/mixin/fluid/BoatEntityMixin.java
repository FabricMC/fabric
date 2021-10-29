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
	@Redirect(method = "method_7544()F",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInHandler1(FluidState state, Tag<Fluid> tag) {
		return FluidUtils.isNavigable(state);
	}

	@Redirect(method = "checkBoatInWater()Z",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInHandler2(FluidState state, Tag<Fluid> tag) {
		return FluidUtils.isNavigable(state);
	}

	@Redirect(method = "getUnderWaterLocation()Lnet/minecraft/entity/vehicle/BoatEntity$Location;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInHandler3(FluidState state, Tag<Fluid> tag) {
		return FluidUtils.isNavigable(state);
	}

	@Redirect(method = "fall(DZLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/fluid/FluidState;isIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isInHandler4(FluidState state, Tag<Fluid> tag) {
		return FluidUtils.isNavigable(state);
	}

	@Redirect(method = "updatePassengerForDismount(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/util/math/Vec3d;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isWater(Lnet/minecraft/util/math/BlockPos;)Z"))
	private boolean isWaterHandler(World world, BlockPos pos) {
		return FluidUtils.isNavigable(world.getFluidState(pos));
	}

	@Redirect(method = "canAddPassenger(Lnet/minecraft/entity/Entity;)Z",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;isSubmergedIn(Lnet/minecraft/tag/Tag;)Z"))
	private boolean isSubmergedInHandler(BoatEntity boat, Tag<Fluid> tag) {
		return this.isSubmerged();
	}

	@Unique
	private boolean isSubmerged() {
		return this.submergedFluidTag != null;
	}
}
