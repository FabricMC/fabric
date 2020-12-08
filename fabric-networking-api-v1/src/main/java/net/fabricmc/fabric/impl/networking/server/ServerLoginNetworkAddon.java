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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import io.netty.util.concurrent.GenericFutureListener;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.impl.networking.AbstractNetworkAddon;
import net.fabricmc.fabric.mixin.networking.accessor.LoginQueryRequestS2CPacketAccessor;
import net.fabricmc.fabric.mixin.networking.accessor.LoginQueryResponseC2SPacketAccessor;
import net.fabricmc.fabric.mixin.networking.accessor.ServerLoginNetworkHandlerAccessor;

public final class ServerLoginNetworkAddon extends AbstractNetworkAddon<ServerLoginNetworking.LoginQueryResponseHandler> implements PacketSender {
	private final ClientConnection connection;
	private final ServerLoginNetworkHandler handler;
	private final MinecraftServer server;
	private final QueryIdFactory queryIdFactory;
	private final Collection<Future<?>> waits = new ConcurrentLinkedQueue<>();
	private final Map<Integer, Identifier> channels = new ConcurrentHashMap<>();
	private boolean firstQueryTick = true;

	public ServerLoginNetworkAddon(ServerLoginNetworkHandler handler) {
		super(ServerNetworkingImpl.LOGIN, "ServerLoginNetworkAddon for " + handler.getConnectionInfo());
		this.connection = handler.connection;
		this.handler = handler;
		this.server = ((ServerLoginNetworkHandlerAccessor) handler).getServer();
		this.queryIdFactory = QueryIdFactory.create();

		ServerLoginConnectionEvents.INIT.invoker().onLoginInit(handler, this.server);
		this.receiver.startSession(this);
	}

	// return true if no longer ticks query
	public boolean queryTick() {
		if (this.firstQueryTick) {
			// Send the compression packet now so clients receive compressed login queries
			this.sendCompressionPacket();

			// Register global receivers.
			for (Map.Entry<Identifier, ServerLoginNetworking.LoginQueryResponseHandler> entry : ServerNetworkingImpl.LOGIN.getHandlers().entrySet()) {
				ServerLoginNetworking.registerReceiver(this.handler, entry.getKey(), entry.getValue());
			}

			ServerLoginConnectionEvents.QUERY_START.invoker().onLoginStart(this.handler, this.server, this, this.waits::add);
			this.firstQueryTick = false;
		}

		AtomicReference<Throwable> error = new AtomicReference<>();
		this.waits.removeIf(future -> {
			if (!future.isDone()) {
				return false;
			}

			try {
				future.get();
			} catch (ExecutionException ex) {
				Throwable caught = ex.getCause();
				error.getAndUpdate(oldEx -> {
					if (oldEx == null) {
						return caught;
					}

					oldEx.addSuppressed(caught);
					return oldEx;
				});
			} catch (InterruptedException | CancellationException ignored) {
				// ignore
			}

			return true;
		});

		return this.channels.isEmpty() && this.waits.isEmpty();
	}

	private void sendCompressionPacket() {
		// Compression is not needed for local transport
		if (this.server.getNetworkCompressionThreshold() >= 0 && !this.connection.isLocal()) {
			this.connection.send(new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()), (channelFuture) ->
					this.connection.setCompressionThreshold(this.server.getNetworkCompressionThreshold())
			);
		}
	}

	/**
	 * Handles an incoming query response during login.
	 *
	 * @param packet the packet to handle
	 * @return true if the packet was handled
	 */
	public boolean handle(LoginQueryResponseC2SPacket packet) {
		LoginQueryResponseC2SPacketAccessor access = (LoginQueryResponseC2SPacketAccessor) packet;
		return handle(access.getQueryId(), access.getResponse());
	}

	private boolean handle(int queryId, @Nullable PacketByteBuf originalBuf) {
		this.logger.debug("Handling inbound login query with id {}", queryId);
		Identifier channel = this.channels.remove(queryId);

		if (channel == null) {
			this.logger.warn("Query ID {} was received but no query has been associated in {}!", queryId, this.connection);
			return false;
		}

		boolean understood = originalBuf != null;
		@Nullable ServerLoginNetworking.LoginQueryResponseHandler handler = ServerNetworkingImpl.LOGIN.getHandler(channel);

		if (handler == null) {
			return false;
		}

		PacketByteBuf buf = understood ? PacketByteBufs.slice(originalBuf) : PacketByteBufs.empty();

		try {
			handler.receive(this.server, this.handler, understood, buf, this.waits::add, this);
		} catch (Throwable ex) {
			this.logger.error("Encountered exception while handling in channel \"{}\"", channel, ex);
			throw ex;
		}

		return true;
	}

	@Override
	public Packet<?> createPacket(Identifier channelName, PacketByteBuf buf) {
		int queryId = this.queryIdFactory.nextId();
		LoginQueryRequestS2CPacket ret = new LoginQueryRequestS2CPacket();
		// The constructor for creating a non-empty response was removed by proguard
		LoginQueryRequestS2CPacketAccessor access = (LoginQueryRequestS2CPacketAccessor) ret;
		access.setQueryId(queryId);
		access.setChannel(channelName);
		access.setPayload(buf);
		return ret;
	}

	@Override
	public void sendPacket(Packet<?> packet) {
		Objects.requireNonNull(packet, "Packet cannot be null");

		this.connection.send(packet);
	}

	@Override
	public void sendPacket(Packet<?> packet, GenericFutureListener<? extends io.netty.util.concurrent.Future<? super Void>> callback) {
		Objects.requireNonNull(packet, "Packet cannot be null");

		this.connection.send(packet, callback);
	}

	public void registerOutgoingPacket(LoginQueryRequestS2CPacket packet) {
		LoginQueryRequestS2CPacketAccessor access = (LoginQueryRequestS2CPacketAccessor) packet;
		this.channels.put(access.getServerQueryId(), access.getChannel());
	}

	@Override
	protected void handleRegistration(Identifier channelName) {
	}

	@Override
	protected void handleUnregistration(Identifier channelName) {
	}

	@Override
	public void invokeDisconnectEvent() {
		ServerLoginConnectionEvents.DISCONNECT.invoker().onLoginDisconnect(this.handler, this.server);
		this.receiver.endSession(this);
	}

	@Override
	protected boolean isReservedChannel(Identifier channelName) {
		return false;
	}
}
