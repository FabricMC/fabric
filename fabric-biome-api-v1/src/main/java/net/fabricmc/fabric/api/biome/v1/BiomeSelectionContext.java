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

package net.fabricmc.fabric.api.biome.v1;

import java.util.List;
import java.util.Optional;

import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.StructureFeature;

import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;

/**
 * Context given to a biome selector for deciding whether it applies to a biome or not.
 */
public interface BiomeSelectionContext {
	RegistryKey<Biome> getBiomeKey();

	/**
	 * Returns the biome with modifications by biome modifiers of higher priority already applied.
	 */
	Biome getBiome();

	RegistryEntry<Biome> getBiomeRegistryEntry();

	/**
	 * Returns true if this biome has the given configured feature, which must be registered
	 * in the {@link net.minecraft.util.registry.BuiltinRegistries}.
	 *
	 * <p>This method is intended for use with the Vanilla configured features found in
	 * classes such as {@link net.minecraft.world.gen.feature.OreConfiguredFeatures}.
	 */
	default boolean hasBuiltInFeature(ConfiguredFeature<?, ?> configuredFeature) {
		RegistryKey<ConfiguredFeature<?, ?>> key = BuiltInRegistryKeys.get(configuredFeature);
		return hasFeature(key);
	}

	/**
	 * Returns true if this biome has the given placed feature, which must be registered
	 * in the {@link net.minecraft.util.registry.BuiltinRegistries}.
	 *
	 * <p>This method is intended for use with the Vanilla placed features found in
	 * classes such as {@link net.minecraft.world.gen.feature.OrePlacedFeatures}.
	 */
	default boolean hasBuiltInPlacedFeature(PlacedFeature placedFeature) {
		return hasPlacedFeature(BuiltInRegistryKeys.get(placedFeature));
	}

	/**
	 * Returns true if this biome contains a placed feature referencing a configured feature with the given key.
	 */
	default boolean hasFeature(RegistryKey<ConfiguredFeature<?, ?>> key) {
		List<RegistryEntryList<PlacedFeature>> featureSteps = getBiome().getGenerationSettings().getFeatures();

		for (RegistryEntryList<PlacedFeature> featureSuppliers : featureSteps) {
			for (RegistryEntry<PlacedFeature> featureSupplier : featureSuppliers) {
				if (featureSupplier.value().getDecoratedFeatures().anyMatch(cf -> getFeatureKey(cf).orElse(null) == key)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns true if this biome contains a placed feature with the given key.
	 */
	default boolean hasPlacedFeature(RegistryKey<PlacedFeature> key) {
		List<RegistryEntryList<PlacedFeature>> featureSteps = getBiome().getGenerationSettings().getFeatures();

		for (RegistryEntryList<PlacedFeature> featureSuppliers : featureSteps) {
			for (RegistryEntry<PlacedFeature> featureSupplier : featureSuppliers) {
				if (getPlacedFeatureKey(featureSupplier.value()).orElse(null) == key) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Tries to retrieve the registry key for the given configured feature, which should be from this biomes
	 * current feature list. May be empty if the configured feature is not registered, or does not come
	 * from this biomes feature list.
	 */
	Optional<RegistryKey<ConfiguredFeature<?, ?>>> getFeatureKey(ConfiguredFeature<?, ?> configuredFeature);

	/**
	 * Tries to retrieve the registry key for the given placed feature, which should be from this biomes
	 * current feature list. May be empty if the placed feature is not registered, or does not come
	 * from this biomes feature list.
	 */
	Optional<RegistryKey<PlacedFeature>> getPlacedFeatureKey(PlacedFeature placedFeature);

	/**
	 * Returns true if the given built-in configured structure from {@link net.minecraft.util.registry.BuiltinRegistries}
	 * can start in this biome in any of the chunk generators used by the current world-save.
	 *
	 * <p>This method is intended for use with the Vanilla configured structures found in {@link net.minecraft.world.gen.feature.ConfiguredStructureFeatures}.
	 */
	default boolean validForBuiltInStructure(StructureFeature structureFeature) {
		RegistryKey<StructureFeature> key = BuiltInRegistryKeys.get(structureFeature);
		return validForStructure(key);
	}

	/**
	 * Returns true if the configured structure with the given key can start in this biome in any chunk generator
	 * used by the current world-save.
	 */
	boolean validForStructure(RegistryKey<StructureFeature> key);

	/**
	 * Tries to retrieve the registry key for the given configured feature, which should be from this biomes
	 * current structure list. May be empty if the configured feature is not registered, or does not come
	 * from this biomes feature list.
	 */
	Optional<RegistryKey<StructureFeature>> getStructureKey(StructureFeature structureFeature);

	/**
	 * Tries to determine whether this biome generates in a specific dimension, based on the {@link net.minecraft.world.gen.GeneratorOptions}
	 * used by the current world-save.
	 *
	 * <p>If no dimension options exist for the given dimension key, <code>false</code> is returned.
	 */
	boolean canGenerateIn(RegistryKey<DimensionOptions> dimensionKey);

	/**
	 * {@return true if this biome is in the given {@link TagKey}}.
	 */
	boolean hasTag(TagKey<Biome> tag);
}
