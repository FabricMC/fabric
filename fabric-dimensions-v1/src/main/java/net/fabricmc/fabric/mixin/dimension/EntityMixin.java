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

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import net.fabricmc.fabric.impl.dimension.Teleportable;

/**
 * This mixin implements {@link Entity#getTeleportTarget(ServerWorld)} for modded dimensions, as Vanilla will
 * not return a teleport target for anything but Vanilla dimensions and prevents changing teleport target in
 * {@link ServerPlayerEntity#getTeleportTarget(ServerWorld)} when teleporting to END using api.
 * This also prevents several End dimension-specific code when teleporting using api.
 */
@Mixin(value = {ServerPlayerEntity.class, Entity.class})
public class EntityMixin implements Teleportable {
	@Unique
	@Nullable
	protected TeleportTarget customTeleportTarget;

	@Override
	public void fabric_setCustomTeleportTarget(TeleportTarget teleportTarget) {
		this.customTeleportTarget = teleportTarget;
	}

	@Inject(method = "getTeleportTarget", at = @At("HEAD"), cancellable = true, allow = 1)
	public void getTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
		// Check if a destination has been set for the entity currently being teleported
		TeleportTarget customTarget = this.customTeleportTarget;

		if (customTarget != null) {
			cir.setReturnValue(customTarget);
		}
	}

	/**
	 * This stops the following behaviors, in 1 mixin.
	 * - ServerWorld#createEndSpawnPlatform in Entity
	 * - End-to-overworld spawning behavior in ServerPlayerEntity
	 * - ServerPlayerEntity#createEndSpawnPlatform in ServerPlayerEntity
	 */
	@Redirect(method = "moveToWorld", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;END:Lnet/minecraft/registry/RegistryKey;"))
	private RegistryKey<World> stopEndSpecificBehavior() {
		if (this.customTeleportTarget != null) return null;
		return World.END;
	}
}
