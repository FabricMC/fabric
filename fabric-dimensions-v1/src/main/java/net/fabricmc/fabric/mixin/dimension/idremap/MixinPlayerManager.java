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

package net.fabricmc.fabric.mixin.dimension.idremap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.impl.dimension.DimensionIdsFixer;
import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
	/**
	 * Synchronizes raw dimension ids to connecting players.
	 */
	@Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/packet/DifficultyS2CPacket;<init>(Lnet/minecraft/world/Difficulty;Z)V"))
	private void onPlayerConnect(ClientConnection conn, ServerPlayerEntity player, CallbackInfo info) {
		// TODO: Refactor out into network + move dimension hook to event

		// No need to send the packet if the player is using the same game instance (dimension types are static)
		if (!player.server.isSinglePlayer() || !conn.isLocal() || FabricDimensionInternals.DEBUG) {
			player.networkHandler.sendPacket(DimensionIdsFixer.createPacket(player.world.getLevelProperties()));
		}
	}
}
