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

package net.fabricmc.fabric.mixin.biome.modification;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;

@Mixin(GenerationSettings.class)
public interface GenerationSettingsAccessor {
	@Accessor("surfaceBuilder")
	Supplier<ConfiguredSurfaceBuilder<?>> fabric_getSurfaceBuilder();

	@Accessor("carvers")
	Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> fabric_getCarvers();

	@Accessor("features")
	List<List<Supplier<ConfiguredFeature<?, ?>>>> fabric_getFeatures();

	@Accessor("structureFeatures")
	List<Supplier<ConfiguredStructureFeature<?, ?>>> fabric_getStructureFeatures();

	@Accessor("flowerFeatures")
	List<ConfiguredFeature<?, ?>> fabric_getFlowerFeatures();

	@Accessor("surfaceBuilder")
	@Mutable
	void fabric_setSurfaceBuilder(Supplier<ConfiguredSurfaceBuilder<?>> value);

	@Accessor("carvers")
	@Mutable
	void fabric_setCarvers(Map<GenerationStep.Carver, List<Supplier<ConfiguredCarver<?>>>> value);

	@Accessor("features")
	@Mutable
	void fabric_setFeatures(List<List<Supplier<ConfiguredFeature<?, ?>>>> value);

	@Accessor("structureFeatures")
	@Mutable
	void fabric_setStructureFeatures(List<Supplier<ConfiguredStructureFeature<?, ?>>> value);

	@Accessor("flowerFeatures")
	@Mutable
	void fabric_setFlowerFeatures(List<ConfiguredFeature<?, ?>> value);
}
