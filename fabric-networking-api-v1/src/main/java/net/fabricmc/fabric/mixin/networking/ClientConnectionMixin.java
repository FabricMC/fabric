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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CookieRequestS2CPacket;
import net.minecraft.network.packet.s2c.common.StoreCookieS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ServerCookieStore;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.fabricmc.fabric.impl.networking.DisconnectPacketSource;
import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.PacketCallbackListener;
import net.fabricmc.fabric.impl.networking.ServerCookieCallback;

@Mixin(ClientConnection.class)
abstract class ClientConnectionMixin implements ChannelInfoHolder, ServerCookieCallback, ServerCookieStore {
	@Shadow
	private PacketListener packetListener;

	@Shadow
	public abstract void disconnect(Text disconnectReason);

	@Shadow
	public abstract void send(Packet<?> packet, @Nullable PacketCallbacks arg);

	@Shadow
	public abstract void send(Packet<?> packet);

	@Unique
	private Map<NetworkPhase, Collection<Identifier>> playChannels;
	@Unique
	private final Map<Identifier, CompletableFuture<byte[]>> pendingCookieRequests = new ConcurrentHashMap<>();

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initAddedFields(NetworkSide side, CallbackInfo ci) {
		this.playChannels = new ConcurrentHashMap<>();
	}

	// Must be fully qualified due to mixin not working in production without it
	@Redirect(method = "exceptionCaught", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V"))
	private void resendOnExceptionCaught(ClientConnection self, Packet<?> packet, PacketCallbacks listener, ChannelHandlerContext context, Throwable ex) {
		PacketListener handler = this.packetListener;
		Text disconnectMessage = Text.translatable("disconnect.genericReason", "Internal Exception: " + ex);

		if (handler instanceof DisconnectPacketSource) {
			this.send(((DisconnectPacketSource) handler).createDisconnectPacket(disconnectMessage), listener);
		} else {
			this.disconnect(disconnectMessage); // Don't send packet if we cannot send proper packets
		}
	}

	@Inject(method = "sendImmediately", at = @At(value = "FIELD", target = "Lnet/minecraft/network/ClientConnection;packetsSentCounter:I"))
	private void checkPacket(Packet<?> packet, PacketCallbacks callback, boolean flush, CallbackInfo ci) {
		if (this.packetListener instanceof PacketCallbackListener) {
			((PacketCallbackListener) this.packetListener).sent(packet);
		}
	}

	@Inject(method = "setPacketListener", at = @At("HEAD"))
	private void unwatchAddon(NetworkState<?> state, PacketListener listener, CallbackInfo ci) {
		if (this.packetListener instanceof NetworkHandlerExtensions oldListener) {
			oldListener.getAddon().endSession();
		}
	}

	@Inject(method = "channelInactive", at = @At("HEAD"))
	private void disconnectAddon(ChannelHandlerContext channelHandlerContext, CallbackInfo ci) {
		if (packetListener instanceof NetworkHandlerExtensions extension) {
			extension.getAddon().handleDisconnect();
		}
	}

	@Inject(method = "handleDisconnection", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/listener/PacketListener;onDisconnected(Lnet/minecraft/text/Text;)V"))
	private void disconnectAddon(CallbackInfo ci) {
		if (packetListener instanceof NetworkHandlerExtensions extension) {
			extension.getAddon().handleDisconnect();
		}
	}

	@Override
	public Collection<Identifier> fabric_getPendingChannelsNames(NetworkPhase state) {
		return this.playChannels.computeIfAbsent(state, (key) -> Collections.newSetFromMap(new ConcurrentHashMap<>()));
	}

	@Override
	public void fabric_invokeCookieCallback(Identifier cookieId, byte[] cookie) {
		CompletableFuture<byte[]> future = pendingCookieRequests.remove(cookieId);
		if (future == null) return;
		future.complete(cookie);
	}

	@Override
	public void setCookie(Identifier cookieId, byte[] cookie) {
		send(new StoreCookieS2CPacket(cookieId, cookie));
	}

	@Override
	public CompletableFuture<byte[]> getCookie(Identifier cookieId) {
		CompletableFuture<byte[]> future = pendingCookieRequests.get(cookieId);
		if (future != null) return future;

		future = new CompletableFuture<>();
		pendingCookieRequests.put(cookieId, future);
		send(new CookieRequestS2CPacket(cookieId));
		return future;
	}
}
