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

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;

import net.fabricmc.fabric.impl.networking.DisconnectPacketSource;
import net.fabricmc.fabric.impl.networking.PacketCallbackListener;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkAddon;
import net.fabricmc.fabric.impl.networking.server.ServerLoginNetworkHandlerExtensions;

@Mixin(ServerLoginNetworkHandler.class)
abstract class ServerLoginNetworkHandlerMixin implements ServerLoginNetworkHandlerExtensions, DisconnectPacketSource, PacketCallbackListener {
	@Shadow
	@Final
	private MinecraftServer server;

	@Shadow
	public abstract void acceptPlayer();

	@Unique
	private ServerLoginNetworkAddon addon;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initAddon(CallbackInfo ci) {
		this.addon = new ServerLoginNetworkAddon((ServerLoginNetworkHandler) (Object) this);
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;acceptPlayer()V"))
	private void handlePlayerJoin(ServerLoginNetworkHandler handler) {
		// Do not accept the player, thereby moving into play stage until all login futures being waited on are completed
		if (this.addon.queryTick()) {
			this.acceptPlayer();
		}
	}

	@Inject(method = "onQueryResponse", at = @At("HEAD"), cancellable = true)
	private void handleCustomPayloadReceivedAsync(LoginQueryResponseC2SPacket packet, CallbackInfo ci) {
		// Handle queries
		if (this.addon.handle(packet)) {
			ci.cancel();
		}
	}

	@Redirect(method = "acceptPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getNetworkCompressionThreshold()I", ordinal = 0))
	private int removeLateCompressionPacketSending(MinecraftServer server) {
		return -1;
	}

	@Inject(method = "onDisconnected", at = @At("HEAD"))
	private void handleDisconnection(Text reason, CallbackInfo ci) {
		this.addon.invokeDisconnectEvent();
	}

	@Override
	public void sent(Packet<?> packet) {
		if (packet instanceof LoginQueryRequestS2CPacket) {
			this.addon.registerOutgoingPacket((LoginQueryRequestS2CPacket) packet);
		}
	}

	@Override
	public ServerLoginNetworkAddon getAddon() {
		return this.addon;
	}

	@Override
	public Packet<?> createDisconnectPacket(Text message) {
		return new LoginDisconnectS2CPacket(message);
	}
}
