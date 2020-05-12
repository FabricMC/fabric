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
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.block.entity.BlockEntityLoadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.block.entity.BlockEntityUnloadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.chunk.ChunkLoadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.chunk.ChunkUnloadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.entity.EntityLoadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.entity.EntityUnloadCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.world.WorldTickCallback;

public final class ServerLifecycleEvents {
	private ServerLifecycleEvents() {
	}

	public static final Event<GameTickCallback<MinecraftServer>> SERVER_TICK = EventFactory.createArrayBacked(GameTickCallback.class, callbacks -> server -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = server.getProfiler();
			profiler.push("fabricServerTick");

			for (GameTickCallback<MinecraftServer> event : callbacks) {
				profiler.push(EventFactory.getHandlerName(event));
				event.onTick(server);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (GameTickCallback<MinecraftServer> event : callbacks) {
				event.onTick(server);
			}
		}
	});

	public static final Event<WorldTickCallback<ServerWorld>> WORLD_TICK = EventFactory.createArrayBacked(WorldTickCallback.class, callbacks -> world -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricServerWorldTick_" + world.dimension.getType().toString());

			for (WorldTickCallback<ServerWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onTick(world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (WorldTickCallback<ServerWorld> callback : callbacks) {
				callback.onTick(world);
			}
		}
	});

	public static final Event<ChunkLoadCallback<ServerWorld>> CHUNK_LOAD = EventFactory.createArrayBacked(ChunkLoadCallback.class, callbacks -> (serverWorld, chunk) -> {
		if (EventFactory.isProfilingEnabled()) {
			Profiler profiler = serverWorld.getProfiler();
			profiler.push("fabricServerChunkLoad");

			for (ChunkLoadCallback<ServerWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onChunkLoad(serverWorld, chunk);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ChunkLoadCallback<ServerWorld> callback : callbacks) {
				callback.onChunkLoad(serverWorld, chunk);
			}
		}
	});

	public static final Event<ChunkUnloadCallback<ServerWorld>> CHUNK_UNLOAD = EventFactory.createArrayBacked(ChunkUnloadCallback.class, callbacks -> (serverWorld, chunk) -> {
		if (EventFactory.isProfilingEnabled()) {
			Profiler profiler = serverWorld.getProfiler();
			profiler.push("fabricServerChunkUnload");

			for (ChunkUnloadCallback<ServerWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onChunkUnload(serverWorld, chunk);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ChunkUnloadCallback<ServerWorld> callback : callbacks) {
				callback.onChunkUnload(serverWorld, chunk);
			}
		}
	});

	public static final Event<BlockEntityLoadCallback<ServerWorld>> BLOCK_ENTITY_LOAD = EventFactory.createArrayBacked(BlockEntityLoadCallback.class, callbacks -> (blockEntity, world) -> {
		final Profiler profiler = world.getProfiler();

		if (EventFactory.isProfilingEnabled()) {
			profiler.push("fabricServerBlockEntityLoad");

			for (BlockEntityLoadCallback<ServerWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onLoadBlockEntity(blockEntity, world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (BlockEntityLoadCallback<ServerWorld> callback : callbacks) {
				callback.onLoadBlockEntity(blockEntity, world);
			}
		}
	});

	public static final Event<BlockEntityUnloadCallback<ServerWorld>> BLOCK_ENTITY_UNLOAD = EventFactory.createArrayBacked(BlockEntityUnloadCallback.class, callbacks -> (blockEntity, world) -> {
		final Profiler profiler = world.getProfiler();

		if (EventFactory.isProfilingEnabled()) {
			profiler.push("fabricServerBlockEntityUnload");

			for (BlockEntityUnloadCallback<ServerWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onUnloadBlockEntity(blockEntity, world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (BlockEntityUnloadCallback<ServerWorld> callback : callbacks) {
				callback.onUnloadBlockEntity(blockEntity, world);
			}
		}
	});

	public static final Event<EntityLoadCallback<ServerWorld>> ENTITY_LOAD = EventFactory.createArrayBacked(EntityLoadCallback.class, callbacks -> (entity, world) -> {
		final Profiler profiler = world.getProfiler();

		if (EventFactory.isProfilingEnabled()) {
			profiler.push("fabricServerEntityLoad");

			for (EntityLoadCallback<ServerWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onEntityLoad(entity, world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (EntityLoadCallback<ServerWorld> callback : callbacks) {
				callback.onEntityLoad(entity, world);
			}
		}
	});

	public static final Event<EntityUnloadCallback<ServerWorld>> ENTITY_UNLOAD = EventFactory.createArrayBacked(EntityUnloadCallback.class, callbacks -> (entity, world) -> {
		final Profiler profiler = world.getProfiler();

		if (EventFactory.isProfilingEnabled()) {
			profiler.push("fabricServerEntityUnload");

			for (EntityUnloadCallback<ServerWorld> callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onEntityUnload(entity, world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (EntityUnloadCallback<ServerWorld> callback : callbacks) {
				callback.onEntityUnload(entity, world);
			}
		}
	});

	public static final Event<ServerLifecycleCallback> SERVER_START = EventFactory.createArrayBacked(ServerLifecycleCallback.class, (callbacks) -> (server) -> {
		for (ServerLifecycleCallback callback : callbacks) {
			callback.onChangeLifecycle(server);
		}
	});

	public static final Event<ServerLifecycleCallback> SERVER_STOP = EventFactory.createArrayBacked(ServerLifecycleCallback.class, (callbacks) -> (server) -> {
		for (ServerLifecycleCallback callback : callbacks) {
			callback.onChangeLifecycle(server);
		}
	});
}
