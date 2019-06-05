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

package net.fabricmc.fabric.impl.network;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.network.NetworkConnectionCallback;

import java.util.Map;

public class ConnectionEvents {

	private static final Map<ConnectionType, Event<NetworkConnectionCallback>> EVENTS = createEvents();

	private static Map<ConnectionType, Event<NetworkConnectionCallback>> createEvents() {
		return ImmutableMap.<ConnectionType, Event<NetworkConnectionCallback>>builder()
			.put(ConnectionType.CLIENT_LOGIN, createConnectEvent())
			.put(ConnectionType.CLIENT_JOIN, createConnectEvent())
			.put(ConnectionType.CLIENT_LEAVE, createConnectEvent())
			.put(ConnectionType.SERVER_LOGIN, createConnectEvent())
			.put(ConnectionType.SERVER_JOIN, createConnectEvent())
			.put(ConnectionType.SERVER_LEAVE, createConnectEvent())
			.build();
	}

	private static Event<NetworkConnectionCallback> createConnectEvent() {
		return EventFactory.createArrayBacked(NetworkConnectionCallback.class, listeners -> handler -> {
			for (NetworkConnectionCallback event : listeners) {
				event.onConnection(handler);
			}
		});
	}

	public static Event<NetworkConnectionCallback> getConnectionEvent(ConnectionType listener) {
		return EVENTS.get(listener);
	}
}
