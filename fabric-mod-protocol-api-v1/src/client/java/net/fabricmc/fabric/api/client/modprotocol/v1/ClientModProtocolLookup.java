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

package net.fabricmc.fabric.api.client.modprotocol.v1;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.modprotocol.v1.ModProtocol;
import net.fabricmc.fabric.api.modprotocol.v1.ModProtocolIds;
import net.fabricmc.fabric.impl.modprotocol.RemoteProtocolStorage;

/**
 * Utility methods allowing to get protocol versions supported by the server.
 *
 * <p>Protocol identifiers can be any valid {@link Identifier}. The default is {@code mod:(mod ID)}.
 * @see ModProtocolIds
 */
public final class ClientModProtocolLookup {
	/**
	 * A protocol version returned by {@code getSupportedProtocol} methods, when the server doesn't support the requested protocol.
	 */
	public static final int UNSUPPORTED = -1;
	private ClientModProtocolLookup() { }

	/**
	 * Gets the protocol version supported by the server.
	 *
	 * @param handler the network handler connected to the server
	 * @param protocolId protocol's id
	 * @return the protocol version supported by the server
	 */
	public static int getSupportedProtocol(ClientCommonNetworkHandler handler, Identifier protocolId) {
		return RemoteProtocolStorage.getProtocol(handler, protocolId);
	}

	/**
	 * Gets the protocol version supported by the server.
	 *
	 * @param connection the ClientConnection connected to the server
	 * @param protocolId protocol's id
	 * @return the protocol version supported by the server
	 */
	public static int getSupportedProtocol(ClientConnection connection, Identifier protocolId) {
		return RemoteProtocolStorage.getProtocol(connection, protocolId);
	}

	/**
	 * Gets the protocol version supported by the server.
	 *
	 * @param handler the network handler connected to the server
	 * @param protocol protocol to check against
	 * @return the protocol version supported by the server
	 */
	public static int getSupportedProtocol(ClientCommonNetworkHandler handler, ModProtocol protocol) {
		return RemoteProtocolStorage.getProtocol(handler, protocol.id());
	}

	/**
	 * Gets the protocol version supported by the server.
	 *
	 * @param connection the ClientConnection connected to the server
	 * @param protocol protocol to check against
	 * @return the protocol version supported by the server
	 */
	public static int getSupportedProtocol(ClientConnection connection, ModProtocol protocol) {
		return RemoteProtocolStorage.getProtocol(connection, protocol.id());
	}

	/**
	 * Gets all protocols supported by the server.
	 *
	 * @param handler the network handler connected to the server
	 * @return the map of protocols to the versions supported by the server
	 */
	public static Object2IntMap<Identifier> getAllSupportedProtocols(ServerCommonNetworkHandler handler) {
		return RemoteProtocolStorage.getMap(handler);
	}

	/**
	 * Gets all protocols supported by the server.
	 *
	 * @param connection the ClientConnection connected to the server
	 * @return the map of protocols to the versions supported by the server
	 */
	public static Object2IntMap<Identifier> getAllSupportedProtocols(ClientConnection connection) {
		return RemoteProtocolStorage.getMap(connection);
	}
}
