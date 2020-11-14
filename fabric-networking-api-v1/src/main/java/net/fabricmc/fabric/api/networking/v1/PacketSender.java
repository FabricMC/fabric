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

import java.util.Objects;

import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Represents something that supports sending packets to channels.
 * @see PacketByteBufs
 */
public interface PacketSender {
	/**
	 * Makes a packet for a channel.
	 *
	 * @param channelName the id of the channel
	 * @param buf     the content of the packet
	 */
	Packet<?> createPacket(Identifier channelName, PacketByteBuf buf);

	/**
	 * Sends a packet.
	 *
	 * @param packet the packet
	 */
	void sendPacket(Packet<?> packet);

	/**
	 * Sends a packet.
	 *
	 * @param packet the packet
	 * @param callback an optional callback to execute after the packet is sent, may be {@code null}. The callback may also accept a {@link ChannelFutureListener}.
	 */
	void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback);

	/**
	 * Sends a packet to a channel.
	 *
	 * @param channel the id of the channel
	 * @param buf the content of the packet
	 */
	default void sendPacket(Identifier channel, PacketByteBuf buf) {
		Objects.requireNonNull(channel, "Channel cannot be null");
		Objects.requireNonNull(buf, "Payload cannot be null");

		this.sendPacket(this.createPacket(channel, buf));
	}

	/**
	 * Sends a packet to a channel.
	 *
	 * @param channel  the id of the channel
	 * @param buf the content of the packet
	 * @param callback an optional callback to execute after the packet is sent, may be {@code null}
	 */
	// the generic future listener can accept ChannelFutureListener
	default void sendPacket(Identifier channel, PacketByteBuf buf, @Nullable GenericFutureListener<? extends Future<? super Void>> callback) {
		Objects.requireNonNull(channel, "Channel cannot be null");
		Objects.requireNonNull(buf, "Payload cannot be null");

		this.sendPacket(this.createPacket(channel, buf), callback);
	}
}
