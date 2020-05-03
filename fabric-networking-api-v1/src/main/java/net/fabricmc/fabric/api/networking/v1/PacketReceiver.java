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

import net.minecraft.util.Identifier;

/**
 * Associates packets to individual packet reception handlers by channel.
 *
 * @param <C> the receiver's context beyond channel
 */
public interface PacketReceiver<C extends ListenerContext> extends ChannelAware {
	/**
	 * Registers a handler to a channel.
	 *
	 * <p>If a handler is already registered to the {@code channel}, this method
	 * will return {@code false}, and no change will be made. Use {@link
	 * #unregister(Identifier)} to unregister the existing handler.</p>
	 *
	 * @param channel the id of the channel
	 * @param handler the handler
	 * @return whether the handler is registered
	 */
	boolean register(Identifier channel, ChannelHandler<? super C> handler);

	/**
	 * Removes the handler of a channel.
	 *
	 * <p>The {@code channel} is guaranteed not to have a handler after this call.</p>
	 *
	 * @param channel the id of the channel
	 * @return the previous handler, or {@code null} if no handler was bound to the channel
	 */
	/* Nullable */ ChannelHandler<? super C> unregister(Identifier channel);

	/**
	 * Returns the collection of all channels that have handlers in this receiver.
	 *
	 * <p>This collection does not contain duplicate channels.</p>
	 *
	 * @return a collection of channels
	 */
	@Override
	Collection<Identifier> getChannels();

	/**
	 * Returns whether a channel has a handler in this receiver.
	 *
	 * @param channel the id of the channel to check
	 * @return whether the channel has a handler
	 */
	@Override
	boolean hasChannel(Identifier channel);
}
