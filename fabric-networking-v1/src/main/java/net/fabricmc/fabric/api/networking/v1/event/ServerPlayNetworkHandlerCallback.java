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

package net.fabricmc.fabric.api.networking.v1.event;

import net.minecraft.server.network.ServerPlayNetworkHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.sender.ServerPacketSenders;

/**
 * The callback for the server play network handler state changes.
 *
 * <p>All server play network handler state events are fired on the server thread.
 */
@FunctionalInterface
public interface ServerPlayNetworkHandlerCallback extends NetworkHandlerCallback<ServerPlayNetworkHandler> {
	/**
	 * Event for the initialization of a server play network handler.
	 *
	 * <p>At this point, the
	 * {@link ServerPacketSenders#of(ServerPlayNetworkHandler) packet sender}
	 * has been set up and the custom payload initial register packet has been received.
	 * You can send your custom initial packets as well.
	 */
	Event<ServerPlayNetworkHandlerCallback> INITIALIZED = EventFactory.createArrayBacked(ServerPlayNetworkHandlerCallback.class, callbacks -> handler -> {
		for (ServerPlayNetworkHandlerCallback callback : callbacks) {
			callback.handle(handler);
		}
	});
	/**
	 * Event for the disconnection and cleanup of a server play network handler.
	 *
	 * <p>At this point, the connection has been closed; no more packets can be
	 * sent.
	 *
	 * <p>This event is not guaranteed to be fired for every server play network
	 * handler; it may be skipped if the packet listener of the connection
	 * has been set from a server play network handler to something else.
	 */
	Event<ServerPlayNetworkHandlerCallback> DISCONNECTED = EventFactory.createArrayBacked(ServerPlayNetworkHandlerCallback.class, callbacks -> handler -> {
		for (ServerPlayNetworkHandlerCallback callback : callbacks) {
			callback.handle(handler);
		}
	});
}
