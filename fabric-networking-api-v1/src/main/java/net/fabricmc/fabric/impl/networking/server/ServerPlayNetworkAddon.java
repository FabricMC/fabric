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

package net.fabricmc.fabric.impl.networking.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.ServerPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.mixin.networking.accessor.CustomPayloadC2SPacketAccessor;

public final class ServerPlayNetworkAddon extends AbstractChanneledNetworkAddon<ServerPlayNetworking.PlayChannelHandler> {
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<Identifier, ServerPlayNetworking.PlayChannelHandler> handlers = new HashMap<>(); // sync map should be fine as there is little read write competitions
	private final ServerPlayNetworkHandler handler;
	private final MinecraftServer server;

	public ServerPlayNetworkAddon(ServerPlayNetworkHandler handler, MinecraftServer server) {
		super(ServerNetworkingImpl.PLAY, handler.getConnection());
		this.handler = handler;
		this.server = server;

		// Must register pending channels via lateinit
		this.registerPendingChannels((ChannelInfoHolder) this.connection);
	}

	public void onClientReady() {
		for (Map.Entry<Identifier, ServerPlayNetworking.PlayChannelHandler> entry : ServerNetworkingImpl.PLAY.getHandlers().entrySet()) {
			this.registerChannel(entry.getKey(), entry.getValue());
		}

		ServerPlayConnectionEvents.PLAY_INIT.invoker().onPlayInit(this.handler, this, this.server);
		this.sendChannelRegistrationPacket();
	}

	/**
	 * Handles an incoming packet.
	 *
	 * @param packet the packet to handle
	 * @return true if the packet has been handled
	 */
	public boolean handle(CustomPayloadC2SPacket packet) {
		CustomPayloadC2SPacketAccessor access = (CustomPayloadC2SPacketAccessor) packet;
		return handle(access.getChannel(), access.getData());
	}

	@Override
	protected void receive(ServerPlayNetworking.PlayChannelHandler handler, PacketByteBuf buf) {
		handler.receive(this.handler, this, this.server, buf);
	}

	// impl details

	@Override
	protected void schedule(Runnable task) {
		this.handler.player.server.execute(task);
	}

	@Override
	public Packet<?> createPacket(Identifier channel, PacketByteBuf buf) {
		return ServerPlayNetworking.createS2CPacket(channel, buf);
	}

	@Override
	protected void postRegisterEvent(List<Identifier> ids) {
		ServerPlayChannelEvents.REGISTER.invoker().onChannelRegister(this.handler, this, this.server, ids);
	}

	@Override
	protected void postUnregisterEvent(List<Identifier> ids) {
		ServerPlayChannelEvents.UNREGISTER.invoker().onChannelUnregister(this.handler, this, this.server, ids);
	}

	@Nullable
	@Override
	public ServerPlayNetworking.PlayChannelHandler getHandler(Identifier channel) {
		Lock lock = this.lock.readLock();
		lock.lock();

		try {
			return this.handlers.get(channel);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean registerChannel(Identifier channel, ServerPlayNetworking.PlayChannelHandler channelHandler) {
		Objects.requireNonNull(channel, "Channel cannot be null");
		Objects.requireNonNull(channelHandler, "Packet handler cannot be null");

		if (NetworkingImpl.isReservedChannel(channel)) {
			throw new IllegalArgumentException(String.format("Cannot register handler for reserved channel \"%s\"", channel));
		}

		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			final boolean hasEntry = this.handlers.putIfAbsent(channel, channelHandler) == null;

			if (!hasEntry) {
				this.register(Collections.singletonList(channel));
				this.sendRegisterPacket(Collections.singleton(channel));
			}

			return hasEntry;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public ServerPlayNetworking.PlayChannelHandler unregisterChannel(Identifier channel) {
		Objects.requireNonNull(channel, "Channel cannot be null");

		if (NetworkingImpl.isReservedChannel(channel)) {
			throw new IllegalArgumentException(String.format("Cannot unregister packet handler for reserved channel \"%s\"", channel));
		}

		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			return this.handlers.remove(channel);
		} finally {
			lock.unlock();
		}
	}
}
