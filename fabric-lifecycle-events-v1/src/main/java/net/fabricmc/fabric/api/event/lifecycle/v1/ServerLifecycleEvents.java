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

import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerLifecycleEvents {
	private ServerLifecycleEvents() {
	}

	/**
	 * Called when a Minecraft server is starting.
	 *
	 * <p>This occurs before the {@link PlayerManager player manager} and any worlds are loaded.
	 */
	public static final Event<ServerStarting> SERVER_STARTING = EventFactory.createArrayBacked(ServerStarting.class, callbacks -> server -> {
		for (ServerStarting callback : callbacks) {
			callback.onServerStarting(server);
		}
	});

	/**
	 * Called when a Minecraft server has started and is about to tick for the first time.
	 *
	 * <p>At this stage, all worlds are live.
	 */
	public static final Event<ServerStarted> SERVER_STARTED = EventFactory.createArrayBacked(ServerStarted.class, (callbacks) -> (server) -> {
		for (ServerStarted callback : callbacks) {
			callback.onServerStarted(server);
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
	public static final Event<ServerStopping> SERVER_STOPPING = EventFactory.createArrayBacked(ServerStopping.class, (callbacks) -> (server) -> {
		for (ServerStopping callback : callbacks) {
			callback.onServerStopping(server);
		}
	});

	/**
	 * Called when a Minecraft server has stopped.
	 * All worlds have been closed and all (block)entities and players have been unloaded.
	 *
	 * <p>For example, an {@link net.fabricmc.api.EnvType#CLIENT integrated server} will begin stopping, but it's client may continue to run.
	 * Meanwhile for a {@link net.fabricmc.api.EnvType#SERVER dedicated server}, this will be the last event called.
	 */
	public static final Event<ServerStopped> SERVER_STOPPED = EventFactory.createArrayBacked(ServerStopped.class, callbacks -> server -> {
		for (ServerStopped callback : callbacks) {
			callback.onServerStopped(server);
		}
	});

	/**
	 * Called before a Minecraft server reloads data packs.
	 *
	 * <p>This event will be followed by {@link #END_DATA_PACK_RELOAD} if the reload was successful.
	 * If the data pack reload failed, then {@link #DATA_PACK_RELOAD_FAIL} will be fired.
	 */
	public static final Event<BeforeDataPackReload> START_DATA_PACK_RELOAD = EventFactory.createArrayBacked(BeforeDataPackReload.class, callbacks -> (server, serverResourceManager) -> {
		for (BeforeDataPackReload callback : callbacks) {
			callback.beforeDataPackReload(server, serverResourceManager);
		}
	});

	/**
	 * Called after a Minecraft server has reloaded data packs.
	 *
	 * <p>When this event is fired, the reloaded data packs will be applied.
	 */
	public static final Event<AfterDataPackReload> END_DATA_PACK_RELOAD = EventFactory.createArrayBacked(AfterDataPackReload.class, callbacks -> (server, serverResourceManager) -> {
		for (AfterDataPackReload callback : callbacks) {
			callback.afterDataPackReload(server, serverResourceManager);
		}
	});

	/**
	 * Called when reloading data packs on a Minecraft server has failed.
	 *
	 * <p>When this event is fired, the currently loaded data packs will not be replaced.
	 */
	public static final Event<FailDataPackReload> DATA_PACK_RELOAD_FAIL = EventFactory.createArrayBacked(FailDataPackReload.class, callbacks -> (throwable, server, serverResourceManager) -> {
		for (FailDataPackReload callback : callbacks) {
			callback.failDataPackReload(throwable, server, serverResourceManager);
		}
	});

	public interface ServerStarting {
		void onServerStarting(MinecraftServer server);
	}

	public interface ServerStarted {
		void onServerStarted(MinecraftServer server);
	}

	public interface ServerStopping {
		void onServerStopping(MinecraftServer server);
	}

	public interface ServerStopped {
		void onServerStopped(MinecraftServer server);
	}

	public interface BeforeDataPackReload {
		void beforeDataPackReload(MinecraftServer server, ServerResourceManager serverResourceManager);
	}

	public interface AfterDataPackReload {
		void afterDataPackReload(MinecraftServer server, ServerResourceManager serverResourceManager);
	}

	public interface FailDataPackReload {
		void failDataPackReload(Throwable throwable, MinecraftServer server, ServerResourceManager serverResourceManager);
	}
}
