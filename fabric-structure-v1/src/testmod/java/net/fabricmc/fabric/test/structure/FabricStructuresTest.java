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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.structure.v1.FabricStructureHelper;
import net.fabricmc.fabric.test.structure.structure.TestStructure;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Test mod for the structure module
 */
public class FabricStructuresTest implements ModInitializer {

	public static final StructureFeature<DefaultFeatureConfig> TEST_STRUCTURE = FabricStructureHelper.register(new Identifier("fabric", "test_structure"), new TestStructure());

	@Override
	public void onInitialize() {
		Registry.BIOME.forEach((biome) -> biome.addStructureFeature(TEST_STRUCTURE.configure(new DefaultFeatureConfig())));
		RegistryEntryAddedCallback.event(Registry.BIOME).register((i, identifier, biome) -> biome.addStructureFeature(TEST_STRUCTURE.configure(new DefaultFeatureConfig())));
	}
}
