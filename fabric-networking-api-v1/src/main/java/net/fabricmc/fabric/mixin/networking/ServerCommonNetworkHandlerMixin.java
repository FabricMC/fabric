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

import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;

import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.payload.ResolvablePayload;
import net.fabricmc.fabric.impl.networking.payload.RetainedPayload;
import net.fabricmc.fabric.impl.networking.server.ServerConfigurationNetworkAddon;
import net.fabricmc.fabric.impl.networking.server.ServerPlayNetworkAddon;

@Mixin(ServerCommonNetworkHandler.class)
public abstract class ServerCommonNetworkHandlerMixin implements NetworkHandlerExtensions {
	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void handleCustomPayloadReceivedAsync(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		if (packet.payload() instanceof ResolvablePayload payload) {
			boolean handled;

			if (getAddon() instanceof ServerPlayNetworkAddon addon) {
				handled = addon.handle(payload);
			} else if (getAddon() instanceof ServerConfigurationNetworkAddon addon) {
				handled = addon.handle(payload);
			} else {
				throw new IllegalStateException("Unknown addon");
			}

			if (handled) {
				ci.cancel();
			} else if (payload instanceof RetainedPayload retained) {
				retained.buf().skipBytes(retained.buf().readableBytes());
				retained.buf().release();
			}
		}
	}

	@Inject(method = "onPong", at = @At("HEAD"))
	private void onPlayPong(CommonPongC2SPacket packet, CallbackInfo ci) {
		if (getAddon() instanceof ServerConfigurationNetworkAddon addon) {
			addon.onPong(packet.getParameter());
		}
	}
}
