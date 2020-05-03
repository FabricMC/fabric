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

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ListenerContext;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

// server login
public abstract class AbstractNetworkAddon<C extends ListenerContext> extends ReceivingNetworkAddon<C> implements PacketSender {
	protected final ClientConnection connection;

	protected AbstractNetworkAddon(BasicPacketReceiver<C> receiver, ClientConnection connection) {
		super(receiver);
		this.connection = connection;
	}

	protected abstract Packet<?> makePacket(Identifier channel, PacketByteBuf buf);

	@Override
	public void sendPacket(Identifier channel, PacketByteBuf buf) {
		this.connection.send(makePacket(channel, buf));
	}

	@Override
	public void sendPacket(Identifier channel, PacketByteBuf buf, GenericFutureListener<? extends Future<? super Void>> callback) {
		this.connection.send(makePacket(channel, buf), callback);
	}
}
