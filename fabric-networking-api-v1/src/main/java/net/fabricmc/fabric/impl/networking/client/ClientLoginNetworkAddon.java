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

import java.util.concurrent.CompletableFuture;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.client.ClientLoginContext;
import net.fabricmc.fabric.impl.networking.ReceivingNetworkAddon;
import net.fabricmc.fabric.mixin.networking.access.LoginQueryRequestS2CPacketAccess;

public final class ClientLoginNetworkAddon extends ReceivingNetworkAddon<ClientLoginContext> {
	private final ClientLoginNetworkHandler handler;

	public ClientLoginNetworkAddon(ClientLoginNetworkHandler handler) {
		super(ClientNetworkingDetails.LOGIN);
		this.handler = handler;
	}

	public boolean handlePacket(LoginQueryRequestS2CPacket packet) {
		LoginQueryRequestS2CPacketAccess access = (LoginQueryRequestS2CPacketAccess) packet;
		return handlePacket(packet.getQueryId(), access.getChannel(), access.getPayload());
	}

	private boolean handlePacket(int queryId, Identifier channel, PacketByteBuf originalBuf) {
		try (Context context = new Context(queryId)) {
			return handle(channel, originalBuf, context);
		}
	}

	final class Context implements ClientLoginContext, AutoCloseable {
		private final int queryId;
		private boolean responded;

		Context(int queryId) {
			this.queryId = queryId;
			this.responded = false;
		}

		@Override
		public void close() {
			if (!responded && handler.getConnection().isOpen()) {
				respond((PacketByteBuf) null);
			}
		}

		@Override
		public ClientLoginNetworkHandler getListener() {
			return ClientLoginNetworkAddon.this.handler;
		}

		//@Override Not exposed for now
		public int getQueryId() {
			return this.queryId;
		}

		@Override
		public void respond(PacketByteBuf buf) {
			respond(buf, null);
		}

		@Override
		public void respond(CompletableFuture<? extends PacketByteBuf> future) {
			respond(future, null);
		}

		@Override
		public void respond(PacketByteBuf buf, GenericFutureListener<? extends Future<? super Void>> callback) {
			handler.getConnection().send(buildPacket(buf), callback);
			this.responded = true;
		}

		@Override
		public void respond(CompletableFuture<? extends PacketByteBuf> future, GenericFutureListener<? extends Future<? super Void>> callback) {
			ClientConnection connection = handler.getConnection();
			future.whenCompleteAsync((buf, ex) -> {
				if (!connection.isOpen()) {
					return;
				}

				// if an exception occurs, just... respond a "not understood" packet
				connection.send(buildPacket(buf), callback);
			});
			this.responded = true;
		}

		@Override
		public MinecraftClient getEngine() {
			return MinecraftClient.getInstance(); // may need update in the future?
		}

		private LoginQueryResponseC2SPacket buildPacket(/* Nullable */ PacketByteBuf buf) {
			return new LoginQueryResponseC2SPacket(this.queryId, buf);
		}
	}
}
