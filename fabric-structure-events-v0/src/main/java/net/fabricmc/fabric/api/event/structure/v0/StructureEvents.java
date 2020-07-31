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

package net.fabricmc.fabric.api.event.structure.v0;

import com.google.common.collect.HashMultimap;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.gen.feature.StructureFeature;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class StructureEvents {
	/**
	 * Called when a structure is added to the world, after the bounding box has been updated to reflect its children.
	 */
	public static final Event<StructureAdded> STRUCTURE_ADDED = EventFactory.createArrayBacked(StructureAdded.class, callbacks -> (structureStart, serverWorld) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = serverWorld.getProfiler();
			profiler.push("fabricStructureAdded");

			for (StructureAdded callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onStructureAdded(structureStart, serverWorld);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (StructureAdded callback : callbacks) {
				callback.onStructureAdded(structureStart, serverWorld);
			}
		}
	});

	private static final HashMultimap<StructureFeature<?>, StructureAdded> STRUCTURE_ADDED_EVENTS = HashMultimap.create();

	/**
	 * Registers a listener for a specific {@link StructureFeature}
	 * @param structureFeature the feature to listen for
	 * @param listener the listener itself
	 */
	public static void register(StructureFeature<?> structureFeature, StructureAdded listener) {
		STRUCTURE_ADDED_EVENTS.put(structureFeature, listener);
	}

	/**
	 * Registers a generic listener that gets called for every structure added to the world.
	 * @param listener the listener itself
	 */
	public static void register(StructureAdded listener) {
		STRUCTURE_ADDED.register(listener);
	}

	@FunctionalInterface
	public interface StructureAdded {
		void onStructureAdded(StructureStart<?> structureStart, ServerWorld serverWorld);
	}

	static {
		STRUCTURE_ADDED.register(((structureStart, world) -> {
			for (StructureAdded callback : STRUCTURE_ADDED_EVENTS.get(structureStart.getFeature())) {
				callback.onStructureAdded(structureStart, world);
			}
		}));
	}
}
