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

package net.fabricmc.fabric.impl.networking.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.fabricmc.fabric.impl.networking.ChannelInfoHolder;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;

@Environment(EnvType.CLIENT)
public final class ClientPlayNetworkAddon extends AbstractChanneledNetworkAddon<ClientPlayNetworking.PlayChannelHandler> {
	private final ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Map<Identifier, ClientPlayNetworking.PlayChannelHandler> handlers = new HashMap<>(); // sync map should be fine as there is little read write competitions
	private final ClientPlayNetworkHandler handler;
	private final MinecraftClient client;

	public ClientPlayNetworkAddon(ClientPlayNetworkHandler handler, MinecraftClient client) {
		super(ClientNetworkingImpl.PLAY, handler.getConnection());
		this.handler = handler;
		this.client = client;

		// Must register pending channels via lateinit
		this.registerPendingChannels((ChannelInfoHolder) this.connection);
	}

	// also expose sendRegistration

	public void onServerReady() {
		// Register global channels
		for (Map.Entry<Identifier, ClientPlayNetworking.PlayChannelHandler> entry : ClientNetworkingImpl.PLAY.getHandlers().entrySet()) {
			this.registerChannel(entry.getKey(), entry.getValue());
		}

		this.sendChannelRegistrationPacket();
		ClientPlayConnectionEvents.PLAY_INIT.invoker().onPlayInit(this.handler, this, this.client);
	}

	/**
	 * Handles an incoming packet.
	 *
	 * @param packet the packet to handle
	 * @return true if the packet has been handled
	 */
	public boolean handle(CustomPayloadS2CPacket packet) {
		PacketByteBuf buf = packet.getData();

		try {
			return handle(packet.getChannel(), buf);
		} finally {
			buf.release();
		}
	}

	@Override
	protected void receive(ClientPlayNetworking.PlayChannelHandler handler, PacketByteBuf buf) {
		handler.receive(this.handler, this, this.client, buf);
	}

	// impl details

	@Override
	protected void schedule(Runnable task) {
		MinecraftClient.getInstance().execute(task);
	}

	@Override
	public Packet<?> createPacket(Identifier channel, PacketByteBuf buf) {
		return ClientPlayNetworking.createC2SPacket(channel, buf);
	}

	@Override
	protected void postRegisterEvent(List<Identifier> ids) {
		ClientPlayChannelEvents.REGISTER.invoker().onChannelRegister(this.handler, this, this.client, ids);
	}

	@Override
	protected void postUnregisterEvent(List<Identifier> ids) {
		ClientPlayChannelEvents.UNREGISTER.invoker().onChannelUnregister(this.handler, this, this.client, ids);
	}

	@Nullable
	@Override
	public ClientPlayNetworking.PlayChannelHandler getHandler(Identifier channel) {
		Lock lock = this.lock.readLock();
		lock.lock();

		try {
			return this.handlers.get(channel);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean registerChannel(Identifier channel, ClientPlayNetworking.PlayChannelHandler channelHandler) {
		Objects.requireNonNull(channel, "Channel cannot be null");
		Objects.requireNonNull(channelHandler, "Packet handler cannot be null");

		if (NetworkingImpl.isReservedChannel(channel)) {
			throw new IllegalArgumentException(String.format("Cannot register handler for reserved channel \"%s\"", channel));
		}

		Lock lock = this.lock.writeLock();
		lock.lock();

		try {
			final boolean noReplacement = this.handlers.putIfAbsent(channel, channelHandler) == null;

			if (noReplacement) {
				this.register(Collections.singletonList(channel));
				this.sendRegisterPacket(Collections.singleton(channel));
			}

			return noReplacement;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public ClientPlayNetworking.PlayChannelHandler unregisterChannel(Identifier channel) {
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
