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

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.structure.Structure;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

public class BiomeSelectionContextImpl implements BiomeSelectionContext {
	private final DynamicRegistryManager dynamicRegistries;
	private final RegistryKey<Biome> key;
	private final Biome biome;
	private final RegistryEntry<Biome> entry;

	public BiomeSelectionContextImpl(DynamicRegistryManager dynamicRegistries, RegistryKey<Biome> key, Biome biome) {
		this.dynamicRegistries = dynamicRegistries;
		this.key = key;
		this.biome = biome;
		this.entry = dynamicRegistries.getOrThrow(RegistryKeys.BIOME).getOrThrow(this.key);
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
	public RegistryEntry<Biome> getBiomeRegistryEntry() {
		return entry;
	}

	@Override
	public Optional<RegistryKey<ConfiguredFeature<?, ?>>> getFeatureKey(ConfiguredFeature<?, ?> configuredFeature) {
		Registry<ConfiguredFeature<?, ?>> registry = dynamicRegistries.getOrThrow(RegistryKeys.CONFIGURED_FEATURE);
		return registry.getKey(configuredFeature);
	}

	@Override
	public Optional<RegistryKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature) {
		Registry<PlacedFeature> registry = dynamicRegistries.getOrThrow(RegistryKeys.PLACED_FEATURE);
		return registry.getKey(placedFeature);
	}

	@Override
	public boolean validForStructure(RegistryKey<Structure> key) {
		Structure instance = dynamicRegistries.getOrThrow(RegistryKeys.STRUCTURE).get(key);

		if (instance == null) {
			return false;
		}

		return instance.getValidBiomes().contains(getBiomeRegistryEntry());
	}

	@Override
	public Optional<RegistryKey<Structure>> getStructureKey(Structure structure) {
		Registry<Structure> registry = dynamicRegistries.getOrThrow(RegistryKeys.STRUCTURE);
		return registry.getKey(structure);
	}

	@Override
	public boolean canGenerateIn(RegistryKey<DimensionOptions> dimensionKey) {
		DimensionOptions dimension = dynamicRegistries.getOrThrow(RegistryKeys.DIMENSION).get(dimensionKey);

		if (dimension == null) {
			return false;
		}

		return dimension.chunkGenerator().getBiomeSource().getBiomes().stream().anyMatch(entry -> entry.value() == biome);
	}

	@Override
	public boolean hasTag(TagKey<Biome> tag) {
		Registry<Biome> biomeRegistry = dynamicRegistries.getOrThrow(RegistryKeys.BIOME);
		return biomeRegistry.getOrThrow(getBiomeKey()).isIn(tag);
	}
}
