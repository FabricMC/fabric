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

import java.util.Collection;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;

/**
 * Event for listening to packet type registration and unregistration notifications
 * (also known as "minecraft:register" and "minecraft:unregister") sent by a server.
 *
 * <p>Registrations received will be for <em>client -&gt; server</em> packets
 * that the sending server can understand.
 *
 * @deprecated Please migrate to {@link S2CPlayChannelEvents}.
 */
@Deprecated
public interface S2CPacketTypeCallback {
	/**
	 * @deprecated Please migrate to {@link S2CPlayChannelEvents#REGISTER}.
	 */
	@Deprecated
	Event<S2CPacketTypeCallback> REGISTERED = EventFactory.createArrayBacked(
			S2CPacketTypeCallback.class,
			(callbacks) -> (types) -> {
				for (S2CPacketTypeCallback callback : callbacks) {
					callback.accept(types);
				}
			}
	);

	/**
	 * @deprecated Please migrate to {@link S2CPlayChannelEvents#UNREGISTER}.
	 */
	@Deprecated
	Event<S2CPacketTypeCallback> UNREGISTERED = EventFactory.createArrayBacked(
			S2CPacketTypeCallback.class,
			(callbacks) -> (types) -> {
				for (S2CPacketTypeCallback callback : callbacks) {
					callback.accept(types);
				}
			}
	);

	/**
	 * Accept a collection of types.
	 *
	 * @param types The provided collection of types.
	 */
	void accept(Collection<Identifier> types);
}
