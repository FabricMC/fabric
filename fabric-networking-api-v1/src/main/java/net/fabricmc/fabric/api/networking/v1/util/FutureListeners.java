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

package net.fabricmc.fabric.api.networking.v1.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import net.minecraft.network.PacketByteBuf;

/**
 * Utilities for working with netty's future listeners.
 */
public final class FutureListeners {
	/**
	 * Returns a future listener that releases a packet byte buf when the buffer has
	 * been sent to a remote connection.
	 *
	 * @param buf the buffer
	 * @return the future listener
	 */
	public static ChannelFutureListener free(PacketByteBuf buf) {
		return (future) -> {
			if (!isLocalChannel(future.channel())) {
				buf.release();
			}
		};
	}

	/**
	 * Returns whether a netty channel performs local transportation, or if the
	 * message objects in the channel are directly passed than written to and
	 * read from a byte buf.
	 *
	 * @param channel the channel to check
	 * @return whether the channel is local
	 */
	public static boolean isLocalChannel(Channel channel) {
		return channel instanceof LocalServerChannel || channel instanceof LocalChannel;
	}

	/**
	 * Combines two future listeners.
	 *
	 * @param first  the first future listener
	 * @param second the second future listener
	 * @param <A>    the future type of the first listener, used for casting
	 * @param <B>    the future type of the second listener, used for casting
	 * @return the combined future listener.
	 */
	@SuppressWarnings("unchecked") // A, B exist just to allow casting lol
	public static <A extends Future<? super Void>, B extends Future<? super Void>> GenericFutureListener<? extends Future<? super Void>> union(
			GenericFutureListener<A> first, GenericFutureListener<B> second) {
		if (first == null) {
			return second;
		}

		if (second == null) {
			return first;
		}

		return (future) -> {
			first.operationComplete((A) future);
			second.operationComplete((B) future);
		};
	}
}
