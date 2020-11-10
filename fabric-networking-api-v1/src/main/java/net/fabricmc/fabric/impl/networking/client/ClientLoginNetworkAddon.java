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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.FutureListeners;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.networking.AbstractNetworkAddon;
import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.mixin.networking.accessor.LoginQueryRequestS2CPacketAccessor;

@Environment(EnvType.CLIENT)
public final class ClientLoginNetworkAddon extends AbstractNetworkAddon<ClientLoginNetworking.LoginChannelHandler> {
	private final ClientLoginNetworkHandler handler;
	private final MinecraftClient client;
	private boolean firstResponse = true;

	public ClientLoginNetworkAddon(ClientLoginNetworkHandler handler, MinecraftClient client) {
		this.handler = handler;
		this.client = client;

		ClientLoginConnectionEvents.LOGIN_INIT.invoker().onLoginStart(this.handler, this.client);
	}

	public boolean handlePacket(LoginQueryRequestS2CPacket packet) {
		LoginQueryRequestS2CPacketAccessor access = (LoginQueryRequestS2CPacketAccessor) packet;
		return handlePacket(packet.getQueryId(), access.getChannel(), access.getPayload());
	}

	private boolean handlePacket(int queryId, Identifier channel, PacketByteBuf originalBuf) {
		if (this.firstResponse) {
			// Register global handlers
			for (Map.Entry<Identifier, ClientLoginNetworking.LoginChannelHandler> entry : ClientNetworkingImpl.LOGIN.getHandlers().entrySet()) {
				ClientLoginNetworking.register(this.handler, entry.getKey(), entry.getValue());
			}

			ClientLoginConnectionEvents.LOGIN_QUERY_START.invoker().onLoginQueryStart(this.handler, this.client);
			this.firstResponse = false;
		}

		@Nullable ClientLoginNetworking.LoginChannelHandler handler = this.getHandler(channel);

		if (handler == null) {
			return false;
		}

		PacketByteBuf buf = PacketByteBufs.slice(originalBuf);
		List<GenericFutureListener<? extends Future<? super Void>>> futureListeners = new ArrayList<>();

		try {
			CompletableFuture<@Nullable PacketByteBuf> future = handler.receive(this.handler, this.client, buf, futureListeners::add);
			future.thenAccept(result -> {
				LoginQueryResponseC2SPacket packet = new LoginQueryResponseC2SPacket(queryId, result);
				GenericFutureListener<? extends Future<? super Void>> listener = null;

				for (GenericFutureListener<? extends Future<? super Void>> each : futureListeners) {
					listener = FutureListeners.union(listener, each);
				}

				this.handler.getConnection().send(packet, listener);
			});
		} catch (Throwable ex) {
			NetworkingImpl.LOGGER.error("Encountered exception while handling in channel \"{}\"", channel, ex);
			throw ex;
		}

		return true;
	}

	@Override
	protected void handleRegistration(Identifier channel) {
		// TODO
	}

	@Override
	protected void handleUnregistration(Identifier channel) {
		// TODO
	}

	@Override
	protected boolean isReservedChannel(Identifier channel) {
		return false;
	}
}
