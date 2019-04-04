/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.TextComponent;

/**
 * Fired on the client when it has disconnected from the server.
 */
public interface DisconnectFromServerCallback {
	static final Event<DisconnectFromServerCallback> EVENT = EventFactory.createArrayBacked(
		DisconnectFromServerCallback.class,
		(callbacks) -> (reason) -> {
			for (DisconnectFromServerCallback callback : callbacks) {
				callback.disconnected(reason);
			}
		}
	);

	/**
	 * The client has disconnected from the server.
	 *
	 * Note, this will be fired on a Netty thread, and so you should make sure to only perform thread-safe operations.
	 * @param reason The reason the client disconnected
	 */
	void disconnected(TextComponent reason);
}
