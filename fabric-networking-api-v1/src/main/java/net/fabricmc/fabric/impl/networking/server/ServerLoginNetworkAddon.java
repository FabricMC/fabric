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

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.server.ServerLoginContext;
import net.fabricmc.fabric.api.networking.v1.server.ServerNetworking;
import net.fabricmc.fabric.api.networking.v1.util.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.AbstractNetworkAddon;
import net.fabricmc.fabric.impl.networking.NetworkingDetails;
import net.fabricmc.fabric.mixin.networking.access.LoginQueryRequestS2CPacketAccess;
import net.fabricmc.fabric.mixin.networking.access.LoginQueryResponseC2SPacketAccess;
import net.fabricmc.fabric.mixin.networking.access.ServerLoginNetworkHandlerAccess;

public final class ServerLoginNetworkAddon extends AbstractNetworkAddon<ServerLoginContext> {
	private final ServerLoginNetworkHandler handler;
	private final MinecraftServer server;
	private final QueryIdFactory queryIdFactory;
	private final Collection<Future<?>> waits = new ConcurrentLinkedQueue<>();
	private final Map<Integer, Identifier> channels = new ConcurrentHashMap<>();
	private boolean firstQueryTick = true;

	public ServerLoginNetworkAddon(ServerLoginNetworkHandler handler) {
		super(ServerNetworkingDetails.LOGIN, handler.connection);
		this.handler = handler;
		this.server = ((ServerLoginNetworkHandlerAccess) handler).getServer();
		this.queryIdFactory = NetworkingDetails.createQueryIdManager();
	}

	// return true if no longer ticks query
	public boolean queryTick() {
		if (this.firstQueryTick) {
			this.sendCompressionPacket();
			ServerNetworking.LOGIN_QUERY_START.invoker().handle(this.handler);
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
			this.connection.send(new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()), (channelFuture) -> {
				this.connection.setCompressionThreshold(this.server.getNetworkCompressionThreshold());
			});
		}
	}

	public boolean handle(LoginQueryResponseC2SPacket packet) {
		LoginQueryResponseC2SPacketAccess access = (LoginQueryResponseC2SPacketAccess) packet;
		return handle(access.getQueryId(), access.getResponse());
	}

	private boolean handle(int queryId, PacketByteBuf originalBuf) {
		Identifier channel = channels.remove(queryId);

		if (channel == null) {
			NetworkingDetails.LOGGER.warn("Query ID {} was received but no channel has been associated in {}!", queryId, this.connection);
			return false;
		}

		boolean understood = originalBuf != null;
		return this.handle(channel, understood ? originalBuf : PacketByteBufs.empty(), new Context(queryId, understood));
	}

	@Override
	public Packet<?> makePacket(Identifier channel, PacketByteBuf buf) {
		int queryId = queryIdFactory.nextId();
		channels.put(queryId, channel);
		LoginQueryRequestS2CPacket ret = new LoginQueryRequestS2CPacket();
		LoginQueryRequestS2CPacketAccess access = (LoginQueryRequestS2CPacketAccess) ret;
		access.setQueryId(queryId);
		access.setChannel(channel);
		access.setPayload(buf);
		return ret;
	}

	final class Context implements ServerLoginContext {
		private final int queryId;
		private final boolean understood;

		Context(int queryId, boolean understood) {
			this.queryId = queryId;
			this.understood = understood;
		}

		@Override
		public ServerLoginNetworkHandler getListener() {
			return ServerLoginNetworkAddon.this.handler;
		}

		@Override
		public PacketSender getPacketSender() {
			return ServerLoginNetworkAddon.this;
		}

		//@Override Not exposed for now
		public int getQueryId() {
			return this.queryId;
		}

		@Override
		public boolean isUnderstood() {
			return this.understood;
		}

		@Override
		public void waitFor(Future<?> future) {
			ServerLoginNetworkAddon.this.waits.add(future);
		}

		@Override
		public MinecraftServer getEngine() {
			return ServerLoginNetworkAddon.this.server;
		}
	}
}
