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

package net.fabricmc.fabric.impl.networking;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.receiver.PlayPacketContext;
import net.fabricmc.fabric.api.networking.v1.receiver.PacketReceiverRegistry;
import net.fabricmc.fabric.api.networking.v1.receiver.ServerPacketReceiverRegistries;
import net.fabricmc.fabric.impl.networking.hook.PlayNetworkHandlerHook;

public class ServerSidePacketRegistryImpl extends PacketRegistryImpl implements ServerSidePacketRegistry {
	@Override
	PacketReceiverRegistry<? extends PlayPacketContext> getReceiverRegistry() {
		return ServerPacketReceiverRegistries.PLAY;
	}

	@Override
	public boolean canPlayerReceive(PlayerEntity player, Identifier id) {
		return ((PlayNetworkHandlerHook) ((ServerPlayerEntity) player).networkHandler).getPacketSender().accepts(id);
	}

	@Override
	public void sendToPlayer(PlayerEntity player, Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		if (!(player instanceof ServerPlayerEntity)) {
			throw new RuntimeException("Can only send to ServerPlayerEntities!");
		} else {
			((ServerPlayerEntity) player).networkHandler.sendPacket(packet, completionListener);
		}
	}

	@Override
	public Packet<?> toPacket(Identifier id, PacketByteBuf buf) {
		return new CustomPayloadS2CPacket(id, buf);
	}
}
