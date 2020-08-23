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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;

import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;

/**
 * This mixin implements {@link Entity#getTeleportTarget(ServerWorld)} for modded dimensions, as Vanilla will
 * not return a teleport target for anything but Vanilla dimensions.
 */
@Mixin(Entity.class)
public class EntityMixin {
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true, allow = 1)
	public void getTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cri) {
		Entity self = (Entity) (Object) this;
		// Check if a destination has been set for the entity currently being teleported
		TeleportTarget customTarget = FabricDimensionInternals.getCustomTarget();

		if (customTarget != null) {
			cri.setReturnValue(customTarget);
		}
	}
}
