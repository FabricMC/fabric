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

import java.util.concurrent.Future;

import net.minecraft.server.network.ServerLoginNetworkHandler;

import net.fabricmc.fabric.api.networking.v1.sender.PacketSender;

/**
 * A packet context for a client login query response packet that is received by
 * a server login network handler.
 *
 * @see PacketSender
 * @see ServerPacketReceiverRegistries#LOGIN_QUERY_RESPONSE
 */
public interface ServerLoginQueryResponsePacketContext extends ServerPacketContext {
	@Override
	ServerLoginNetworkHandler getNetworkHandler();

	/**
	 * Indicates if the client receiving the query was able to understand
	 * this query.
	 *
	 * <p>The buffer passed to the receiver will always be empty if this
	 * method returns {@code false}.
	 *
	 * @return true if the query was understood by the client
	 */
	boolean isUnderstood();

	/**
	 * Gets the custom payload based login query packet sender for this
	 * server login network handler.
	 *
	 * <p>This packet sender may be useful in case an additional query
	 * is to be sent after a response.
	 *
	 * @return the packet sender
	 * @see PlayPacketContext#getPacketSender()
	 */
	PacketSender getPacketSender();

	/**
	 * Adds a future that prevents the acceptance of player until the
	 * future is completed.
	 *
	 * <p>For instance, if you need to send another query packet after
	 * handling this response on the server thread, send the completable
	 * future returned by {@code MinecraftServer.submit(() -> )} into this
	 * method. Otherwise, the login handler may accept the player before
	 * the subsequent query packet is sent after computation is done on
	 * the server thread.
	 *
	 * @param future the future to complete before player acceptance
	 */
	void addLoginHold(Future<?> future);
}
