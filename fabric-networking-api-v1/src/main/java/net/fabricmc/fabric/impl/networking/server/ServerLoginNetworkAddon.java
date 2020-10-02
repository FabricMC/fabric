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
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerNetworking;
import net.fabricmc.fabric.impl.networking.AbstractNetworkAddon;
import net.fabricmc.fabric.impl.networking.NetworkingDetails;
import net.fabricmc.fabric.mixin.networking.accessor.LoginQueryRequestS2CPacketAccessor;
import net.fabricmc.fabric.mixin.networking.accessor.LoginQueryResponseC2SPacketAccessor;
import net.fabricmc.fabric.mixin.networking.accessor.ServerLoginNetworkHandlerAccessor;

public final class ServerLoginNetworkAddon extends AbstractNetworkAddon {
	private final ServerLoginNetworkHandler handler;
	private final MinecraftServer server;
	private final QueryIdFactory queryIdFactory;
	private final Collection<Future<?>> waits = new ConcurrentLinkedQueue<>();
	private final Map<Integer, Identifier> channels = new ConcurrentHashMap<>();
	private boolean firstQueryTick = true;

	public ServerLoginNetworkAddon(ServerLoginNetworkHandler handler) {
		super(handler.connection);
		this.handler = handler;
		this.server = ((ServerLoginNetworkHandlerAccessor) handler).getServer();
		this.queryIdFactory = QueryIdFactory.create();
	}

	// return true if no longer ticks query
	public boolean queryTick() {
		if (this.firstQueryTick) {
			this.sendCompressionPacket();
			ServerConnectionEvents.LOGIN_QUERY_START.invoker().onLoginStart(this.handler, this.server, this, this.waits::add);
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
				// ignored
			}

			return true;
		});

		return this.channels.isEmpty() && this.waits.isEmpty();
	}

	private void sendCompressionPacket() {
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

	private boolean handle(int queryId, PacketByteBuf originalBuf) {
		Identifier channel = this.channels.remove(queryId);

		if (channel == null) {
			NetworkingDetails.LOGGER.warn("Query ID {} was received but no channel has been associated in {}!", queryId, this.connection);
			return false;
		}

		boolean understood = originalBuf != null;
		ServerNetworking.LoginChannelHandler handler = ServerNetworkingDetails.LOGIN.get(channel);

		if (handler == null) {
			return false;
		}

		PacketByteBuf buf = understood ? PacketByteBufs.slice(originalBuf) : PacketByteBufs.empty();

		try {
			handler.receive(this.handler, this.server, this, buf, understood, this.waits::add);
		} catch (Throwable ex) {
			NetworkingDetails.LOGGER.error("Encountered exception while handling in channel \"{}\"", channel, ex);
			throw ex;
		}

		return true;
	}

	@Override
	public Packet<?> makePacket(Identifier channel, PacketByteBuf buf) {
		int queryId = this.queryIdFactory.nextId();
		LoginQueryRequestS2CPacket ret = new LoginQueryRequestS2CPacket();
		LoginQueryRequestS2CPacketAccessor access = (LoginQueryRequestS2CPacketAccessor) ret;
		access.setQueryId(queryId);
		access.setChannel(channel);
		access.setPayload(buf);
		return ret;
	}

	public void registerOutgoingPacket(LoginQueryRequestS2CPacket packet) {
		LoginQueryRequestS2CPacketAccessor access = (LoginQueryRequestS2CPacketAccessor) packet;
		this.channels.put(access.getServerQueryId(), access.getChannel());
	}
}
