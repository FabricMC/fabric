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

package net.fabricmc.fabric.mixin.networking;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.impl.networking.PacketRegistryImpl;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;

@Mixin(priority = 500, value = PlayerManager.class)
public abstract class MixinPlayerManager {
	@Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/packet/DifficultyS2CPacket;<init>(Lnet/minecraft/world/Difficulty;Z)V"))
	public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
		Optional<Packet<?>> optionalPacket = PacketRegistryImpl.createInitialRegisterPacket(ServerSidePacketRegistry.INSTANCE);

		//noinspection OptionalIsPresent
		if (optionalPacket.isPresent()) {
			player.networkHandler.sendPacket(optionalPacket.get());
			((ServerSidePacketRegistryImpl) ServerSidePacketRegistry.INSTANCE).addNetworkHandler(player.networkHandler);
		}
	}
}
