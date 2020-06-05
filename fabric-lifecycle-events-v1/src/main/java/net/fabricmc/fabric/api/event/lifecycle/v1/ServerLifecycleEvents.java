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
import net.fabricmc.fabric.impl.lifecycle.ServerLifecycleInternals;

public final class ServerLifecycleEvents {
	private ServerLifecycleEvents() {
	}

	/**
	 * Called when the server has started and is about to tick for the first time.
	 *
	 * <p>At this stage, all worlds are live.
	 */
	public static final Event<ServerLifecycleEvents.LifecycleCallback> SERVER_STARTED = EventFactory.createArrayBacked(ServerLifecycleEvents.LifecycleCallback.class, (callbacks) -> (server) -> {
		for (ServerLifecycleEvents.LifecycleCallback callback : callbacks) {
			callback.onChangeLifecycle(server);
		}
	});

	/**
	 * Called when the server has started shutting down. This occurs before the server's network channel is closed and before any players are disconnected.
	 *
	 * <p>All worlds are still present and can be modified.
	 */
	public static final Event<ServerLifecycleEvents.LifecycleCallback> SERVER_STOPPING = EventFactory.createArrayBacked(ServerLifecycleEvents.LifecycleCallback.class, (callbacks) -> (server) -> {
		for (ServerLifecycleEvents.LifecycleCallback callback : callbacks) {
			callback.onChangeLifecycle(server);
		}
	});

	/**
	 * Called when the server has stopped. All worlds have been closed and all (block)entities and players have been unloaded.
	 *
	 * <p>On a {@link net.fabricmc.api.EnvType#SERVER dedicated server}, this will be the last event called.
	 * Otherwise the client will continue to tick.
	 */
	public static final Event<ServerLifecycleEvents.LifecycleCallback> SERVER_STOPPED = EventFactory.createArrayBacked(ServerLifecycleEvents.LifecycleCallback.class, callbacks -> server -> {
		for (ServerLifecycleEvents.LifecycleCallback callback : callbacks) {
			callback.onChangeLifecycle(server);
		}
	});

	/**
	 * Gets the currently running server.
	 *
	 * <p><b>Use of this method is highly impractical and not recommended since there is no real restriction on whether the game engine could run multiple servers concurrently.</b>
	 * One should attempt to obtain the server instance from a {@link ServerWorld server world} or via other means.
	 *
	 * <p>The server instance returned SHOULD NOT be cached! Call the method every time you need the server.
	 *
	 * @return the currently running server
	 * @throws IllegalStateException if the server is not available
	 */
	public static MinecraftServer getCurrentServer() {
		final MinecraftServer server = ServerLifecycleInternals.getServer();

		if (server != null) {
			return server;
		}

		throw new IllegalStateException("Server was not available");
	}

	/**
	 * Checks if the current server is available.
	 * he server may not always be available on a {@link net.fabricmc.api.EnvType#CLIENT client}, so it is advised to verify this is {@code true} before calling {@link ServerLifecycleEvents#getCurrentServer()}
	 *
	 * <p><b>Use of this method is highly impractical and not recommended since there is no real restriction on whether the game engine could run multiple servers concurrently.</b>
	 * One should attempt to obtain the server instance from a {@link ServerWorld server world} or via other means.
	 *
	 * @return true if the server is available.
	 */
	public static boolean isServerAvailable() {
		return ServerLifecycleInternals.getServer() != null;
	}

	public interface LifecycleCallback {
		void onChangeLifecycle(MinecraftServer server);
	}
}
