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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Event for listening to packet type registration and unregistration notifications
 * (also known as "minecraft:register" and "minecraft:unregister") sent by a client.
 *
 * <p>Registrations received will be for <em>server -&gt; client</em> packets
 * that the sending client can understand.
 *
 * @deprecated Please migrate to {@link C2SPlayChannelEvents}.
 */
@Deprecated
public interface C2SPacketTypeCallback {
	/**
	 * @deprecated Please migrate to {@link C2SPlayChannelEvents#REGISTER}.
	 */
	@Deprecated
	Event<C2SPacketTypeCallback> REGISTERED = EventFactory.createArrayBacked(
			C2SPacketTypeCallback.class,
			(callbacks) -> (client, types) -> {
				for (C2SPacketTypeCallback callback : callbacks) {
					callback.accept(client, types);
				}
			}
	);

	/**
	 * @deprecated Please migrate to {@link C2SPlayChannelEvents#UNREGISTER}.
	 */
	@Deprecated
	Event<C2SPacketTypeCallback> UNREGISTERED = EventFactory.createArrayBacked(
			C2SPacketTypeCallback.class,
			(callbacks) -> (client, types) -> {
				for (C2SPacketTypeCallback callback : callbacks) {
					callback.accept(client, types);
				}
			}
	);

	/**
	 * Accept a collection of types.
	 *
	 * @param client The player who is the source of the packet.
	 * @param types  The provided collection of types.
	 */
	void accept(PlayerEntity client, Collection<Identifier> types);
}
