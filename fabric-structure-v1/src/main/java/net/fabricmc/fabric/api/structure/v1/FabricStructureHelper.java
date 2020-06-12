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

package net.fabricmc.fabric.api.structure.v1;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import net.fabricmc.fabric.mixin.structure.StructureFeatureMixin;

/**
 * API that hooks into the internal structure generation step logic.
 */
public final class FabricStructureHelper {
	private FabricStructureHelper() { }

	/**
	 * Adds a structure to the given generation step.
	 *
	 * @param structure The structure to add to the step map. Must not be null
	 */
	public static void setGenerationStep(FabricStructure structure) {
		// Put if absent was used to avoid vanilla overrides
		StructureFeatureMixin.getGenerationStepMap().putIfAbsent(structure, structure.method_28663());
	}

	/**
	 * Registers a structure in the structure registry, the serialization hashmap, and generation step hashmap.
	 *
	 * @param id        The identifier under which to register the structure universally
	 * @param structure The structure to register under the given identifier
	 * @return
	 */
	public static <T extends FeatureConfig> StructureFeature<T> register(Identifier id, FabricStructure<T> structure) {
		StructureFeature.STRUCTURES.put(structure.getName(), structure);
		setGenerationStep(structure);
		return Registry.register(Registry.STRUCTURE_FEATURE, id, structure);
	}
}
