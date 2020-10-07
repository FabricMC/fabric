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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;

/**
 * Context given to a biome selector for deciding whether it applies to a biome or not.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 */
@Deprecated
public interface BiomeSelectionContext {
	RegistryKey<Biome> getBiomeKey();

	/**
	 * Returns the biome with modifications by biome modifiers of higher priority already applied.
	 */
	Biome getBiome();

	/**
	 * Returns true if this biome uses the given built-in surface builder, which must be registered
	 * in the {@link net.minecraft.util.registry.BuiltinRegistries}.
	 *
	 * <p>This method is intended for use with the Vanilla surface builders found in {@link ConfiguredSurfaceBuilders}.
	 */
	default boolean hasBuiltInSurfaceBuilder(ConfiguredSurfaceBuilder<?> surfaceBuilder) {
		RegistryKey<ConfiguredSurfaceBuilder<?>> key = BuiltInRegistryKeys.get(surfaceBuilder);
		return hasSurfaceBuilder(key);
	}

	/**
	 * Returns true if this biome uses a surface builder that has the given key.
	 */
	default boolean hasSurfaceBuilder(RegistryKey<ConfiguredSurfaceBuilder<?>> key) {
		return getSurfaceBuilderKey().orElse(null) == key;
	}

	/**
	 * Tries to retrieve the registry key for this biomes current surface builder, which may be empty, if the
	 * surface builder is not registered.
	 */
	Optional<RegistryKey<ConfiguredSurfaceBuilder<?>>> getSurfaceBuilderKey();

	/**
	 * Returns true if this biome has the given configured feature, which must be registered
	 * in the {@link net.minecraft.util.registry.BuiltinRegistries}.
	 *
	 * <p>This method is intended for use with the Vanilla configured features found in {@link net.minecraft.world.gen.feature.ConfiguredFeatures}.
	 */
	default boolean hasBuiltInFeature(ConfiguredFeature<?, ?> configuredFeature) {
		RegistryKey<ConfiguredFeature<?, ?>> key = BuiltInRegistryKeys.get(configuredFeature);
		return hasFeature(key);
	}

	/**
	 * Returns true if this biome contains a configured feature with the given key.
	 */
	default boolean hasFeature(RegistryKey<ConfiguredFeature<?, ?>> key) {
		List<List<Supplier<ConfiguredFeature<?, ?>>>> featureSteps = getBiome().getGenerationSettings().getFeatures();

		for (List<Supplier<ConfiguredFeature<?, ?>>> featureSuppliers : featureSteps) {
			for (Supplier<ConfiguredFeature<?, ?>> featureSupplier : featureSuppliers) {
				if (getFeatureKey(featureSupplier.get()).orElse(null) == key) {
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
	 * Returns true if the configured structure with the given key can start in this biome.
	 */
	default boolean hasStructure(RegistryKey<ConfiguredStructureFeature<?, ?>> key) {
		Collection<Supplier<ConfiguredStructureFeature<?, ?>>> structureFeatures = getBiome().getGenerationSettings().getStructureFeatures();

		for (Supplier<ConfiguredStructureFeature<?, ?>> supplier : structureFeatures) {
			if (getStructureKey(supplier.get()).orElse(null) == key) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Tries to retrieve the registry key for the given configured feature, which should be from this biomes
	 * current feature list. May be empty if the configured feature is not registered, or does not come
	 * from this biomes feature list.
	 */
	Optional<RegistryKey<ConfiguredStructureFeature<?, ?>>> getStructureKey(ConfiguredStructureFeature<?, ?> configuredStructure);
}
