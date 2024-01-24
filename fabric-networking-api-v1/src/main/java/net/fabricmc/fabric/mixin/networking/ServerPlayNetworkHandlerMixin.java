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

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.common.ServerTransferS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ServerCookieStore;
import net.fabricmc.fabric.api.networking.v1.ServerTransferable;
import net.fabricmc.fabric.impl.networking.DisconnectPacketSource;
import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.server.ServerPlayNetworkAddon;
import net.fabricmc.fabric.mixin.networking.accessor.ServerCommonNetworkHandlerAccessor;

// We want to apply a bit earlier than other mods which may not use us in order to prevent refCount issues
@Mixin(value = ServerPlayNetworkHandler.class, priority = 999)
abstract class ServerPlayNetworkHandlerMixin extends ServerCommonNetworkHandler implements NetworkHandlerExtensions, DisconnectPacketSource, ServerTransferable, ServerCookieStore {
	@Unique
	private ServerPlayNetworkAddon addon;

	ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData arg) {
		super(server, connection, arg);
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initAddon(CallbackInfo ci) {
		this.addon = new ServerPlayNetworkAddon((ServerPlayNetworkHandler) (Object) this, connection, server);
		// A bit of a hack but it allows the field above to be set in case someone registers handlers during INIT event which refers to said field
		this.addon.lateInit();
	}

	@Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
	private void handleCustomPayloadReceivedAsync(CustomPayloadC2SPacket packet, CallbackInfo ci) {
		if (getAddon().handle(packet.payload())) {
			ci.cancel();
		}
	}

	@Override
	public ServerPlayNetworkAddon getAddon() {
		return this.addon;
	}

	@Override
	public Packet<?> createDisconnectPacket(Text message) {
		return new DisconnectS2CPacket(message);
	}

	@Override
	public void transferToServer(String host, int port) {
		connection.send(new ServerTransferS2CPacket(host, port));
	}

	@Override
	public boolean wasTransferred() {
		return ((ServerCommonNetworkHandlerAccessor) this).isTransferred();
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
