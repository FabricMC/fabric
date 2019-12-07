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

package net.fabricmc.fabric.impl.networking.handler;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.receiver.PacketContext;
import net.fabricmc.fabric.api.networking.v1.sender.PacketSender;
import net.fabricmc.fabric.impl.networking.receiver.SimplePacketReceiverRegistry;

abstract class AbstractPacketHandler<T extends PacketContext> implements PacketSender {
	final ClientConnection connection;

	AbstractPacketHandler(ClientConnection connection) {
		this.connection = connection;
	}

	// call this after the packet handler is set.
	// on main thread for play handlers; off thread otherwise
	public abstract void init();

	// returns if other logic should be allowed to execute
	// buf is original buf, consider have it kept etc.
	public boolean accept(Identifier channel, T context, PacketByteBuf buf) {
		return getPacketReceiverRegistry().receive(channel, context, buf);
	}

	@Override
	public void send(Identifier channel, PacketByteBuf buffer) {
		connection.send(createPacket(channel, buffer));
	}

	@Override
	public void send(Identifier channel, PacketByteBuf buffer, GenericFutureListener<? extends Future<? super Void>> listener) {
		connection.send(createPacket(channel, buffer), listener);
	}

	abstract SimplePacketReceiverRegistry<T> getPacketReceiverRegistry();

	abstract Packet<?> createPacket(Identifier channel, PacketByteBuf buffer);
}
