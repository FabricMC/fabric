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

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;

/**
 * This mixin prevents END dimension specific events when using api. Specifically:
 * <ol>
 *   <li>{@link ServerPlayerEntity#createEndSpawnPlatform(ServerWorld, BlockPos)} execution when teleporting to END</li>
 *   <li>"Game won" screen and {@link ServerPlayerEntity#seenCredits} flag setting by
 *   {@link ServerPlayerEntity#moveToWorld(ServerWorld)} when teleporting from END</li>
 * </ol>
 */
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
	@Inject(method = "createEndSpawnPlatform", at = @At("HEAD"), cancellable = true, allow = 1)
	public void getTeleportTarget(ServerWorld world, BlockPos centerPos, CallbackInfo ci) {
		// Check if a destination has been set for the entity currently being teleported
		if (FabricDimensionInternals.getCustomTarget() != null) {
			ci.cancel();
		}
	}

	@Redirect(
			method = "moveToWorld",
			slice = @Slice(
					from = @At(value = "FIELD", target = "net/minecraft/world/World.END:Lnet/minecraft/util/registry/RegistryKey;", opcode = Opcodes.GETSTATIC, ordinal = 0),
					to = @At(value = "FIELD", target = "net/minecraft/server/network/ServerPlayerEntity.notInAnyWorld:Z", opcode = Opcodes.GETFIELD, ordinal = 0)
			),
			at = @At(value = "FIELD", target = "net/minecraft/world/World.OVERWORLD:Lnet/minecraft/util/registry/RegistryKey;", opcode = Opcodes.GETSTATIC),
			allow = 1
	)
	public RegistryKey<World> moveToWorld() {
		// Check if a destination has been set for the entity currently being teleported
		if (FabricDimensionInternals.getCustomTarget() != null) {
			return null;
		}

		return World.OVERWORLD;
	}
}
