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
import net.minecraft.util.Identifier;
import net.minecraft.util.thread.ThreadExecutor;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.PacketRegistry;
import net.fabricmc.fabric.api.networking.v1.ClientNetworking;

public class ClientSidePacketRegistryImpl implements ClientSidePacketRegistry, PacketRegistry {
	@Override
	public boolean canServerReceive(Identifier id) {
		return ClientNetworking.getPlayReceiver().hasChannel(id);
	}

	@Override
	public void sendToServer(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> completionListener) {
		ClientNetworking.getPlaySender().sendPacket(packet, completionListener);
	}

	@Override
	public Packet<?> toPacket(Identifier id, PacketByteBuf buf) {
		return ClientNetworking.getPlaySender().makePacket(id, buf);
	}

	@Override
	public void register(Identifier id, PacketConsumer consumer) {
		// id is checked in client networking
		Objects.requireNonNull(consumer, "PacketConsumer cannot be null");

		ClientNetworking.getPlayReceiver().register(id, (handler, client, sender, buf) -> {
			consumer.accept(new PacketContext() {
				@Override
				public EnvType getPacketEnvironment() {
					return EnvType.CLIENT;
				}

				@Override
				public PlayerEntity getPlayer() {
					return client.player;
				}

				@Override
				public ThreadExecutor<?> getTaskQueue() {
					return client;
				}
			}, buf);
		});
	}

	@Override
	public void unregister(Identifier id) {
		ClientNetworking.getPlayReceiver().unregister(id);
	}
}
