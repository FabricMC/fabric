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

import net.fabricmc.fabric.api.event.network.client.ServerLoginCallback;
import net.fabricmc.fabric.api.event.network.server.ClientLeaveCallback;
import net.fabricmc.fabric.api.event.network.server.ClientLoginCallback;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.listener.ServerPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection {

	@Shadow public abstract PacketListener getPacketListener();

	@Inject(method = "handleDisconnection", expect = 2, at = @At(value = "INVOKE", target = "Lnet/minecraft/network/listener/PacketListener;onDisconnected(Lnet/minecraft/network/chat/Component;)V"))
	private void onDisconnect(CallbackInfo ci) {
		ClientConnection self = (ClientConnection) (Object) this;
		if (this.getPacketListener() instanceof ServerPlayPacketListener){
			ClientLeaveCallback.EVENT.invoker().onLeave(self);
		}
	}

	@Inject(method = "setPacketListener", at = @At("RETURN"))
	private void onSetListener(PacketListener listener, CallbackInfo ci) {
		ClientConnection self = (ClientConnection) (Object) this;
		if (listener instanceof ClientLoginPacketListener) {
			ServerLoginCallback.EVENT.invoker().onLogin(self);
		} else if (listener instanceof ServerLoginPacketListener) {
			ClientLoginCallback.EVENT.invoker().onLogin(self);
		}
	}
}
