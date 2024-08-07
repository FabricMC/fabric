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

package net.fabricmc.fabric.api.modprotocol.v1;


import it.unimi.dsi.fastutil.objects.Object2IntMap;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.modprotocol.RemoteProtocolStorage;

/**
 * Utility methods allowing to get protocol versions supported by the player.
 *
 * <p>Protocol identifier's can be any valid identifier, through by default mods defining it will use "mod" namespace and path equal to its id.
 * See {@link ModProtocolIds} for more information.</>
 */
public final class ServerModProtocolLookup {
	public static final int UNSUPPORTED = -1;
	private ServerModProtocolLookup() {}

	/**
	 * Gets protocol version supported by the player
	 *
	 * @param player the player
	 * @param protocolId protocol's id
	 * @return Protocol version supported by the player
	 */
	public static int getSupportedProtocol(ServerPlayerEntity player, Identifier protocolId) {
		return RemoteProtocolStorage.getProtocol(player.networkHandler, protocolId);
	}
	/**
	 * Gets protocol version supported by the player
	 *
	 * @param handler the network handler owned by the player you want to check protocol for
	 * @param protocolId protocol's id
	 * @return Protocol version supported by the player
	 */
	public static int getSupportedProtocol(ServerCommonNetworkHandler handler, Identifier protocolId) {
		return RemoteProtocolStorage.getProtocol(handler, protocolId);
	}

	/**
	 * Gets protocol version supported by the server
	 *
	 * @param connection the ClientConnection connected to the server
	 * @param protocolId protocol's id
	 * @return Protocol version supported by the server
	 */
	public static int getSupportedProtocol(ClientConnection connection, Identifier protocolId) {
		return RemoteProtocolStorage.getProtocol(connection, protocolId);
	}

	/**
	 * Gets protocol version supported by the player
	 *
	 * @param player the player
	 * @param protocol protocol to check against
	 * @return Protocol version supported by the player
	 */
	public static int getSupportedProtocol(ServerPlayerEntity player, ModProtocol protocol) {
		return RemoteProtocolStorage.getProtocol(player.networkHandler, protocol.id());
	}
	/**
	 * Gets protocol version supported by the player
	 *
	 * @param handler the network handler owned by the player you want to check protocol for
	 * @param protocol protocol to check against
	 * @return Protocol version supported by the player
	 */
	public static int getSupportedProtocol(ServerCommonNetworkHandler handler, ModProtocol protocol) {
		return RemoteProtocolStorage.getProtocol(handler, protocol.id());
	}

	/**
	 * Gets protocol version supported by the server
	 *
	 * @param connection the ClientConnection connected to the server
	 * @param protocol protocol to check against
	 * @return Protocol version supported by the server
	 */
	public static int getSupportedProtocol(ClientConnection connection, ModProtocol protocol) {
		return RemoteProtocolStorage.getProtocol(connection, protocol.id());
	}

	/**
	 * Gets all protocols supported by the player
	 *
	 * @param player the player
	 * @return Map of protocols supported by the player
	 */
	public static Object2IntMap<Identifier> getAllSupportedProtocols(ServerPlayerEntity player) {
		return RemoteProtocolStorage.getMap(player.networkHandler);
	}
	/**
	 * Gets all protocols supported by the player
	 *
	 * @param handler the network handler owned by the player you want to check protocol for
	 * @return Map of protocols supported by the player
	 */
	public static Object2IntMap<Identifier> getAllSupportedProtocols(ServerCommonNetworkHandler handler) {
		return RemoteProtocolStorage.getMap(handler);
	}

	/**
	 * Gets all protocols supported by the player
	 *
	 * @param connection the ClientConnection connected to the server
	 * @return Map of protocols supported by the player
	 */
	public static Object2IntMap<Identifier> getAllSupportedProtocols(ClientConnection connection) {
		return RemoteProtocolStorage.getMap(connection);
	}
}
