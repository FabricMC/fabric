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

package net.fabricmc.fabric.mixin.network;

import io.netty.channel.ChannelHandlerContext;
import net.fabricmc.fabric.api.event.network.client.ServerLeaveCallback;
import net.fabricmc.fabric.api.event.network.client.ServerLoginCallback;
import net.fabricmc.fabric.api.event.network.server.ClientLeaveCallback;
import net.fabricmc.fabric.api.event.network.server.ClientLoginCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection {

	@Inject(method = "setPacketListener", at = @At("RETURN"))
	private void onSetListener(PacketListener listener, CallbackInfo ci) {
		ClientConnection self = (ClientConnection) (Object) this;
		if (listener instanceof ClientLoginPacketListener) {
			ServerLoginCallback.EVENT.invoker().onLogin(self);
		} else if (listener instanceof ServerLoginPacketListener) {
			ClientLoginCallback.EVENT.invoker().onLogin(self);
		}
	}

	@Inject(method = "channelInactive", remap = false, at = @At("RETURN"))
	private void onChannelInactive(ChannelHandlerContext context, CallbackInfo ci) {
		ClientConnection self = (ClientConnection) (Object) this;
		if (self.getPacketListener() instanceof ServerPlayPacketListener) {
			ClientLeaveCallback.EVENT.invoker().onLeave(self);
		} else if (self.getPacketListener() instanceof ClientPlayPacketListener) {
			ServerLeaveCallback.EVENT.invoker().onLeave(self);
		}
	}
}
