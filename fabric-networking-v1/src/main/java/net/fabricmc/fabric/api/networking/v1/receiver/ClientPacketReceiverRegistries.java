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

import net.minecraft.util.PacketByteBuf;

import net.fabricmc.fabric.impl.networking.receiver.ClientPacketReceivers;

/**
 * The packet receiver registries that exist on the logical client.
 */
public final class ClientPacketReceiverRegistries {
	/**
	 * The packet receiver registry for play stage server to client custom payload packets.
	 */
	public static final PacketReceiverRegistry<ClientPlayPacketContext> PLAY = ClientPacketReceivers.PLAY;
	/**
	 * The packet receiver registry for login stage server to client custom login query packets.
	 *
	 * <p>Warning: All receivers for client login queries must call
	 * {@link ClientLoginQueryPacketContext#sendResponse(PacketByteBuf)} after receiving the packet! Otherwise,
	 * the server will kick the client for slow login.
	 */
	public static final PacketReceiverRegistry<ClientLoginQueryPacketContext> LOGIN_QUERY = ClientPacketReceivers.LOGIN_QUERY;

	private ClientPacketReceiverRegistries() {
	}
}
