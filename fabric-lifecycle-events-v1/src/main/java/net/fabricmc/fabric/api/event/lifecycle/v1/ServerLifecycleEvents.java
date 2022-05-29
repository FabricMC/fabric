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

import org.jetbrains.annotations.Nullable;

import net.minecraft.resource.LifecycledResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

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
	 * Called when a Minecraft server is about to send tag and recipe data to one or multiple players.
	 * @see SyncDataPackContents
	 */
	public static final Event<SyncDataPackContents> SYNC_DATA_PACK_CONTENTS = EventFactory.createArrayBacked(SyncDataPackContents.class, callbacks -> (playerManager, player) -> {
		for (SyncDataPackContents callback : callbacks) {
			callback.syncDataPackContents(playerManager, player);
		}
	});

	/**
	 * Called before a Minecraft server reloads data packs.
	 */
	public static final Event<StartDataPackReload> START_DATA_PACK_RELOAD = EventFactory.createArrayBacked(StartDataPackReload.class, callbacks -> (server, serverResourceManager) -> {
		for (StartDataPackReload callback : callbacks) {
			callback.startDataPackReload(server, serverResourceManager);
		}
	});

	/**
	 * Called after a Minecraft server has reloaded data packs.
	 *
	 * <p>If reloading data packs was unsuccessful, the current data packs will be kept.
	 */
	public static final Event<EndDataPackReload> END_DATA_PACK_RELOAD = EventFactory.createArrayBacked(EndDataPackReload.class, callbacks -> (server, serverResourceManager, success) -> {
		for (EndDataPackReload callback : callbacks) {
			callback.endDataPackReload(server, serverResourceManager, success);
		}
	});

	@FunctionalInterface
	public interface ServerStarting {
		void onServerStarting(MinecraftServer server);
	}

	@FunctionalInterface
	public interface ServerStarted {
		void onServerStarted(MinecraftServer server);
	}

	@FunctionalInterface
	public interface ServerStopping {
		void onServerStopping(MinecraftServer server);
	}

	@FunctionalInterface
	public interface ServerStopped {
		void onServerStopped(MinecraftServer server);
	}

	@FunctionalInterface
	public interface SyncDataPackContents {
		/**
		 * Called right before tags and recipes are sent to one or multiple clients.
		 * The {@linkplain MinecraftServer#getResourceManager() server resource manager} is up-to-date when this is called.
		 *
		 * <p>If {@code player} is non-{@code null}, then that player just joined and data should only be sent to that player.
		 * Otherwise, if {@code player} is {@code null}, the server just finished a reload and data should be sent to all players in the {@code playerManager}.
		 *
		 * <p>Usage example, to send a packet to each relevant player with custom data:
		 * <pre>{@code
		 * ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((playerManager, player) -> {
		 *     // 1) Encode packet
		 *     PacketByteBuf buf = PacketByteBufs.create();
		 *     // Fill buffer with data...
		 *     // Create packet
		 *     Packet<?> packet = ServerPlayNetworking.createS2CPacket(CHANNEL_NAME, buf);
		 *
		 *     // 2) Send to relevant player(s)
		 *     if (player == null) {
		 *         // Reload: send to all
		 *         playerManager.sendToAll(packet);
		 *     } else {
		 *         // Player joined: send only to that player
		 *         player.networkHandler.sendPacket(packet);
		 *     }
		 * });
		 * }</pre>
		 *
		 * @param playerManager The player manager, always present.
		 * @param player Null if the data is synced to all players, otherwise the player the data should be synced to.
		 */
		void syncDataPackContents(PlayerManager playerManager, @Nullable ServerPlayerEntity player);
	}

	@FunctionalInterface
	public interface StartDataPackReload {
		void startDataPackReload(MinecraftServer server, LifecycledResourceManager resourceManager);
	}

	@FunctionalInterface
	public interface EndDataPackReload {
		/**
		 * Called after data packs on a Minecraft server have been reloaded.
		 *
		 * <p>If the reload was not successful, the old data packs will be kept.
		 *
		 * @param server the server
		 * @param resourceManager the resource manager
		 * @param success if the reload was successful
		 */
		void endDataPackReload(MinecraftServer server, LifecycledResourceManager resourceManager, boolean success);
	}
}
