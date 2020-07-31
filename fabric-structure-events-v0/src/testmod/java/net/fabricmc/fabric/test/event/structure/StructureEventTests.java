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
import net.fabricmc.fabric.api.event.structure.v0.JigsawPieceEvents;
import net.fabricmc.fabric.api.event.structure.v0.StructureEvents;
import net.fabricmc.fabric.api.event.structure.v0.StructurePieceEvents;

public class StructureEventTests implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("StructureEventsTest");

	@Override
	public void onInitialize() {
		StructureEvents.register((structureStart, serverWorld) -> LOGGER.info("Structure {} added to {}", structureStart.getFeature().getName(), serverWorld.getRegistryKey().getValue()));
		StructurePieceEvents.register(StructurePieceType.JIGSAW, ((piece, serverWorld) -> LOGGER.info("Placing Jigsaw structure piece {} in {}", piece.toString(), serverWorld.getRegistryKey().getValue())));
		JigsawPieceEvents.register(new Identifier("minecraft:village/plains/terminators/terminator_01"), ((piece, serverWorld) -> LOGGER.info("Placing minecraft:village/plains/terminators/terminator_01")));
	}
}
