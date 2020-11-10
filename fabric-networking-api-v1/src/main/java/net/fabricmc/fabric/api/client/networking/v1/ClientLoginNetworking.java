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

package net.fabricmc.fabric.api.client.networking.v1;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;

/**
 * Offers access to login stage client-side networking functionalities.
 *
 * <p>Client-side networking functionalities include receiving clientbound packets, sending serverbound packets, and events related to client-side network handlers.
 *
 * @see ClientPlayNetworking
 * @see ServerLoginNetworking
 */
@Environment(EnvType.CLIENT)
public final class ClientLoginNetworking {
	/**
	 * Registers a handler to a channel.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregisterGlobalReceiver(Identifier)} to unregister the existing handler.</p>
	 *
	 * @param channel the id of the channel
	 * @param channelHandler the handler
	 * @return false if a handler is already registered to the channel
	 */
	public static boolean registerGlobalReceiver(Identifier channel, LoginChannelHandler channelHandler) {
		return ClientNetworkingImpl.LOGIN.registerGlobalReceiver(channel, channelHandler);
	}

	/**
	 * Removes the handler of a channel.
	 *
	 * <p>The {@code channel} is guaranteed not to have a handler after this call.</p>
	 *
	 * @param channel the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 */
	@Nullable
	public static LoginChannelHandler unregisterGlobalReceiver(Identifier channel) {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	public static boolean register(ClientLoginNetworkHandler networkHandler, Identifier channel, LoginChannelHandler channelHandler) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ClientNetworkingImpl.getAddon(networkHandler).registerChannel(channel, channelHandler);
	}

	public static LoginChannelHandler unregister(ClientLoginNetworkHandler networkHandler, Identifier channel) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ClientNetworkingImpl.getAddon(networkHandler).unregisterChannel(channel);
	}

	public static Collection<Identifier> getGlobalReceivers() {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	public static boolean hasGlobalReceiver(Identifier channel) {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	private ClientLoginNetworking() {
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface LoginChannelHandler {
		/**
		 * Handles an incoming query request from a server.
		 *
		 * <p>This method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
		 * Modification to the game should be {@linkplain net.minecraft.util.thread.ThreadExecutor#submit(Runnable) scheduled} using the provided Minecraft client instance.
		 *
		 * <p>The return value of this method is a completable future that may be used to delay the login process to the server until a task {@link CompletableFuture#isDone() is done}.
		 *
		 * @param handler the network handler that received this packet
		 * @param client the client
		 * @param buf the payload of the packet
		 * @param listenerAdder listeners to be called when the response packet is sent to the server
		 * @return a completable future which contains the payload to respond to the server with.
		 * If the future contains {@code null}, then the server will be notified that the client did not understand the query.
		 */
		CompletableFuture<@Nullable PacketByteBuf> receive(ClientLoginNetworkHandler handler, MinecraftClient client, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder);
	}
}
