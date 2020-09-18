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

package net.fabricmc.fabric.api.networking.v1;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingDetails;

/**
 * Offers access to client-side networking functionalities.
 *
 * <p>Client-side networking functionalities include receiving clientbound packets,
 * sending serverbound packets, and events related to client-side network handlers.</p>
 *
 * <p>This class should be only used on the physical client and for the logical client.</p>
 *
 * @see ServerNetworking
 */
@Environment(EnvType.CLIENT)
public final class ClientNetworking {
	/**
	 * Returns the packet sender for the current client player.
	 *
	 * <p>This is a shortcut method for getting a sender.
	 * When a client play network handler is available, {@link #getPlaySender(ClientPlayNetworkHandler)} is preferred.
	 *
	 * @return the packet sender for the current client player
	 * @throws IllegalStateException if the client's player is {@code null}
	 */
	public static PlayPacketSender getPlaySender() throws IllegalStateException {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;

		if (player == null) {
			throw new IllegalStateException("Cannot get packet sender when not in game!");
		}

		return getPlaySender(player.networkHandler);
	}

	/**
	 * Returns the packet sender for a client play network handler.
	 *
	 * @param handler a client play network handler
	 * @return the associated packet sender
	 */
	public static PlayPacketSender getPlaySender(ClientPlayNetworkHandler handler) {
		return ClientNetworkingDetails.getAddon(handler);
	}

	/**
	 * Returns the packet receiver for channel handler registration on client play network handlers, receiving {@link net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket server to client custom payload packets}.
	 */
	public static ChannelHandlerRegistry<PlayChannelHandler> getPlayReceiver() {
		return ClientNetworkingDetails.PLAY;
	}

	/**
	 * Returns the packet receiver for channel handler registration on client login network handlers, receiving {@link net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket login query request packets}.
	 */
	public static ChannelHandlerRegistry<LoginChannelHandler> getLoginReceiver() {
		return ClientNetworkingDetails.LOGIN;
	}

	/**
	 * Sends a packet to the connected server.
	 *
	 * @param channel the channel of the packet
	 * @param buf the payload of the packet
	 */
	public static void send(Identifier channel, PacketByteBuf buf) {
		getPlaySender().sendPacket(channel, buf);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface PlayChannelHandler {
		/**
		 * Handles an incoming packet.
		 *
		 * <p>This method is executed on {@linkplain io.netty.channel.EventLoop netty's event loops}.
		 * Modification to the game should be {@linkplain net.minecraft.util.thread.ThreadExecutor#submit(Runnable) scheduled} using the provided Minecraft client instance.
		 *
		 * <p>An example usage of this is to display a
		 * <blockquote><pre>
		 * ClientNetworking.getPlayReceiver().register(new Identifier("mymod", "overlay"), (handler, client, sender, buf) -&rt; {
		 * 	String message = buf.readString(32767);
		 *
		 * 	// All operations on the server or world must be executed on the server thread
		 * 	client.execute(() -&rt; {
		 * 		client.inGameHud.setOverlayMessage(message, true);
		 * 	});
		 * });
		 * </pre></blockquote>
		 *
		 * @param handler the network handler that received this packet
		 * @param client the client
		 * @param sender the packet sender
		 * @param buf the payload of the packet
		 */
		void receive(ClientPlayNetworkHandler handler, MinecraftClient client, PlayPacketSender sender, PacketByteBuf buf);
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
		 * If {@code null}, then the server will be notified that the client did not understand the query.
		 */
		CompletableFuture<@Nullable PacketByteBuf> receive(ClientLoginNetworkHandler handler, MinecraftClient client, PacketByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder);
	}
}
