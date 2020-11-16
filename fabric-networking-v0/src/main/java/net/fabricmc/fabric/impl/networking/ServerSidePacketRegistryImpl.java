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

import java.util.Objects;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.PacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ServerSidePacketRegistryImpl implements ServerSidePacketRegistry, PacketRegistry {
	@Override
	public boolean canPlayerReceive(PlayerEntity player, Identifier id) {
		if (player instanceof ServerPlayerEntity) {
			return ServerPlayNetworking.canSend((ServerPlayerEntity) player, id);
		}

		return false;
	}

	@Override
	public void sendToPlayer(PlayerEntity player, Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		if (player instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) player).networkHandler.sendPacket(packet, completionListener);
			return;
		}

		throw new RuntimeException("Can only send to ServerPlayerEntities!");
	}

	@Override
	public Packet<?> toPacket(Identifier id, PacketByteBuf buf) {
		return new CustomPayloadS2CPacket(id, buf);
	}

	@Override
	public void register(Identifier id, PacketConsumer consumer) {
		Objects.requireNonNull(consumer, "PacketConsumer cannot be null");

		ServerPlayNetworking.registerGlobalReceiver(id, (server, player, handler, buf, sender) -> {
			consumer.accept(new PacketContext() {
				@Override
				public EnvType getPacketEnvironment() {
					return EnvType.SERVER;
				}

				@Override
				public PlayerEntity getPlayer() {
					return player;
				}

				@Override
				public ThreadExecutor<?> getTaskQueue() {
					return server;
				}
			}, buf);
		});
	}

	@Override
	public void unregister(Identifier id) {
		ServerPlayNetworking.unregisterGlobalReceiver(id);
	}
}
