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

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerEntityEvents {
	private ServerEntityEvents() {
	}

	/**
	 * Called when an Entity is loaded into a ServerWorld.
	 *
	 * <p>When this event is called, the entity is already in the world.
	 *
	 * <p>Note there is no corresponding unload event because entity unloads cannot be reliably tracked.
	 */
	public static final Event<ServerEntityEvents.Load> ENTITY_LOAD = EventFactory.createArrayBacked(ServerEntityEvents.Load.class, callbacks -> (entity, world) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricServerEntityLoad");

			for (ServerEntityEvents.Load callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onLoad(entity, world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ServerEntityEvents.Load callback : callbacks) {
				callback.onLoad(entity, world);
			}
		}
	});

	@FunctionalInterface
	public interface Load {
		void onLoad(Entity entity, ServerWorld world);
	}
}
