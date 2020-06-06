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

@Environment(EnvType.CLIENT)
public final class ClientTickEvents {
	public ClientTickEvents() {
	}

	/**
	 * Called at the start of the client tick.
	 */
	public static final Event<ClientTickEvents.Client> START_CLIENT_TICK = EventFactory.createArrayBacked(ClientTickEvents.Client.class, callbacks -> client -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = client.getProfiler();
			profiler.push("fabricStartClientTick");

			for (ClientTickEvents.Client event : callbacks) {
				profiler.push(EventFactory.getHandlerName(event));
				event.onTick(client);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ClientTickEvents.Client event : callbacks) {
				event.onTick(client);
			}
		}
	});

	/**
	 * Called at the end of the client tick.
	 */
	public static final Event<ClientTickEvents.Client> END_CLIENT_TICK = EventFactory.createArrayBacked(ClientTickEvents.Client.class, callbacks -> client -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = client.getProfiler();
			profiler.push("fabricEndClientTick");

			for (ClientTickEvents.Client event : callbacks) {
				profiler.push(EventFactory.getHandlerName(event));
				event.onTick(client);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ClientTickEvents.Client event : callbacks) {
				event.onTick(client);
			}
		}
	});

	/**
	 * Called at the start of a ClientWorld's tick.
	 */
	public static final Event<ClientTickEvents.World> START_WORLD_TICK = EventFactory.createArrayBacked(ClientTickEvents.World.class, callbacks -> world -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricStartClientWorldTick");

			for (ClientTickEvents.World callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onTick(world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ClientTickEvents.World callback : callbacks) {
				callback.onTick(world);
			}
		}
	});

	/**
	 * Called at the end of a ClientWorld's tick.
	 *
	 * <p>End of world tick may be used to start async computations for the next tick.
	 */
	public static final Event<ClientTickEvents.World> END_WORLD_TICK = EventFactory.createArrayBacked(ClientTickEvents.World.class, callbacks -> world -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricEndClientWorldTick");

			for (ClientTickEvents.World callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onTick(world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ClientTickEvents.World callback : callbacks) {
				callback.onTick(world);
			}
		}
	});

	public interface Client {
		void onTick(MinecraftClient client);
	}

	public interface World {
		void onTick(ClientWorld world);
	}
}
