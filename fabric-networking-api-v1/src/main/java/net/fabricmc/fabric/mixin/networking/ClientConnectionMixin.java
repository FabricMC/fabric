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
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.fabricmc.fabric.impl.networking.DisconnectPacketSource;
import net.fabricmc.fabric.impl.networking.GenericFutureListenerHolder;
import net.fabricmc.fabric.impl.networking.NetworkHandlerExtensions;
import net.fabricmc.fabric.impl.networking.PacketCallbackListener;

@Mixin(ClientConnection.class)
abstract class ClientConnectionMixin implements ChannelInfoHolder {
	@Shadow
	private PacketListener packetListener;

	@Shadow
	public abstract void disconnect(Text disconnectReason);

	@Shadow
	public abstract void send(Packet<?> packet, @Nullable PacketCallbacks arg);

	@Unique
	private Collection<Identifier> playChannels;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void initAddedFields(NetworkSide side, CallbackInfo ci) {
		this.playChannels = Collections.newSetFromMap(new ConcurrentHashMap<>());
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
	private void checkPacket(Packet<?> packet, PacketCallbacks callback, CallbackInfo ci) {
		if (this.packetListener instanceof PacketCallbackListener) {
			((PacketCallbackListener) this.packetListener).sent(packet);
		}
	}

	@Inject(method = "channelInactive", at = @At("HEAD"))
	private void handleDisconnect(ChannelHandlerContext channelHandlerContext, CallbackInfo ci) {
		if (packetListener instanceof NetworkHandlerExtensions) { // not the case for client/server query
			((NetworkHandlerExtensions) packetListener).getAddon().handleDisconnect();
		}
	}

	@Inject(method = "sendInternal", at = @At(value = "INVOKE_ASSIGN", target = "Lio/netty/channel/Channel;writeAndFlush(Ljava/lang/Object;)Lio/netty/channel/ChannelFuture;", remap = false), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void sendInternal(Packet<?> packet, @Nullable PacketCallbacks listener, NetworkState packetState, NetworkState currentState, CallbackInfo ci, ChannelFuture channelFuture) {
		if (listener instanceof GenericFutureListenerHolder holder) {
			channelFuture.addListener(holder.getDelegate());
			channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
			ci.cancel();
		}
	}

	@Override
	public Collection<Identifier> getPendingChannelsNames() {
		return this.playChannels;
	}
}
