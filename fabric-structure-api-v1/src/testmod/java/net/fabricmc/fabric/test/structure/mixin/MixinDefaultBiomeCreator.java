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

package net.fabricmc.fabric.test.structure.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.biome.GenerationSettings;

import net.fabricmc.fabric.test.structure.StructureTest;

@Mixin(DefaultBiomeCreator.class)
public class MixinDefaultBiomeCreator {
	@ModifyVariable(method = "createPlains", ordinal = 0, at = @At(value = "STORE", ordinal = 0))
	private static GenerationSettings.Builder addCustomStructure(GenerationSettings.Builder builder, boolean sunflower) {
		if (!sunflower) {
			builder.structureFeature(StructureTest.CONFIGURED_STRUCTURE);
		}

		return builder;
	}
}
