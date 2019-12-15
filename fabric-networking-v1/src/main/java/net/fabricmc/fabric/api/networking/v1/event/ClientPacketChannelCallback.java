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

import net.minecraft.client.network.ClientPlayNetworkHandler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Holds event instances of server to client custom payload packet channel registration.
 *
 * <p>The events are fired on the logical client's network thread (asynchronously).
 *
 * <p>This subclass is created to avoid classloading errors on dedicated servers.
 */
@FunctionalInterface
public interface ClientPacketChannelCallback extends PacketChannelCallback<ClientPlayNetworkHandler> {
	/**
	 * Event for a server declaring its acceptance of custom payload packets
	 * on certain channels to a client play network handler.
	 */
	Event<ClientPacketChannelCallback> SERVER_REGISTERED = EventFactory.createArrayBacked(ClientPacketChannelCallback.class, callbacks -> (handler, sender, channels) -> {
		for (ClientPacketChannelCallback callback : callbacks) {
			callback.accept(handler, sender, channels);
		}
	});
	/**
	 * Event for a server declaring its rejection of custom payload packets
	 * on certain channels to a client play network handler.
	 */
	Event<ClientPacketChannelCallback> SERVER_UNREGISTERED = EventFactory.createArrayBacked(ClientPacketChannelCallback.class, callbacks -> (handler, sender, channels) -> {
		for (ClientPacketChannelCallback callback : callbacks) {
			callback.accept(handler, sender, channels);
		}
	});
}
