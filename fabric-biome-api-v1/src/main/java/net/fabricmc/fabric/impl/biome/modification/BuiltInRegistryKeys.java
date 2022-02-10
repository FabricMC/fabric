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

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.PlacedFeature;

/**
 * Utility class for getting the registry keys of built-in worldgen objects and throwing proper exceptions if they
 * are not registered.
 */
@ApiStatus.Internal
public final class BuiltInRegistryKeys {
	private BuiltInRegistryKeys() {
	}

	public static RegistryKey<ConfiguredStructureFeature<?, ?>> getKey(ConfiguredStructureFeature<?, ?> configuredStructure) {
		return BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getKey(configuredStructure)
				.orElseThrow(() -> new IllegalArgumentException("Given configured structure is not built-in: " + configuredStructure));
	}

	public static RegistryKey<ConfiguredFeature<?, ?>> getKey(ConfiguredFeature<?, ?> configuredFeature) {
		return BuiltinRegistries.CONFIGURED_FEATURE.getKey(configuredFeature)
				.orElseThrow(() -> new IllegalArgumentException("Given configured feature is not built-in: " + configuredFeature));
	}

	public static RegistryKey<PlacedFeature> getKey(PlacedFeature placedFeature) {
		return BuiltinRegistries.PLACED_FEATURE.getKey(placedFeature)
				.orElseThrow(() -> new IllegalArgumentException("Given placed feature is not built-in: " + placedFeature));
	}

	public static RegistryKey<ConfiguredCarver<?>> getKey(ConfiguredCarver<?> configuredCarver) {
		return BuiltinRegistries.CONFIGURED_CARVER.getKey(configuredCarver)
				.orElseThrow(() -> new IllegalArgumentException("Given configured carver is not built-in: " + configuredCarver));
	}

	public static RegistryEntry<ConfiguredStructureFeature<?, ?>> getEntry(ConfiguredStructureFeature<?, ?> configuredStructure) {
		return BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getOrCreateEntry(getKey(configuredStructure));
	}

	public static RegistryEntry<ConfiguredFeature<?, ?>> getEntry(ConfiguredFeature<?, ?> configuredFeature) {
		return BuiltinRegistries.CONFIGURED_FEATURE.getOrCreateEntry(getKey(configuredFeature));
	}

	public static RegistryEntry<PlacedFeature> getEntry(PlacedFeature placedFeature) {
		return BuiltinRegistries.PLACED_FEATURE.getOrCreateEntry(getKey(placedFeature));
	}

	public static RegistryEntry<ConfiguredCarver<?>> getEntry(ConfiguredCarver<?> configuredCarver) {
		return BuiltinRegistries.CONFIGURED_CARVER.getOrCreateEntry(getKey(configuredCarver));
	}
}
