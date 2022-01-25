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

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

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
	public boolean hasStructure(RegistryKey<ConfiguredStructureFeature<?, ?>> key) {
		ConfiguredStructureFeature<?, ?> instance = dynamicRegistries.get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY).get(key);

		if (instance == null) {
			return false;
		}

		// Since the biome->structure mapping is now stored in the chunk generator configurations, it's no longer
		// trivial to detect if a given biome _could_ spawn a structure. To still support the API, we now do this on a
		// per-chunk-generator level.
		Registry<ChunkGeneratorSettings> chunkGeneratorSettings = dynamicRegistries.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);

		for (Map.Entry<RegistryKey<ChunkGeneratorSettings>, ChunkGeneratorSettings> entry : chunkGeneratorSettings.getEntries()) {
			StructuresConfig structuresConfig = entry.getValue().getStructuresConfig();

			if (structuresConfig.getConfiguredStructureFeature(instance.feature).get(instance).contains(getBiomeKey())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Optional<RegistryKey<ConfiguredStructureFeature<?, ?>>> getStructureKey(ConfiguredStructureFeature<?, ?> configuredStructure) {
		Registry<ConfiguredStructureFeature<?, ?>> registry = dynamicRegistries.get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);
		return registry.getKey(configuredStructure);
	}
}
