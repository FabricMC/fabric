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

import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.StructureFeature;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class StructureFeatureEvents {
	/**
	 * Called when a structure is added to the world, after the bounding box has been updated to reflect its children.
	 */
	public static final Event<StructureAdded> STRUCTURE_FEATURE_ADDED = EventFactory.createArrayBacked(StructureAdded.class, callbacks -> (structureStart, structureWorldAccess) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = structureWorldAccess.toServerWorld().getProfiler();
			profiler.push("fabricStructureAdded");

			for (StructureAdded callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onStructureAdded(structureStart, structureWorldAccess);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (StructureAdded callback : callbacks) {
				callback.onStructureAdded(structureStart, structureWorldAccess);
			}
		}
	});

	private static final HashMultimap<Identifier, StructureAdded> STRUCTURE_FEATURE_ADDED_EVENTS = HashMultimap.create();

	/**
	 * Registers a listener for a specific {@link StructureFeature}.
	 * @param structureFeature the feature to listen for
	 * @param listener the listener itself
	 */
	public static void register(StructureFeature<?> structureFeature, StructureAdded listener) {
		register(Registry.STRUCTURE_FEATURE.getId(structureFeature), listener);
	}

	/**
	 * Registers a listener for a specific {@link StructureFeature}, by {@link Identifier}.
	 * This method is useful for adding support for structures added by outside mods without needing a
	 * dependency on them, as the event will simply never be called if a matching structure doesn't exist.
	 * @param id the identifier of the structure feature to listen for
	 * @param listener the listener itself
	 */
	public static void register(Identifier id, StructureAdded listener) {
		STRUCTURE_FEATURE_ADDED_EVENTS.put(id, listener);
	}

	/**
	 * Registers a generic listener that gets called for every structure added to the world.
	 * @param listener the listener itself
	 */
	public static void register(StructureAdded listener) {
		STRUCTURE_FEATURE_ADDED.register(listener);
	}

	@FunctionalInterface
	public interface StructureAdded {
		/**
		 * Called prior to a structure feature being generated in the world.
		 * Note that the world itself is not available at this time, and is exposed for context purposes only.
		 * Attempting to modify the world directly will result in deadlocking the server.
		 * @param structureStart the start of the structure being generated
		 * @param structureWorldAccess the world that the structure will be added to
		 */
		void onStructureAdded(StructureStart<?> structureStart, StructureWorldAccess structureWorldAccess);
	}

	static {
		STRUCTURE_FEATURE_ADDED.register(((structureStart, world) -> {
			for (StructureAdded callback : STRUCTURE_FEATURE_ADDED_EVENTS.get(Registry.STRUCTURE_FEATURE.getId(structureStart.getFeature()))) {
				callback.onStructureAdded(structureStart, world);
			}
		}));
	}
}
