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

/**
 * Holds event instances of client to server custom payload packet channel registration.
 *
 * <p>The events are fired on the logical server's network thread (asynchronously).
 */
@FunctionalInterface
public interface ServerPacketChannelCallback extends PacketChannelCallback<ServerPlayNetworkHandler> {
	/**
	 * Event for a client declaring its acceptance of custom payload packets
	 * on certain channels to a server play network handler.
	 */
	Event<ServerPacketChannelCallback> CLIENT_REGISTERED = EventFactory.createArrayBacked(ServerPacketChannelCallback.class, callbacks -> (handler, sender, channels) -> {
		for (ServerPacketChannelCallback callback : callbacks) {
			callback.accept(handler, sender, channels);
		}
	});
	/**
	 * Event for a client declaring its rejection of custom payload packets
	 * on certain channels to a server play network handler.
	 */
	Event<ServerPacketChannelCallback> CLIENT_UNREGISTERED = EventFactory.createArrayBacked(ServerPacketChannelCallback.class, callbacks -> (handler, sender, channels) -> {
		for (ServerPacketChannelCallback callback : callbacks) {
			callback.accept(handler, sender, channels);
		}
	});
}
