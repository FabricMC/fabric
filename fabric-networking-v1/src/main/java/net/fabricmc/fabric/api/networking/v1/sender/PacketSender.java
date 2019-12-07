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

package net.fabricmc.fabric.api.networking.v1.sender;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

/**
 * A utility to send custom payload based packets to a network handler's recipient.
 *
 * <p>To obtain packet sender instances, call {@code PacketSenders.of} on your network handlers.
 *
 * @see PacketSenders
 */
public interface PacketSender {
	/**
	 * Sends a custom payload packet of a certain channel to the connection target.
	 *
	 * <p>The {@code buffer} can be created with {@code new PacketByteBuf(Unpooled.buffer())},
	 * which you can write your custom data to.
	 *
	 * @param channel the channel of the packet
	 * @param buffer the buffer
	 * @see #send(Identifier, PacketByteBuf, GenericFutureListener)
	 */
	void send(Identifier channel, PacketByteBuf buffer);

	/**
	 * Sends a custom payload packet of a certain channel to the connection target.
	 *
	 * <p>This is functionally similar to {@link #send(Identifier, PacketByteBuf)},
	 * but this method will invoke a callback listener  after the packet has been
	 * successfully sent by the connection.
	 *
	 * @param channel the channel of the packet
	 * @param buffer the buffer
	 * @param listener the callback listener
	 */
	void send(Identifier channel, PacketByteBuf buffer, /* Nullable */ GenericFutureListener<? extends Future<? super Void>> listener);
}
