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

import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.level.LevelProperties;

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;

@ApiStatus.Internal
public class BiomeSelectionContextImpl implements BiomeSelectionContext {
	private final DynamicRegistryManager dynamicRegistries;
	private final LevelProperties levelProperties;
	private final RegistryKey<Biome> key;
	private final Biome biome;
	private final RegistryEntry<Biome> entry;

	public BiomeSelectionContextImpl(DynamicRegistryManager dynamicRegistries, LevelProperties levelProperties, RegistryKey<Biome> key, Biome biome) {
		this.dynamicRegistries = dynamicRegistries;
		this.levelProperties = levelProperties;
		this.key = key;
		this.biome = biome;
		this.entry = dynamicRegistries.get(Registry.BIOME_KEY).getEntry(this.key).orElseThrow();
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
		Registry<ConfiguredFeature<?, ?>> registry = dynamicRegistries.get(Registry.CONFIGURED_FEATURE_KEY);
		return registry.getKey(configuredFeature);
	}

	@Override
	public Optional<RegistryKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature) {
		Registry<PlacedFeature> registry = dynamicRegistries.get(Registry.PLACED_FEATURE_KEY);
		return registry.getKey(placedFeature);
	}

	@Override
	public boolean validForStructure(RegistryKey<StructureFeature> key) {
		StructureFeature instance = dynamicRegistries.get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY).get(key);

		if (instance == null) {
			return false;
		}

		return instance.method_41607().contains(getBiomeRegistryEntry());
	}

	@Override
	public Optional<RegistryKey<StructureFeature>> getStructureKey(StructureFeature structureFeature) {
		Registry<StructureFeature> registry = dynamicRegistries.get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
		return registry.getKey(structureFeature);
	}

	@Override
	public boolean canGenerateIn(RegistryKey<DimensionOptions> dimensionKey) {
		DimensionOptions dimension = levelProperties.getGeneratorOptions().getDimensions().get(dimensionKey);

		if (dimension == null) {
			return false;
		}

		return dimension.getChunkGenerator().getBiomeSource().getBiomes().stream().anyMatch(entry -> entry.value() == biome);
	}

	@Override
	public boolean hasTag(TagKey<Biome> tag) {
		Registry<Biome> biomeRegistry = dynamicRegistries.get(Registry.BIOME_KEY);
		return biomeRegistry.entryOf(getBiomeKey()).isIn(tag);
	}
}
