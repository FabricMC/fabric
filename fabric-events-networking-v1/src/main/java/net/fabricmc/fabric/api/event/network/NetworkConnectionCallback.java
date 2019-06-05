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

package net.fabricmc.fabric.api.event.network;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.network.ConnectionEvents;
import net.fabricmc.fabric.impl.network.ConnectionType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.listener.ClientPlayPacketListener;

/**
 * An event that is fired when a change to the network state occurs. This can be used to listen
 * for when a client joins the server.
 *
 * <p>
 * Examples:
 *
 * <pre>
 * NetworkConnectionCallback.CLIENT_LOGIN.register(connection -> {
 * 	// login on client
 * 	ClientLoginNetworkHandler handler = (ClientLoginNetworkHandler) connection.getPacketListener();
 * });
 * NetworkConnectionCallback.CLIENT_JOIN.register(connection -> {
 * 	// joined on client
 * 	ClientPlayNetworkHandler handler = (ClientPlayNetworkHandler) connection.getPacketListener();
 * });
 * NetworkConnectionCallback.SERVER_JOIN.register(connection -> {
 * 	// joined on server
 * 	ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) listener.getPacketListener();
 * });
 * </pre>
 */
public interface NetworkConnectionCallback {

	/**
	 * Login event on the client. The packet listener will be {@link ClientLoginPacketListener}.
	 */
	Event<NetworkConnectionCallback> CLIENT_LOGIN = ConnectionEvents.getConnectionEvent(ConnectionType.CLIENT_LOGIN);
	/**
	 * Login event on the server. The packet listener will be {@link ClientLoginPacketListener}.
	 */
	Event<NetworkConnectionCallback> SERVER_LOGIN = ConnectionEvents.getConnectionEvent(ConnectionType.SERVER_LOGIN);
	/**
	 * Join event on the client. The packet listener will be {@link ClientPlayPacketListener}.
	 */
	Event<NetworkConnectionCallback> CLIENT_JOIN = ConnectionEvents.getConnectionEvent(ConnectionType.CLIENT_JOIN);
	/**
	 * Join event on the server. The packet listener will be {@link ClientPlayPacketListener}.
	 */
	Event<NetworkConnectionCallback> SERVER_JOIN = ConnectionEvents.getConnectionEvent(ConnectionType.SERVER_JOIN);
	/**
	 * Leave event on the client. The packet listener will be {@link ClientPlayPacketListener}.
	 */
	Event<NetworkConnectionCallback> CLIENT_LEAVE = ConnectionEvents.getConnectionEvent(ConnectionType.CLIENT_LEAVE);
	/**
	 * Leave event on the server. The packet listener will be {@link ClientPlayPacketListener}.
	 */
	Event<NetworkConnectionCallback> SERVER_LEAVE = ConnectionEvents.getConnectionEvent(ConnectionType.SERVER_LEAVE);

	/**
	 * Called when a connection state is changed.
	 *
	 * @param connection The network connection object
	 */
	void onConnection(ClientConnection connection);
}
