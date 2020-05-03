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

package net.fabricmc.fabric.api.networking.v1.client;

import java.util.concurrent.CompletableFuture;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ChannelHandler;
import net.fabricmc.fabric.api.networking.v1.ListenerContext;

/**
 * Represents the context for {@link ClientNetworking#getLoginReceiver()}, in which a
 * {@link net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket login
 * query request packest} is received.
 *
 * <p>Compared to other type of packet reception context, the client login query packet
 * reception is expected to respond after receiving a packet. Use {@code respond} methods
 * to send a response; if none of the {@code respond} methods are called immediately within
 * {@link ChannelHandler#receive(ListenerContext, PacketByteBuf)},
 * a "not understood" response will be sent to the server. If a response cannot be calculated
 * immediately, use {@link #respond(CompletableFuture)} or {@link #respond(CompletableFuture, GenericFutureListener)},
 * which can send the response when the {@link CompletableFuture} is completed.</p>
 *
 * @see ClientNetworking#getLoginReceiver()
 */
@Environment(EnvType.CLIENT)
public interface ClientLoginContext extends ClientContext {
	/**
	 * {@inheritDoc}
	 *
	 * <p>In a client login context, the network handler is always a {@link
	 * ClientLoginNetworkHandler}.</p>
	 */
	@Override
	ClientLoginNetworkHandler getListener();

	// packet qualities

	/* (Non-Javadoc)
	 * Returns the integer ID of the query request.
	 */
	//int getQueryId(); Expose again when needed

	// utilities
	// if none of these "respond" is called, an unknown packet will be sent

	/**
	 * Sends a response to the server.
	 *
	 * <p>The {@code response} may be {@code null} to indicate a "not understood"
	 * query response.</p>
	 *
	 * @param buf the content of the response, may be {@code null}
	 */
	void respond(PacketByteBuf buf);

	/**
	 * Sends a response to the server.
	 *
	 * <p>The {@code response} may be {@code null} to indicate a "not understood"
	 * query response.</p>
	 *
	 * @param buf      the content of the response, may be {@code null}
	 * @param callback a callback when the response is sent, may be {@code null}
	 */
	void respond(PacketByteBuf buf, GenericFutureListener<? extends Future<? super Void>> callback);

	/**
	 * Schedule to send a response to the server when the {@code future} is completed.
	 *
	 * <p>If the future {@linkplain CompletableFuture#complete(Object) completed} with {@code null}
	 * result, a "not understood" query response is sent to the server.</p>
	 *
	 * <p>If the future {@linkplain CompletableFuture#completeExceptionally(Throwable) completed
	 * exceptionally}, a "not understood" query response is sent to the server.</p>
	 *
	 * @param future the future that calculates the response
	 */
	void respond(CompletableFuture<? extends PacketByteBuf> future);

	/**
	 * Schedule to send a response to the server when the {@code future} is completed.
	 *
	 * <p>If the future {@linkplain CompletableFuture#complete(Object) completed} with {@code null}
	 * result, a "not understood" query response is sent to the server.</p>
	 *
	 * <p>If the future {@linkplain CompletableFuture#completeExceptionally(Throwable) completed
	 * exceptionally}, a "not understood" query response is sent to the server.</p>
	 *
	 * @param future   the future that calculates the response
	 * @param callback a callback when the response is sent, may be {@code null}
	 */
	void respond(CompletableFuture<? extends PacketByteBuf> future, GenericFutureListener<? extends Future<? super Void>> callback);
}
