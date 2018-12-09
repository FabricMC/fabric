/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.fabricmc.fabric.networking.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.CustomPayloadClientPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ThreadTaskQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler implements PacketContext {
	@Shadow
	private MinecraftClient client;

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	public void onCustomPayload(CustomPayloadClientPacket packet, CallbackInfo info) {
		if (CustomPayloadPacketRegistry.CLIENT.accept(packet.getChannel(), this, packet.getData())) {
			info.cancel();
		}
	}

	@Override
	public EnvType getPacketEnvironment() {
		return EnvType.CLIENT;
	}

	@Override
	public PlayerEntity getPlayer() {
		return client.player;
	}

	@Override
	public ThreadTaskQueue getTaskQueue() {
		return client;
	}
}
