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

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;

@Mixin(StructureFeature.class)
public interface StructureFeatureAccessor {
	@Accessor("JIGSAW_STRUCTURES")
	@Mutable
	static void setSurfaceAdjustingStructures(List<StructureFeature<?>> surfaceAdjustingStructures) {
		throw new AssertionError("Untransformed accessor");
	}

	@Invoker
	static <F extends StructureFeature<?>> F callRegister(String name, F structureFeature, GenerationStep.Feature step) {
		throw new AssertionError("Untransformed accessor");
	}
}
