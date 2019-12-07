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

import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.util.PacketByteBuf;

/**
 * A packet context for a client login query packet that is received by a client
 * login network handler.
 *
 * @see ClientPacketReceiverRegistries#LOGIN_QUERY
 */
public interface ClientLoginQueryPacketContext extends ClientPacketContext {
	/**
	 * Gets the client login network handler that received this packet.
	 *
	 * @return the client login network handler
	 */
	@Override
	ClientLoginNetworkHandler getNetworkHandler();

	/**
	 * Sends a response for this query packet.
	 *
	 * <p>The sent packet has the query id automatically set by the
	 * implementation.
	 *
	 * <p>If {@code null} is passed for the buffer, an empty buffer
	 * is sent instead, as to indicate an understood request.
	 *
	 * <p>Note: This method <b>must</b> be called after the query packet
	 * has been received! Otherwise, the server may not let the client
	 * log in and will kick the client for connection overtime.
	 *
	 * @param buffer the response data
	 */
	void sendResponse(PacketByteBuf buffer);
}
