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

public final class ServerTickEvents {
	private ServerTickEvents() {
	}

	/**
	 * Called at the start of the server tick.
	 */
	public static final Event<ServerTickEvents.Server> START_SERVER_TICK = EventFactory.createArrayBacked(ServerTickEvents.Server.class, callbacks -> server -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = server.getProfiler();
			profiler.push("fabricStartServerTick");

			for (ServerTickEvents.Server event : callbacks) {
				profiler.push(EventFactory.getHandlerName(event));
				event.onTick(server);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ServerTickEvents.Server event : callbacks) {
				event.onTick(server);
			}
		}
	});

	/**
	 * Called at the end of the server tick.
	 */
	public static final Event<ServerTickEvents.Server> END_SERVER_TICK = EventFactory.createArrayBacked(ServerTickEvents.Server.class, callbacks -> server -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = server.getProfiler();
			profiler.push("fabricEndServerTick");

			for (ServerTickEvents.Server event : callbacks) {
				profiler.push(EventFactory.getHandlerName(event));
				event.onTick(server);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ServerTickEvents.Server event : callbacks) {
				event.onTick(server);
			}
		}
	});

	/**
	 * Called at the start of a ServerWorld's tick.
	 */
	public static final Event<ServerTickEvents.World> START_WORLD_TICK = EventFactory.createArrayBacked(ServerTickEvents.World.class, callbacks -> world -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricStartServerWorldTick_" + world.dimension.getType().toString());

			for (ServerTickEvents.World callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onTick(world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ServerTickEvents.World callback : callbacks) {
				callback.onTick(world);
			}
		}
	});

	/**
	 * Called at the end of a ServerWorld's tick.
	 *
	 * <p>End of world tick may be used to start async computations for the next tick.
	 */
	public static final Event<ServerTickEvents.World> END_WORLD_TICK = EventFactory.createArrayBacked(ServerTickEvents.World.class, callbacks -> world -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricEndServerWorldTick_" + world.dimension.getType().toString());

			for (ServerTickEvents.World callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onTick(world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ServerTickEvents.World callback : callbacks) {
				callback.onTick(world);
			}
		}
	});

	public interface Server {
		void onTick(MinecraftServer server);
	}

	public interface World {
		void onTick(ServerWorld world);
	}
}
