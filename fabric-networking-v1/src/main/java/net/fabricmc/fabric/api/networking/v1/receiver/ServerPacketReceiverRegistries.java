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

import net.fabricmc.fabric.impl.networking.receiver.ServerPacketReceivers;

/**
 * The packet receiver registries that exist on the logical server.
 */
public final class ServerPacketReceiverRegistries {
	/**
	 * The packet receiver registry for play stage client to server custom payload packets.
	 */
	public static final PacketReceiverRegistry<ServerPlayPacketContext> PLAY = ServerPacketReceivers.PLAY;
	/**
	 * The packet receiver registry for login stage client to server custom login query response packets.
	 *
	 * <p>If a client login query is registered without a corresponding login query response, the response packet
	 * is simply taken as the client has finished a response.
	 */
	public static final PacketReceiverRegistry<ServerLoginQueryResponsePacketContext> LOGIN_QUERY_RESPONSE = ServerPacketReceivers.LOGIN_QUERY_RESPONSE;

	private ServerPacketReceiverRegistries() {
	}
}
