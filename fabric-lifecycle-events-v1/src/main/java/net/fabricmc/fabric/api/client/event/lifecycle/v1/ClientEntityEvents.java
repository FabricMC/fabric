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

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public final class ClientEntityEvents {
	public ClientEntityEvents() {
	}

	/**
	 * Called when an Entity is loaded into a ClientWorld.
	 *
	 * <p>When this event is called, the chunk is already in the world.
	 */
	public static final Event<ClientEntityEvents.Load> ENTITY_LOAD = EventFactory.createArrayBacked(ClientEntityEvents.Load.class, callbacks -> (entity, world) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricClientEntityLoad");

			for (ClientEntityEvents.Load callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onLoad(entity, world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ClientEntityEvents.Load callback : callbacks) {
				callback.onLoad(entity, world);
			}
		}
	});

	/**
	 * Called when an Entity is about to be unloaded from a ClientWorld.
	 *
	 * <p>When this event is called, the entity is still present in the world.
	 */
	public static final Event<ClientEntityEvents.Unload> ENTITY_UNLOAD = EventFactory.createArrayBacked(ClientEntityEvents.Unload.class, callbacks -> (entity, world) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = world.getProfiler();
			profiler.push("fabricClientEntityLoad");

			for (ClientEntityEvents.Unload callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onUnload(entity, world);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ClientEntityEvents.Unload callback : callbacks) {
				callback.onUnload(entity, world);
			}
		}
	});

	@FunctionalInterface
	public interface Load {
		void onLoad(Entity entity, ClientWorld world);
	}

	@FunctionalInterface
	public interface Unload {
		void onUnload(Entity entity, ClientWorld world);
	}
}
