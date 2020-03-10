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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.impl.networking.CustomPayloadC2SPacketAccessor;
import net.fabricmc.fabric.impl.networking.ServerSidePacketRegistryImpl;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler implements PacketContext {
	@Shadow
	private MinecraftServer server;
	@Shadow
	private ServerPlayerEntity player;

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	public void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo info) {
		Identifier channel = ((CustomPayloadC2SPacketAccessor) packet).getChannel();

		if (((ServerSidePacketRegistryImpl) ServerSidePacketRegistry.INSTANCE).accept(channel, this, ((CustomPayloadC2SPacketAccessor) packet)::getData)) {
			info.cancel();
		}
	}

	@Override
	public EnvType getPacketEnvironment() {
		return EnvType.SERVER;
	}

	@Override
	public PlayerEntity getPlayer() {
		return player;
	}

	@Override
	public ThreadExecutor getTaskQueue() {
		return server;
	}
}
