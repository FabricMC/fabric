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

package net.fabricmc.fabric.api.client.event.lifecycle.v1;

import net.minecraft.client.MinecraftClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public final class ClientLifecycleEvents {
	private ClientLifecycleEvents() {
	}

	/**
	 * Called when the client has started and is about to tick for the first time.
	 */
	public static final Event<ClientLifecycleCallback> CLIENT_STARTED = EventFactory.createArrayBacked(ClientLifecycleCallback.class, callbacks -> client -> {
		for (ClientLifecycleCallback callback : callbacks) {
			callback.onChangeLifecycle(client);
		}
	});

	/**
	 * Called when the client begins to stop. This will fire before the client's player is disconnected if in game.
	 *
	 * <p>This will occur whether the client was gracefully stopped or the game crashes.
	 */
	public static final Event<ClientLifecycleCallback> CLIENT_STOPPING = EventFactory.createArrayBacked(ClientLifecycleCallback.class, callbacks -> client -> {
		for (ClientLifecycleCallback callback : callbacks) {
			callback.onChangeLifecycle(client);
		}
	});

	/**
	 * Called when the client has stopped.
	 */
	public static final Event<ClientLifecycleCallback> CLIENT_STOPPED = EventFactory.createArrayBacked(ClientLifecycleCallback.class, callbacks -> client -> {
		for (ClientLifecycleCallback callback : callbacks) {
			callback.onChangeLifecycle(client);
		}
	});

	public interface ClientLifecycleCallback {
		void onChangeLifecycle(MinecraftClient client);
	}
}
