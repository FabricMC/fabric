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

package net.fabricmc.fabric.mixin.dimension;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PortalForcer;

import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;

@Mixin(PortalForcer.class)
public abstract class MixinPortalForcer {
	@Shadow
	@Final
	private ServerWorld world;

	@Inject(method = "usePortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getLastNetherPortalDirectionVector()Lnet/minecraft/util/math/Vec3d;"))
	private void onUsePortal(Entity teleported, float yaw, CallbackInfoReturnable<Boolean> cir) {
		FabricDimensionInternals.prepareDimensionalTeleportation(teleported);
	}

	@Inject(method = "getPortal", at = @At("HEAD"), cancellable = true)
	private void findEntityPlacement(BlockPos pos, Vec3d velocity, Direction portalDir, double portalX, double portalY, boolean player, CallbackInfoReturnable<BlockPattern.TeleportTarget> cir) {
		BlockPattern.TeleportTarget ret = FabricDimensionInternals.tryFindPlacement(this.world, portalDir, portalX, portalY);

		if (ret != null) {
			cir.setReturnValue(ret);
		}
	}
}
