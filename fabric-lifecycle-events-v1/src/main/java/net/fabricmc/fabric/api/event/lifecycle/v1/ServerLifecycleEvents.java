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

package net.fabricmc.fabric.api.event.lifecycle.v1;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerLifecycleEvents {
	private ServerLifecycleEvents() {
	}

	/**
	 * Called when a Minecraft server has started and is about to tick for the first time.
	 *
	 * <p>At this stage, all worlds are live.
	 */
	public static final Event<ServerLifecycleEvents.LifecycleCallback> SERVER_STARTED = EventFactory.createArrayBacked(ServerLifecycleEvents.LifecycleCallback.class, (callbacks) -> (server) -> {
		for (ServerLifecycleEvents.LifecycleCallback callback : callbacks) {
			callback.onChangeLifecycle(server);
		}
	});

	/**
	 * Called when a Minecraft server has started shutting down.
	 * This occurs before the server's network channel is closed and before any players are disconnected.
	 *
	 * <p>For example, an integrated server will begin stopping, but it's client may continue to run.
	 *
	 * <p>All worlds are still present and can be modified.
	 */
	public static final Event<ServerLifecycleEvents.LifecycleCallback> SERVER_STOPPING = EventFactory.createArrayBacked(ServerLifecycleEvents.LifecycleCallback.class, (callbacks) -> (server) -> {
		for (ServerLifecycleEvents.LifecycleCallback callback : callbacks) {
			callback.onChangeLifecycle(server);
		}
	});

	/**
	 * Called when a Minecraft server has stopped.
	 * All worlds have been closed and all (block)entities and players have been unloaded.
	 *
	 * <p>For example, an {@link net.fabricmc.api.EnvType#CLIENT integrated server} will begin stopping, but it's client may continue to run.
	 * Meanwhile for a {@link net.fabricmc.api.EnvType#SERVER dedicated server}, this will be the last event called.
	 */
	public static final Event<ServerLifecycleEvents.LifecycleCallback> SERVER_STOPPED = EventFactory.createArrayBacked(ServerLifecycleEvents.LifecycleCallback.class, callbacks -> server -> {
		for (ServerLifecycleEvents.LifecycleCallback callback : callbacks) {
			callback.onChangeLifecycle(server);
		}
	});

	public interface LifecycleCallback {
		void onChangeLifecycle(MinecraftServer server);
	}
}
