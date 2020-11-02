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

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;
import net.fabricmc.fabric.impl.networking.client.ClientPlayNetworkHandlerExtensions;

/**
 * Offers access to play stage client-side networking functionalities.
 *
 * <p>Client-side networking functionalities include receiving clientbound packets,
 * sending serverbound packets, and events related to client-side network handlers.
 *
 * <p>This class should be only used on the physical client and for the logical client.
 *
 * @see ClientLoginNetworking
 * @see ServerPlayNetworking
 */
@Environment(EnvType.CLIENT)
public final class ClientPlayNetworking {
	public static boolean registerGlobalReceiver(Identifier channel, PlayChannelHandler handler) {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	@Nullable
	public static PlayChannelHandler unregisterGlobalReceiver(Identifier channel) {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	/**
	 * Registers a handler to a channel.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregister(ClientPlayNetworkHandler, Identifier)} to unregister the existing handler.</p>
	 *
	 * @param channel the id of the channel
	 * @param networkHandler the handler
	 * @return false if a handler is already registered to the channel
	 */
	public static boolean register(ClientPlayNetworkHandler networkHandler, Identifier channel, PlayChannelHandler channelHandler) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ((ClientPlayNetworkHandlerExtensions) networkHandler).getAddon().registerChannel(channel, channelHandler);
	}

	/**
	 * Removes the handler of a channel.
	 *
	 * <p>The {@code channel} is guaranteed not to have a handler after this call.</p>
	 *
	 * @param channel the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 */
	public static PlayChannelHandler unregister(ClientPlayNetworkHandler networkHandler, Identifier channel) {
		Objects.requireNonNull(networkHandler, "Network handler cannot be null");

		return ((ClientPlayNetworkHandlerExtensions) networkHandler).getAddon().unregisterChannel(channel);
	}

	public static Collection<Identifier> getGlobalReceivers() {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	public static boolean hasGlobalReceiver(Identifier channel) {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	public static Collection<Identifier> getReceivers() throws IllegalStateException {
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			throw new IllegalStateException(); // TODO: Error message
		}

		return getReceivers(MinecraftClient.getInstance().getNetworkHandler());
	}

	public static Collection<Identifier> getReceivers(ClientPlayerEntity player) {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	public static Collection<Identifier> getReceivers(ClientPlayNetworkHandler handler) {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	public static boolean canReceive(Identifier channel) {
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			throw new IllegalStateException(); // TODO: Error message
		}

		return canReceive(MinecraftClient.getInstance().getNetworkHandler(), channel);
	}

	public static boolean canReceive(ClientPlayerEntity player, Identifier channel) {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	public static boolean canReceive(ClientPlayNetworkHandler handler, Identifier channel) {
		throw new UnsupportedOperationException("Reimplement me!");
	}

	public static Packet<?> createC2SPacket(Identifier channel, PacketByteBuf buf) {
		Objects.requireNonNull(channel, "Channel cannot be null");
		Objects.requireNonNull(buf, "Buf cannot be null");

		return ClientNetworkingImpl.createPlayC2SPacket(channel, buf);
	}

	/**
	 * Sends a packet to the connected server.
	 *
	 * @param channel the channel of the packet
	 * @param buf the payload of the packet
	 * @throws IllegalStateException if the client's player is {@code null}
	 */
	public static void send(Identifier channel, PacketByteBuf buf) throws IllegalStateException {
		if (MinecraftClient.getInstance().getNetworkHandler() != null) {
			throw new IllegalStateException(); // TODO: Error message
		}

		send(MinecraftClient.getInstance().getNetworkHandler(), channel, buf);
	}

	/**
	 * Sends a packet to a server.
	 *
	 * @param handler a client play network handler
	 * @param channel the channel of the packet
	 * @param buf the payload of the packet
	 * @throws IllegalStateException if the client's player is {@code null}
	 */
	public static void send(ClientPlayNetworkHandler handler, Identifier channel, PacketByteBuf buf) {
		Objects.requireNonNull(handler, "Client play network handler cannot be null");

		handler.sendPacket(createC2SPacket(channel, buf));
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
		 * <p>An example usage of this is to display an overlay message:
		 * <pre>{@code
		 * ClientPlayNetworking.getPlayReceivers().register(new Identifier("mymod", "overlay"), (handler, client, sender, buf) -&rt; {
		 * 	String message = buf.readString(32767);
		 *
		 * 	// All operations on the server or world must be executed on the server thread
		 * 	client.execute(() -&rt; {
		 * 		client.inGameHud.setOverlayMessage(message, true);
		 * 	});
		 * });
		 * }</pre>
		 *
		 * @param handler the network handler that received this packet
		 * @param sender the packet sender
		 * @param client the client
		 * @param buf the payload of the packet
		 */
		void receive(ClientPlayNetworkHandler handler, PacketSender sender, MinecraftClient client, PacketByteBuf buf);
	}
}
