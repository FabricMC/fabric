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

package net.fabricmc.fabric.mixin.networking.client;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;

import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.client.ClientConfigurationNetworkAddon;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkAddon;
import net.fabricmc.fabric.impl.networking.payload.ResolvablePayload;
import net.fabricmc.fabric.impl.networking.payload.RetainedPayload;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class ClientCommonNetworkHandlerMixin implements NetworkHandlerExtensions {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Inject(method = "onCustomPayload(Lnet/minecraft/network/packet/s2c/common/CustomPayloadS2CPacket;)V", at = @At("HEAD"), cancellable = true)
	public void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
		if (packet.payload() instanceof ResolvablePayload payload) {
			boolean handled;

			if (this.getAddon() instanceof ClientPlayNetworkAddon addon) {
				handled = addon.handle(payload);
			} else if (this.getAddon() instanceof ClientConfigurationNetworkAddon addon) {
				handled = addon.handle(payload);
			} else {
				throw new IllegalStateException("Unknown network addon");
			}

			if (!handled && payload instanceof RetainedPayload retained && retained.buf().refCnt() > 0) {
				// Duplicate the vanilla log message, as we cancel further processing.
				LOGGER.warn("Unknown custom packet payload: {}", payload.id());

				retained.buf().skipBytes(retained.buf().readableBytes());
				retained.buf().release();
			}

			ci.cancel();
		}
	}
}
