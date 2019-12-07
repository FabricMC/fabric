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

package net.fabricmc.fabric.mixin.networking.login;

import io.netty.util.concurrent.GenericFutureListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.packet.LoginCompressionS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.packet.LoginQueryResponseC2SPacket;

import net.fabricmc.fabric.api.networking.v1.event.LoginQueryStartCallback;
import net.fabricmc.fabric.api.networking.v1.sender.PacketSender;
import net.fabricmc.fabric.impl.networking.PacketHelper;
import net.fabricmc.fabric.impl.networking.handler.ServerLoginPacketHandler;
import net.fabricmc.fabric.impl.networking.hook.ServerLoginNetworkHandlerHook;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin implements ServerLoginNetworkHandlerHook {
	@Shadow
	@Final
	public ClientConnection client;
	@Shadow
	@Final
	private MinecraftServer server;
	// impl fields
	private ServerLoginPacketHandler handler;
	private boolean neverReady;

	@Shadow
	public abstract void acceptPlayer();

	@Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/ClientConnection;)V", at = @At("TAIL"))
	public void fabric_onConstructor(CallbackInfo ci) {
		neverReady = true;
		handler = new ServerLoginPacketHandler(client);
		handler.init();
	}

	@Override
	public PacketSender getPacketSender() {
		return handler;
	}

	@Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;acceptPlayer()V"))
	public void fabric_onReadyToAcceptPlayer(ServerLoginNetworkHandler self) {
		if (neverReady) {
			neverReady = false;
			fabric_sendCompressionPacket();
			LoginQueryStartCallback.EVENT.invoker().onLoginQuery(server, (ServerLoginNetworkHandler) (Object) this, handler);
		}

		// if there is nothing on hold (followup q&a), accept player
		// TODO need a way to prevent timeouts
		if (handler.canAcceptPlayer()) {
			acceptPlayer();
		}
	}

	@Unique
	private void fabric_sendCompressionPacket() {
		if (this.server.getNetworkCompressionThreshold() >= 0 && !this.client.isLocal()) {
			this.client.send(new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()), channelFuture -> this.client.setMinCompressedSize(this.server.getNetworkCompressionThreshold()));
		}
	}

	@Inject(method = "onQueryResponse(Lnet/minecraft/server/network/packet/LoginQueryResponseC2SPacket;)V", at = @At("HEAD"), cancellable = true)
	public void fabric_injectQueryResponse(LoginQueryResponseC2SPacket packet, CallbackInfo ci) {
		LoginQueryResponsePacketAccessor hook = (LoginQueryResponsePacketAccessor) packet;

		if (handler.accept(server, (ServerLoginNetworkHandler) (Object) this, handler, hook)) {
			// buf already released
			ci.cancel();
		}
	}

	@Inject(method = "onQueryResponse(Lnet/minecraft/server/network/packet/LoginQueryResponseC2SPacket;)V", at = @At("TAIL"))
	public void fabric_finishVanillaQueryResponse(LoginQueryResponseC2SPacket packet, CallbackInfo ci) {
		PacketHelper.releaseBuffer(((LoginQueryResponsePacketAccessor) packet).getResponse());
		// clean up after vanilla disconnect unknown response
	}

	/**
	 * @reason Packet sent before login query.
	 */
	@Redirect(method = "acceptPlayer()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V"))
	public void fabric_passCompressionPacket(ClientConnection connection, Packet<?> packet, GenericFutureListener<?> listener) {
		// So the compression packet doesn't get sent here any more
	}
}
