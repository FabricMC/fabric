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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

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
	 * Called when a world is loaded by a Minecraft server.
	 *
	 * <p>For example, this can be used to load world specific metadata or initialize a {@link PersistentState} on a server world.
	 */
	public static final Event<LoadWorld> LOAD_WORLD = EventFactory.createArrayBacked(LoadWorld.class, callbacks -> (server, world) -> {
		for (LoadWorld callback : callbacks) {
			callback.onWorldLoaded(server, world);
		}
	});

	/**
	 * Called before a Minecraft server reloads datapacks.
	 */
	public static final Event<BeforeResourceReload> BEFORE_RESOURCE_RELOAD = EventFactory.createArrayBacked(BeforeResourceReload.class, callbacks -> (server, serverResourceManager) -> {
		for (BeforeResourceReload callback : callbacks) {
			callback.beforeResourceReload(server, serverResourceManager);
		}
	});

	/**
	 * Called after a Minecraft server has reloaded datapacks.
	 */
	public static final Event<AfterResourceReload> AFTER_RESOURCE_RELOAD = EventFactory.createArrayBacked(AfterResourceReload.class, callbacks -> (server, serverResourceManager) -> {
		for (AfterResourceReload callback : callbacks) {
			callback.afterResourceReload(server, serverResourceManager);
		}
	});

	/**
	 * Called before a Minecraft server saves all worlds and world properties.
	 *
	 * <p>Mods can use this event to save cached data.
	 */
	public static final Event<Save> SAVE = EventFactory.createArrayBacked(Save.class, callbacks -> (server, flush) -> {
		for (Save callback : callbacks) {
			callback.onSave(server, flush);
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

	public interface LoadWorld {
		void onWorldLoaded(MinecraftServer server, ServerWorld world);
	}

	public interface BeforeResourceReload {
		void beforeResourceReload(MinecraftServer server, ServerResourceManager serverResourceManager);
	}

	public interface AfterResourceReload {
		void afterResourceReload(MinecraftServer server, ServerResourceManager serverResourceManager);
	}

	public interface Save {
		/**
		 * Called before worlds and world properties are saved.
		 *
		 * @param server the server
		 * @param flush specifies whether the worlds should unload all chunks, typically during shutdown
		 */
		void onSave(MinecraftServer server, boolean flush);
	}
}
