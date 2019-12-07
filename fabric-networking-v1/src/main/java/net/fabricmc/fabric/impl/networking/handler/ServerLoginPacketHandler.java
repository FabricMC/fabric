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

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.client.network.packet.LoginQueryRequestS2CPacket;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.api.networking.v1.receiver.ServerLoginQueryResponsePacketContext;
import net.fabricmc.fabric.api.networking.v1.sender.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.receiver.ServerLoginQueryResponsePacketContextImpl;
import net.fabricmc.fabric.impl.networking.receiver.ServerPacketReceivers;
import net.fabricmc.fabric.impl.networking.receiver.SimplePacketReceiverRegistry;
import net.fabricmc.fabric.mixin.networking.login.LoginQueryPacketAccessor;
import net.fabricmc.fabric.mixin.networking.login.LoginQueryResponsePacketAccessor;

public final class ServerLoginPacketHandler extends AbstractPacketHandler<ServerLoginQueryResponsePacketContext> {
	private final AtomicInteger queryId = new AtomicInteger(0);
	private final ConcurrentMap<Integer, Identifier> queryIdToChannel = new ConcurrentHashMap<>();
	private final Collection<Future<?>> holds = new ConcurrentLinkedQueue<>();

	public ServerLoginPacketHandler(ClientConnection connection) {
		super(connection);
	}

	public void addHold(Future<?> future) {
		holds.add(future);
	}

	public boolean canAcceptPlayer() {
		holds.removeIf(Future::isDone);
		return holds.isEmpty() && queryIdToChannel.isEmpty();
	}

	public boolean accept(MinecraftServer server, ServerLoginNetworkHandler loginNetworkHandler, ServerLoginPacketHandler packetHandler, LoginQueryResponsePacketAccessor accessor) {
		Identifier channel = queryIdToChannel.remove(accessor.getQueryId());
		if (channel == null) return false;

		PacketByteBuf buf = accessor.getResponse();
		ServerLoginQueryResponsePacketContext context = new ServerLoginQueryResponsePacketContextImpl(server, loginNetworkHandler, packetHandler, buf != null);
		PacketByteBuf passed = buf == null ? PacketByteBufs.empty() : buf;
		accept(channel, context, passed);
		return true;
	}

	@Override
	SimplePacketReceiverRegistry<ServerLoginQueryResponsePacketContext> getPacketReceiverRegistry() {
		return ServerPacketReceivers.LOGIN_QUERY_RESPONSE;
	}

	@Override
	Packet<?> createPacket(Identifier channel, PacketByteBuf buffer) {
		int id = queryId.incrementAndGet();
		Packet<?> ret = new LoginQueryRequestS2CPacket();
		LoginQueryPacketAccessor packet = (LoginQueryPacketAccessor) ret;
		packet.setChannel(channel);
		packet.setPayload(buffer);
		packet.setQueryId(id);
		queryIdToChannel.put(id, channel);
		return ret;
	}

	@Override
	public void init() {
	}
}
