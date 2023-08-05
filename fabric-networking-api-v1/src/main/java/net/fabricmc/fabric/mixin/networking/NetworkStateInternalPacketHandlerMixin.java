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

import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;

import net.fabricmc.fabric.impl.networking.LoginQueryRequestS2CPacketFactory;

@Mixin(NetworkState.InternalPacketHandler.class)
public abstract class NetworkStateInternalPacketHandlerMixin<T extends ClientLoginPacketListener> {
	@Unique
	private static final Function<PacketByteBuf, LoginQueryRequestS2CPacket> LOGIN_QUERY_REQUEST_FACTORY = LoginQueryRequestS2CPacketFactory::create;

	@Shadow public abstract <P extends Packet<? super T>> NetworkState.InternalPacketHandler<T> register(Class<P> type, Function<PacketByteBuf, P> packetFactory);

	@Inject(method = "register", at = @At("HEAD"), cancellable = true)
	private <P extends Packet<? super T>> void register(Class<P> type, Function<PacketByteBuf, P> packetFactory, CallbackInfoReturnable<NetworkState.InternalPacketHandler<T>> cir) {
		if (type == LoginQueryRequestS2CPacket.class && packetFactory != LOGIN_QUERY_REQUEST_FACTORY) {
			cir.setReturnValue(register(LoginQueryRequestS2CPacket.class, LOGIN_QUERY_REQUEST_FACTORY));
		}
	}
}
