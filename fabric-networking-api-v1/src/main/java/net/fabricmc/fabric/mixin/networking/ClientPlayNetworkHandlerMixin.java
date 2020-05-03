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

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.networking.v1.client.ClientNetworking;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkHandlerHook;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayNetworkHandlerHook {
	private ClientPlayNetworkAddon addon;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void networking$ctor(CallbackInfo ci) {
		this.addon = new ClientPlayNetworkAddon((ClientPlayNetworkHandler) (Object) this);
	}

	@Inject(method = "onGameJoin", at = @At("RETURN"))
	private void networking$onServerPlayReady(GameJoinS2CPacket packet, CallbackInfo ci) {
		this.addon.onServerReady();
	}

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void networking$customPayloadReceivedAsync(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (this.addon.handle(packet)) {
			ci.cancel();
		}
	}

	@Inject(method = "onDisconnected", at = @At("HEAD"))
	private void networking$onDisconnected(Text reason, CallbackInfo ci) {
		ClientNetworking.PLAY_DISCONNECTED.invoker().handle((ClientPlayNetworkHandler) (Object) this);
	}

	@Override
	public ClientPlayNetworkAddon getAddon() {
		return this.addon;
	}
}
