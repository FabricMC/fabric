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
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class StructurePieceEvents {
	/**
	 * Called when a structure piece is generated in the world.
	 */
	public static final Event<StructurePieceAdded> PIECE_ADDED = EventFactory.createArrayBacked(StructurePieceAdded.class, callbacks -> (piece, serverWorld) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = serverWorld.getProfiler();
			profiler.push("fabricStructurePieceAdded");

			for (StructurePieceAdded callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onStructurePieceAdded(piece, serverWorld);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (StructurePieceAdded callback : callbacks) {
				callback.onStructurePieceAdded(piece, serverWorld);
			}
		}
	});
	
	private static final HashMultimap<Identifier, StructurePieceAdded> STRUCTURE_PIECE_ADDED_EVENTS = HashMultimap.create();

	/**
	 * Registers a listener for a specific {@link StructurePieceType}
	 * @param structurePieceType the type of the structure to listen for
	 * @param listener the listener itself
	 */
	public static void register(StructurePieceType structurePieceType, StructurePieceAdded listener) {
		register(Registry.STRUCTURE_PIECE.getId(structurePieceType), listener);
	}

	/**
	 * Registers a listener for a specific {@link StructurePieceType}, by {@link Identifier}.
	 * This method is useful for adding support for structures added by outside mods without needing a
	 * dependency on them, as the event will simply never be called if a matching structure doesn't exist.
	 * @param id the identifier of the structure piece to listen for
	 * @param listener the listener itself
	 */
	public static void register(Identifier id, StructurePieceAdded listener) {
		STRUCTURE_PIECE_ADDED_EVENTS.put(id, listener);
	}

	/**
	 * Registers a generic listener that gets called for every structure piece that gets added to the world.
	 * @param listener the listener itself
	 */
	public static void register(StructurePieceAdded listener) {
		PIECE_ADDED.register(listener);
	}

	@FunctionalInterface
	public interface StructurePieceAdded {
		void onStructurePieceAdded(StructurePiece piece, ServerWorld serverWorld);
	}

	static {
		PIECE_ADDED.register(((piece, world) -> {
			for (StructurePieceAdded callback : STRUCTURE_PIECE_ADDED_EVENTS.get(Registry.STRUCTURE_PIECE.getId(piece.getType()))) {
				callback.onStructurePieceAdded(piece, world);
			}
		}));
	}
}
