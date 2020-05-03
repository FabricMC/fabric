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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.networking.v1.server.ServerNetworking;
import net.fabricmc.fabric.impl.networking.DisconnectPacketSource;
import net.fabricmc.fabric.impl.networking.server.ServerPlayNetworkAddon;
import net.fabricmc.fabric.impl.networking.server.ServerPlayNetworkHandlerHook;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin implements ServerPlayNetworkHandlerHook, DisconnectPacketSource {
	private ServerPlayNetworkAddon addon;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void networking$ctor(CallbackInfo ci) {
		this.addon = new ServerPlayNetworkAddon((ServerPlayNetworkHandler) (Object) this);
	}

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void networking$customPayloadReceivedAsync(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		if (this.addon.handle(packet)) {
			ci.cancel();
		}
	}

	@Inject(method = "onDisconnected", at = @At("HEAD"))
	private void networking$onDisconnected(Text reason, CallbackInfo ci) {
		ServerNetworking.PLAY_DISCONNECTED.invoker().handle((ServerPlayNetworkHandler) (Object) this);
	}

	@Override
	public ServerPlayNetworkAddon getAddon() {
		return this.addon;
	}

	@Override
	public Packet<?> makeDisconnectPacket(Text message) {
		return new DisconnectS2CPacket(message);
	}
}
