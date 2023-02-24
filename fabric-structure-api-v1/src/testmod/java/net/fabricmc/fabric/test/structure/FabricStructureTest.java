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

package net.fabricmc.fabric.test.structure;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

public class FabricStructureTest implements ModInitializer {
	public static final StructureProcessorType<TestStructureProcessor> TEST_STRUCTURE_PROCESSOR_TYPE = Registry.register(
			Registries.STRUCTURE_PROCESSOR,
			new Identifier("fabric-structure-api-v1-testmod", "test_processor"),
			() -> TestStructureProcessor.CODEC
	);

	@Override
	public void onInitialize() {
		// See biome-api testmod for a full functional test.
	}
}
