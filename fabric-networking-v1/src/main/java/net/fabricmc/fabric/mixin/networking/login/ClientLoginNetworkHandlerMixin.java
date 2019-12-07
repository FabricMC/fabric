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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;

import net.fabricmc.fabric.impl.networking.PacketHelper;
import net.fabricmc.fabric.impl.networking.receiver.ClientLoginQueryPacketContextImpl;
import net.fabricmc.fabric.impl.networking.receiver.ClientPacketReceivers;

@Mixin(ClientLoginNetworkHandler.class)
public abstract class ClientLoginNetworkHandlerMixin {
	@Inject(method = "onQueryRequest(Lnet/minecraft/client/network/packet/LoginQueryRequestS2CPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/Packet;)V"), cancellable = true)
	public void fabric_redirectQueryRequest(LoginQueryRequestS2CPacket packet, CallbackInfo ci) {
		LoginQueryPacketAccessor hook = (LoginQueryPacketAccessor) packet;

		if (ClientPacketReceivers.LOGIN_QUERY.receive(hook.getChannel(), new ClientLoginQueryPacketContextImpl((ClientLoginNetworkHandler) (Object) this, hook.getQueryId()), hook.getPayload())) {
			// payload has been released
			ci.cancel();
		}
	}

	@Inject(method = "onQueryRequest(Lnet/minecraft/client/network/packet/LoginQueryRequestS2CPacket;)V", at = @At("RETURN"))
	public void fabric_finishVanillaQueryRequest(LoginQueryRequestS2CPacket packet, CallbackInfo ci) {
		PacketHelper.releaseBuffer(((LoginQueryPacketAccessor) packet).getPayload());
	}
}
