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
import java.util.function.Supplier;

import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;

/**
 * Context given to a biome selector for deciding whether it applies to a biome or not.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 */
public interface BiomeSelectionContext {
	RegistryKey<Biome> getBiomeKey();

	/**
	 * Returns the biome with modifications by biome modifiers of higher priority already applied.
	 */
	Biome getBiome();

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
		List<List<Supplier<PlacedFeature>>> featureSteps = getBiome().getGenerationSettings().getFeatures();

		for (List<Supplier<PlacedFeature>> featureSuppliers : featureSteps) {
			for (Supplier<PlacedFeature> featureSupplier : featureSuppliers) {
				if (featureSupplier.get().getDecoratedFeatures().anyMatch(cf -> getFeatureKey(cf).orElse(null) == key)) {
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
		List<List<Supplier<PlacedFeature>>> featureSteps = getBiome().getGenerationSettings().getFeatures();

		for (List<Supplier<PlacedFeature>> featureSuppliers : featureSteps) {
			for (Supplier<PlacedFeature> featureSupplier : featureSuppliers) {
				if (getPlacedFeatureKey(featureSupplier.get()).orElse(null) == key) {
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
	 * can start in this biome.
	 *
	 * <p>This method is intended for use with the Vanilla configured structures found in {@link net.minecraft.world.gen.feature.ConfiguredStructureFeatures}.
	 */
	default boolean hasBuiltInStructure(ConfiguredStructureFeature<?, ?> configuredStructure) {
		RegistryKey<ConfiguredStructureFeature<?, ?>> key = BuiltInRegistryKeys.get(configuredStructure);
		return hasStructure(key);
	}

	/**
	 * Returns true if the configured structure with the given key can start in this biome in any configured
	 * chunk generator.
	 */
	boolean hasStructure(RegistryKey<ConfiguredStructureFeature<?, ?>> key);

	/**
	 * Tries to retrieve the registry key for the given configured feature, which should be from this biomes
	 * current structure list. May be empty if the configured feature is not registered, or does not come
	 * from this biomes feature list.
	 */
	Optional<RegistryKey<ConfiguredStructureFeature<?, ?>>> getStructureKey(ConfiguredStructureFeature<?, ?> configuredStructure);
}
