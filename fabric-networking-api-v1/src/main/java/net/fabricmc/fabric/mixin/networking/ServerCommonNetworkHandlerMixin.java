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

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import net.fabricmc.fabric.api.networking.v1.ServerCookieStore;
import net.fabricmc.fabric.api.networking.v1.ServerTransferable;

import net.fabricmc.fabric.impl.networking.ServerTransferMeta;

import net.minecraft.network.ClientConnection;

import net.minecraft.network.packet.c2s.common.CookieResponseC2SPacket;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;

import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.server.ServerConfigurationNetworkAddon;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerCommonNetworkHandler.class)
public abstract class ServerCommonNetworkHandlerMixin implements NetworkHandlerExtensions, ServerTransferable, ServerCookieStore {

	@Shadow
	@Final
	protected ClientConnection connection;

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void handleCustomPayloadReceivedAsync(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		final CustomPayload payload = packet.payload();

		boolean handled;

		if (getAddon() instanceof ServerConfigurationNetworkAddon addon) {
			handled = addon.handle(payload);
		} else {
			// Play should be handled in ServerPlayNetworkHandlerMixin
			throw new IllegalStateException("Unknown addon");
		}

		if (handled) {
			ci.cancel();
		}
	}

	@Inject(method = "onPong", at = @At("HEAD"))
	private void onPlayPong(CommonPongC2SPacket packet, CallbackInfo ci) {
		if (getAddon() instanceof ServerConfigurationNetworkAddon addon) {
			addon.onPong(packet.getParameter());
		}
	}

	@Inject(method = "onCookieResponse", at = @At("HEAD"))
	private void storeCookie(CookieResponseC2SPacket packet, CallbackInfo ci) {
		((ServerTransferMeta) connection).fabric_invokeCookieCallback(packet.key(), packet.payload());
	}

	@WrapWithCondition(method = "onCookieResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerCommonNetworkHandler;disconnect(Lnet/minecraft/text/Text;)V"))
	private boolean cancelDisconnect(ServerCommonNetworkHandler instance, Text reason) {
		return false;
	}

	@Override
	public void transferToServer(String host, int port) {
		((ServerTransferable) connection).transferToServer(host, port);
	}

	@Override
	public boolean wasTransferred() {
		return ((ServerTransferable) connection).wasTransferred();
	}

	@Override
	public void setCookie(Identifier cookieId, byte[] cookie) {
		((ServerCookieStore) connection).setCookie(cookieId, cookie);
	}

	@Override
	public CompletableFuture<byte[]> getCookie(Identifier cookieId) {
		return ((ServerCookieStore) connection).getCookie(cookieId);
	}
}
