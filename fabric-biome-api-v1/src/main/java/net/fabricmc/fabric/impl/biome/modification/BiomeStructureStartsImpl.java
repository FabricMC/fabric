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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * Helps with modifying the structure start information stored in {@link ChunkGeneratorSettings}.
 */
@ApiStatus.Internal
public final class BiomeStructureStartsImpl {
	private BiomeStructureStartsImpl() {
	}

	public static void addStart(DynamicRegistryManager registries,
								ConfiguredStructureFeature<?, ?> configuredStructure,
								RegistryKey<Biome> biome) {
		changeStructureStarts(registries, structureMap -> {
			Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> configuredMap = structureMap.computeIfAbsent(configuredStructure.feature, k -> HashMultimap.create());

			// This is tricky, the keys might be either from builtin (Vanilla) or dynamic registries (modded)
			RegistryKey<ConfiguredStructureFeature<?, ?>> configuredStructureKey = registries.get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY).getKey(configuredStructure).orElseThrow();
			ConfiguredStructureFeature<?, ?> mapKey = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.get(configuredStructureKey);
			if (mapKey == null) {
				// This means the configured structure passed in is not a (potentially modified) Vanilla entry,
				// but rather a modded one. In this case, this will create a new entry in the map.
				mapKey = configuredStructure;
			}

			configuredMap.put(mapKey, biome);
		});
	}

	public static boolean removeStart(DynamicRegistryManager registries,
									  ConfiguredStructureFeature<?, ?> configuredStructure,
									  RegistryKey<Biome> biome) {
		AtomicBoolean result = new AtomicBoolean(false);

		changeStructureStarts(registries, structureMap -> {
			Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> configuredMap = structureMap.get(configuredStructure.feature);
			if (configuredMap == null) {
				return;
			}

			// This is tricky, the keys might be either from builtin (Vanilla) or dynamic registries (modded)
			RegistryKey<ConfiguredStructureFeature<?, ?>> configuredStructureKey = registries.get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY).getKey(configuredStructure).orElseThrow();
			ConfiguredStructureFeature<?, ?> mapKey = BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.get(configuredStructureKey);
			if (mapKey == null) {
				// This means the configured structure passed in is not a (potentially modified) Vanilla entry,
				// but rather a modded one. In this case, this will create a new entry in the map.
				mapKey = configuredStructure;
			}

			if (configuredMap.remove(mapKey, biome)) {
				result.set(true);
			}
		});

		return result.get();
	}

	public static boolean removeStructureStarts(DynamicRegistryManager registries,
												StructureFeature<?> structure,
												RegistryKey<Biome> biome) {
		AtomicBoolean result = new AtomicBoolean(false);

		changeStructureStarts(registries, structureMap -> {
			Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> configuredMap = structureMap.get(structure);
			if (configuredMap == null) {
				return;
			}

			if (configuredMap.values().remove(biome)) {
				result.set(true);
			}
		});

		return result.get();
	}

	private static void changeStructureStarts(DynamicRegistryManager registries, Consumer<Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>>> modifier) {
		Registry<ChunkGeneratorSettings> chunkGenSettingsRegistry = registries.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);

		for (Map.Entry<RegistryKey<ChunkGeneratorSettings>, ChunkGeneratorSettings> entry : chunkGenSettingsRegistry.getEntries()) {
			Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> structureMap = unfreeze(entry.getValue());

			modifier.accept(structureMap);

			freeze(entry.getValue(), structureMap);
		}
	}

	private static Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> unfreeze(ChunkGeneratorSettings settings) {
		ImmutableMap<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> frozenMap = settings.getStructuresConfig().field_34696;
		Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> result = new HashMap<>(frozenMap.size());

		for (Map.Entry<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> entry : frozenMap.entrySet()) {
			result.put(entry.getKey(), HashMultimap.create(entry.getValue()));
		}

		return result;
	}

	private static void freeze(ChunkGeneratorSettings settings, Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>>> structureStarts) {
		settings.getStructuresConfig().field_34696 = structureStarts.entrySet().stream()
				.collect(ImmutableMap.toImmutableMap(
						Map.Entry::getKey,
						e -> ImmutableMultimap.copyOf(e.getValue())
				));
	}

}
