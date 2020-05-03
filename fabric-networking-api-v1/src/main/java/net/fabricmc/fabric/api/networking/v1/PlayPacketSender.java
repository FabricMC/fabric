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

import java.util.Collection;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Supports sending packets to channels in the play network handlers.
 *
 * <p>Compared to a simple packet sender, the play packet sender is informed
 * if its connected recipient may {@link #hasChannel(Identifier) accept packets
 * in certain channels}. When the {@code fabric-networking-api-v1.warnUnregisteredPackets}
 * system property is absent or set to {@code true} and the recipient did not
 * declare its ability to receive packets in a channel a packet was sent in, a
 * warning is logged.</p>
 */
public interface PlayPacketSender extends PacketSender, ChannelAware {
	/**
	 * {@inheritDoc}
	 *
	 * <p>When the {@code fabric-networking-api-v1.warnUnregisteredPackets} system
	 * property is absent or set to {@code true} and the {@code channel} is not
	 * {@linkplain #hasChannel(Identifier) registered}, a warning will be logged.</p>
	 *
	 * @param channel the id of the channel
	 * @param buf     the content of the packet
	 */
	@Override
	void sendPacket(Identifier channel, PacketByteBuf buf);

	/**
	 * {@inheritDoc}
	 *
	 * <p>When the {@code fabric-networking-api-v1.warnUnregisteredPackets} system
	 * property is absent or set to {@code true} and the {@code channel} is not
	 * {@linkplain #hasChannel(Identifier) registered}, a warning will be logged.</p>
	 *
	 * @param channel  the id of the channel
	 * @param buf      the content of the packet
	 * @param callback an optional callback to execute after the packet is sent, may be {@code null}
	 */
	@Override
	void sendPacket(Identifier channel, PacketByteBuf buf, /* Nullable */ GenericFutureListener<? extends Future<? super Void>> callback);

	/**
	 * Returns the ids of all channels the recipient side of this sender has declared
	 * ability to receive.
	 *
	 * <p>This collection does not contain duplicate channels.</p>
	 *
	 * @return a collection of channels
	 */
	@Override
	Collection<Identifier> getChannels();

	/**
	 * Returns if the recipient side of this sender has declared its ability to receive
	 * in a certain channel.
	 *
	 * @param channel the id of the channel to check
	 * @return whether the recipient declares it can receive in that channel
	 */
	@Override
	boolean hasChannel(Identifier channel);
}
