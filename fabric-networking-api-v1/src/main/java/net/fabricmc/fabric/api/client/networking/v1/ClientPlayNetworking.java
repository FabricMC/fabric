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
import net.fabricmc.fabric.api.networking.v1.PlayPacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.networking.client.ClientNetworkingImpl;

/**
 * Offers access to client-side networking functionalities.
 *
 * <p>Client-side networking functionalities include receiving clientbound packets,
 * sending serverbound packets, and events related to client-side network handlers.</p>
 *
 * <p>This class should be only used on the physical client and for the logical client.</p>
 *
 * @see ServerPlayNetworking
 */
@Environment(EnvType.CLIENT)
public final class ClientPlayNetworking {
	/**
	 * Registers a handler to a channel.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method will return {@code false}, and no change will be made.
	 * Use {@link #unregister(Identifier)} to unregister the existing handler.</p>
	 *
	 * @param channel the id of the channel
	 * @param handler the handler
	 * @return false if a handler is already registered to the channel
	 */
	public static boolean register(Identifier channel, PlayChannelHandler handler) {
		Objects.requireNonNull(channel, "Channel cannot be null");
		Objects.requireNonNull(handler, "Play channel handler cannot be null");

		return ClientNetworkingImpl.PLAY.register(channel, handler);
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
	public static PlayChannelHandler unregister(Identifier channel) {
		Objects.requireNonNull(channel, "Channel cannot be null");

		return ClientNetworkingImpl.PLAY.unregister(channel);
	}

	public static Collection<Identifier> getGlobalChannels() {
		return ClientNetworkingImpl.PLAY.getChannels();
	}

	public static boolean hasGlobalReceiver(Identifier channel) {
		return ClientNetworkingImpl.PLAY.hasChannel(channel);
	}

	public static Packet<?> createC2SPacket(Identifier channel, PacketByteBuf buf) {
		Objects.requireNonNull(channel, "Channel cannot be null");
		Objects.requireNonNull(buf, "Buf cannot be null");

		return ClientNetworkingImpl.createPlayC2SPacket(channel, buf);
	}

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
		Objects.requireNonNull(handler, "Network handler cannot be null");

		return ClientNetworkingImpl.getAddon(handler);
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
		getPlaySender(handler).sendPacket(channel, buf);
	}

	/**
	 * Sends a packet to the connected server.
	 *
	 * @param channel the channel of the packet
	 * @param buf the payload of the packet
	 * @throws IllegalStateException if the client's player is {@code null}
	 */
	public static void send(Identifier channel, PacketByteBuf buf) throws IllegalStateException {
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
		 * @param client the client
		 * @param sender the packet sender
		 * @param buf the payload of the packet
		 */
		void receive(ClientPlayNetworkHandler handler, MinecraftClient client, PlayPacketSender sender, PacketByteBuf buf);
	}
}
