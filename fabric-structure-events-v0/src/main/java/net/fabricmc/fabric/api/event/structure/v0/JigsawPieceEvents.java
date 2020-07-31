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

import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public final class JigsawPieceEvents {
	private static final HashMultimap<Identifier, StructurePieceEvents.StructurePieceAdded> JIGSAW_PIECE_ADDED_EVENTS = HashMultimap.create();

	/**
	 * Registers a listener for a specific jigsaw structure piece
	 * @param jigsawPieceIdentifier the identifier of the jigsaw piece that was generated
	 * @param listener the listener itself
	 */
	public static void register(Identifier jigsawPieceIdentifier, StructurePieceEvents.StructurePieceAdded listener) {
		JIGSAW_PIECE_ADDED_EVENTS.put(jigsawPieceIdentifier, listener);
	}

	static {
		StructurePieceEvents.register(StructurePieceType.JIGSAW, ((piece, serverWorld) -> {
			StructurePoolElement element = ((PoolStructurePiece) piece).getPoolElement();

			if (element instanceof SinglePoolElement) {
				((SinglePoolElement) element).field_24015.ifLeft((identifier -> {
					for (StructurePieceEvents.StructurePieceAdded callback : JIGSAW_PIECE_ADDED_EVENTS.get(identifier)) {
						callback.onStructurePieceAdded(piece, serverWorld);
					}
				}));
			} else if (element instanceof FeaturePoolElement) {
				Identifier identifier = Registry.FEATURE.getId(((ConfiguredFeature<?, ?>) ((FeaturePoolElement) element).feature.get()).getFeature());

				for (StructurePieceEvents.StructurePieceAdded callback : JIGSAW_PIECE_ADDED_EVENTS.get(identifier)) {
					callback.onStructurePieceAdded(piece, serverWorld);
				}
			}
		}));
	}
}
