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

package net.fabricmc.fabric.api.networking.v1.receiver;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.sender.PacketSender;

/**
 * A registry for packet receivers on different custom payload packet channels.
 *
 * <p>This registry is applied to all network handlers, unlike packet senders that
 * are bound to individual network handlers.
 *
 * @param <T> the packet context type
 * @see ClientPacketReceiverRegistries
 * @see ServerPacketReceiverRegistries
 * @see PacketSender
 */
public interface PacketReceiverRegistry<T extends PacketContext> {
	/**
	 * Register a packet receiver to a channel.
	 *
	 * @param channel the channel it handles
	 * @param receiver the packet receiver
	 * @return true if the receiver is successfully registered
	 */
	boolean register(Identifier channel, PacketReceiver<? super T> receiver);

	/**
	 * Unregister a packet receiver from a channel.
	 *
	 * @param channel the channel it handles
	 * @return true if the receiver is successfully unregistered
	 */
	boolean unregister(Identifier channel);
}
