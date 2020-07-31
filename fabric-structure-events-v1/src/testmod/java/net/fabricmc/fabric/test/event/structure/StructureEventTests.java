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

package net.fabricmc.fabric.test.event.structure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.structure.v1.JigsawPieceEvents;
import net.fabricmc.fabric.api.event.structure.v1.StructureFeatureEvents;
import net.fabricmc.fabric.api.event.structure.v1.StructurePieceEvents;

public class StructureEventTests implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("StructureEventsTest");

	@Override
	public void onInitialize() {
		StructureFeatureEvents.register((structureStart, structureWorldAccess) -> LOGGER.info("Structure {} added to {}", structureStart.getFeature().getName(), structureWorldAccess.toServerWorld().getRegistryKey().getValue()));
		StructureFeatureEvents.register(new Identifier("fabric", "structure_feature"), ((structureStart, structureWorldAccess) -> LOGGER.info("This should never be called.")));

		StructurePieceEvents.register(StructurePieceType.JIGSAW, ((piece, structureWorldAccess) -> LOGGER.info("Placing Jigsaw structure piece {} in {}", piece.toString(), structureWorldAccess.toServerWorld().getRegistryKey().getValue())));
		StructurePieceEvents.register(new Identifier("fabric", "structure_feature_piece"), ((piece, structureWorldAccess) -> LOGGER.info("This should never be called.")));

		JigsawPieceEvents.register(new Identifier("village/plains/terminators/terminator_01"), ((piece, structureWorldAccess) -> LOGGER.info("Placing minecraft:village/plains/terminators/terminator_01")));
	}
}
