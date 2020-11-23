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

package net.fabricmc.fabric.impl.biome.modification;

import java.util.Optional;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

@ApiStatus.Internal
public class BiomeSelectionContextImpl implements BiomeSelectionContext {
	private final RegistryKey<Biome> key;
	private final Biome biome;
	private final DynamicRegistryManager dynamicRegistries;

	public BiomeSelectionContextImpl(DynamicRegistryManager dynamicRegistries, RegistryKey<Biome> key, Biome biome) {
		this.key = key;
		this.biome = biome;
		this.dynamicRegistries = dynamicRegistries;
	}

	@Override
	public RegistryKey<Biome> getBiomeKey() {
		return key;
	}

	@Override
	public Biome getBiome() {
		return biome;
	}

	@Override
	public Optional<RegistryKey<ConfiguredSurfaceBuilder<?>>> getSurfaceBuilderKey() {
		Registry<ConfiguredSurfaceBuilder<?>> registry = dynamicRegistries.get(Registry.CONFIGURED_SURFACE_BUILDER_WORLDGEN);
		return registry.getKey(biome.getGenerationSettings().getSurfaceBuilder().get());
	}

	@Override
	public Optional<RegistryKey<ConfiguredFeature<?, ?>>> getFeatureKey(ConfiguredFeature<?, ?> configuredFeature) {
		Registry<ConfiguredFeature<?, ?>> registry = dynamicRegistries.get(Registry.CONFIGURED_FEATURE_WORLDGEN);
		return registry.getKey(configuredFeature);
	}

	@Override
	public Optional<RegistryKey<ConfiguredStructureFeature<?, ?>>> getStructureKey(ConfiguredStructureFeature<?, ?> configuredStructure) {
		Registry<ConfiguredStructureFeature<?, ?>> registry = dynamicRegistries.get(Registry.CONFIGURED_STRUCTURE_FEATURE_WORLDGEN);
		return registry.getKey(configuredStructure);
	}
}
