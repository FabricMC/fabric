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

package net.fabricmc.fabric.mixin.structure;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Provides access to the internal Generation Step hashmap.
 */
@Mixin(StructureFeature.class)
public interface StructureFeatureMixin {
	/**
	 * @return A static hashmap that controls which structure generate on which generation steps.
	 */
	@Accessor("STRUCTURE_TO_GENERATION_STEP")
	@Mutable
	static Map<StructureFeature<?>, GenerationStep.Feature> getGenerationStepMap() {
		return null;
	}
}
