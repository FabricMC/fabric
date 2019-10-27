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

/**
 * Event for listening to packet type registrations and unregistrations
 * (also known as "minecraft:register" and "minecraft:unregister")
 * in the server -&gt; client direction.
 */
public interface S2CPacketTypeCallback {
	Event<S2CPacketTypeCallback> REGISTERED = EventFactory.createArrayBacked(
			S2CPacketTypeCallback.class,
			(callbacks) -> (types) -> {
				for (S2CPacketTypeCallback callback : callbacks) {
					callback.accept(types);
				}
			}
	);

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
