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

package net.fabricmc.fabric.mixin.entity.event;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.ClientConnection;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

@Mixin(PlayerManager.class)
abstract class PlayerManagerMixin {
	@Inject(method = "respawnPlayer", at = @At("TAIL"))
	private void afterRespawn(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
		ServerPlayerEvents.AFTER_RESPAWN.invoker().afterRespawn(oldPlayer, cir.getReturnValue(), alive);
	}

	@Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "net/minecraft/server/network/ServerPlayerEntity.setWorld(Lnet/minecraft/server/world/ServerWorld;)V", shift = At.Shift.AFTER))
	private void afterWorldSet(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
		ServerPlayerEvents.BEFORE_SPAWN.invoker().beforeSpawn(player);
	}

	@ModifyVariable(method = "onPlayerConnect", ordinal = 1, at = @At(value = "INVOKE", target = "net/minecraft/server/network/ServerPlayerEntity.setWorld(Lnet/minecraft/server/world/ServerWorld;)V", shift = At.Shift.AFTER))
	private ServerWorld fixServerWorld(ServerWorld world, ClientConnection connection, ServerPlayerEntity player) {
		return player.getWorld();
	}

	@ModifyVariable(method = "onPlayerConnect", ordinal = 0, at = @At(value = "INVOKE", target = "net/minecraft/server/network/ServerPlayerEntity.setWorld(Lnet/minecraft/server/world/ServerWorld;)V", shift = At.Shift.AFTER))
	private RegistryKey<World> fixRegistryKey(RegistryKey<World> world, ClientConnection connection, ServerPlayerEntity player) {
		return player.getWorld().getRegistryKey();
	}
}
