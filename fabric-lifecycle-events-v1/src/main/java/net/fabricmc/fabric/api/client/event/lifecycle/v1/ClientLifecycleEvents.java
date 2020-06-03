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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.GameLifecycleCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.GameTickCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.block.entity.BlockEntityLoadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.block.entity.BlockEntityUnloadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.chunk.ChunkLoadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.chunk.ChunkUnloadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.entity.EntityLoadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.entity.EntityUnloadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.world.WorldTickCallback;

@Environment(EnvType.CLIENT)
public final class ClientLifecycleEvents {
	private ClientLifecycleEvents() {
	}

	/**
	 * Called when a client ticks.
	 */
	public static final Event<GameTickCallback<MinecraftClient>> CLIENT_TICK = EventFactory.createArrayBacked(GameTickCallback.class, callbacks -> client -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = client.getProfiler();
			profiler.push("fabricServerTick");

			for (GameTickCallback<MinecraftClient> event : callbacks) {
				profiler.push(EventFactory.getHandlerName(event));
				event.onTick(client);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (GameTickCallback<MinecraftClient> event : callbacks) {
				event.onTick(client);
			}
		}
	});

	/**
	 * Called when a ClientWorld ticks.
	 */
	public static final Event<WorldTickCallback<ClientWorld>> WORLD_TICK = EventFactory.createArrayBacked(WorldTickCallback.class, callbacks -> world -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricClientWorldTick");

			for (WorldTickCallback<ClientWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onTick(world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (WorldTickCallback<ClientWorld> callback : callbacks) {
				callback.onTick(world);
			}
		}
	});

	/**
	 * Called when a chunk is loaded into a ClientWorld.
	 */
	public static final Event<ChunkLoadCallback<ClientWorld>> CHUNK_LOAD = EventFactory.createArrayBacked(ChunkLoadCallback.class, callbacks -> (clientWorld, chunk) -> {
		if (EventFactory.isProfilingEnabled()) {
			Profiler profiler = clientWorld.getProfiler();
			profiler.push("fabricClientChunkLoad");

			for (ChunkLoadCallback<ClientWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onChunkLoad(clientWorld, chunk);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ChunkLoadCallback<ClientWorld> callback : callbacks) {
				callback.onChunkLoad(clientWorld, chunk);
			}
		}
	});

	/**
	 * Called when a chunk is unloaded from a ClientWorld.
	 */
	public static final Event<ChunkUnloadCallback<ClientWorld>> CHUNK_UNLOAD = EventFactory.createArrayBacked(ChunkUnloadCallback.class, callbacks -> (clientWorld, chunk) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = clientWorld.getProfiler();
			profiler.push("fabricClientChunkUnload");

			for (ChunkUnloadCallback<ClientWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onChunkUnload(clientWorld, chunk);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ChunkUnloadCallback<ClientWorld> callback : callbacks) {
				callback.onChunkUnload(clientWorld, chunk);
			}
		}
	});

	/**
	 * Called when a BlockEntity is loaded into a ClientWorld.
	 */
	public static final Event<BlockEntityLoadCallback<ClientWorld>> BLOCK_ENTITY_LOAD = EventFactory.createArrayBacked(BlockEntityLoadCallback.class, callbacks -> (blockEntity, world) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricClientBlockEntityLoad");

			for (BlockEntityLoadCallback<ClientWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onLoadBlockEntity(blockEntity, world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (BlockEntityLoadCallback<ClientWorld> callback : callbacks) {
				callback.onLoadBlockEntity(blockEntity, world);
			}
		}
	});

	/**
	 * Called when a BlockEntity is unloaded from a ClientWorld.
	 */
	public static final Event<BlockEntityUnloadCallback<ClientWorld>> BLOCK_ENTITY_UNLOAD = EventFactory.createArrayBacked(BlockEntityUnloadCallback.class, callbacks -> (blockEntity, world) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricClientBlockEntityUnload");

			for (BlockEntityUnloadCallback<ClientWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onUnloadBlockEntity(blockEntity, world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (BlockEntityUnloadCallback<ClientWorld> callback : callbacks) {
				callback.onUnloadBlockEntity(blockEntity, world);
			}
		}
	});

	/**
	 * Called when an Entity is loaded into a ClientWorld.
	 */
	public static final Event<EntityLoadCallback<ClientWorld>> ENTITY_LOAD = EventFactory.createArrayBacked(EntityLoadCallback.class, callbacks -> (entity, world) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricClientEntityLoad");

			for (EntityLoadCallback<ClientWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onEntityLoad(entity, world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (EntityLoadCallback<ClientWorld> callback : callbacks) {
				callback.onEntityLoad(entity, world);
			}
		}
	});

	/**
	 * Called when an Entity is unloaded from a ClientWorld.
	 */
	public static final Event<EntityUnloadCallback<ClientWorld>> ENTITY_UNLOAD = EventFactory.createArrayBacked(EntityUnloadCallback.class, callbacks -> (entity, world) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricClientEntityLoad");

			for (EntityUnloadCallback<ClientWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onEntityUnload(entity, world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (EntityUnloadCallback<ClientWorld> callback : callbacks) {
				callback.onEntityUnload(entity, world);
			}
		}
	});

	public static final Event<GameLifecycleCallback<MinecraftClient>> CLIENT_STARTING = EventFactory.createArrayBacked(GameLifecycleCallback.class, callbacks -> client -> {
		for (GameLifecycleCallback<MinecraftClient> callback : callbacks) {
			callback.onChangeLifecycle(client);
		}
	});

	public static final Event<GameLifecycleCallback<MinecraftClient>> CLIENT_STOPPING = EventFactory.createArrayBacked(GameLifecycleCallback.class, callbacks -> client -> {
		for (GameLifecycleCallback<MinecraftClient> callback : callbacks) {
			callback.onChangeLifecycle(client);
		}
	});

	public static final Event<GameLifecycleCallback<MinecraftClient>> CLIENT_STOPPED = EventFactory.createArrayBacked(GameLifecycleCallback.class, callbacks -> client -> {
		for (GameLifecycleCallback<MinecraftClient> callback : callbacks) {
			callback.onChangeLifecycle(client);
		}
	});
}
