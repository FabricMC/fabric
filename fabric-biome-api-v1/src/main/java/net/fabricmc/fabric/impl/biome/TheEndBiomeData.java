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

package net.fabricmc.fabric.impl.biome;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;

/**
 * Internal data for modding Vanilla's {@link TheEndBiomeSource}.
 */
@ApiStatus.Internal
public final class TheEndBiomeData {
	public static final Set<RegistryKey<Biome>> ADDED_BIOMES = new HashSet<>();
	private static final Map<RegistryKey<Biome>, WeightedPicker<RegistryKey<Biome>>> END_BIOMES_MAP = new IdentityHashMap<>();
	private static final Map<RegistryKey<Biome>, WeightedPicker<RegistryKey<Biome>>> END_MIDLANDS_MAP = new IdentityHashMap<>();
	private static final Map<RegistryKey<Biome>, WeightedPicker<RegistryKey<Biome>>> END_BARRENS_MAP = new IdentityHashMap<>();

	static {
		END_BIOMES_MAP.computeIfAbsent(BiomeKeys.THE_END, key -> new WeightedPicker<>())
				.add(BiomeKeys.THE_END, 1.0);
		END_BIOMES_MAP.computeIfAbsent(BiomeKeys.END_HIGHLANDS, key -> new WeightedPicker<>())
				.add(BiomeKeys.END_HIGHLANDS, 1.0);
		END_BIOMES_MAP.computeIfAbsent(BiomeKeys.SMALL_END_ISLANDS, key -> new WeightedPicker<>())
				.add(BiomeKeys.SMALL_END_ISLANDS, 1.0);

		END_MIDLANDS_MAP.computeIfAbsent(BiomeKeys.END_HIGHLANDS, key -> new WeightedPicker<>())
				.add(BiomeKeys.END_MIDLANDS, 1.0);
		END_BARRENS_MAP.computeIfAbsent(BiomeKeys.END_HIGHLANDS, key -> new WeightedPicker<>())
				.add(BiomeKeys.END_BARRENS, 1.0);
	}

	private TheEndBiomeData() {
	}

	public static void addEndBiomeReplacement(RegistryKey<Biome> replaced, RegistryKey<Biome> variant, double weight) {
		Preconditions.checkNotNull(replaced, "replaced entry is null");
		Preconditions.checkNotNull(variant, "variant entry is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_BIOMES_MAP.computeIfAbsent(replaced, key -> new WeightedPicker<>()).add(variant, weight);
		ADDED_BIOMES.add(variant);
	}

	public static void addEndMidlandsReplacement(RegistryKey<Biome> highlands, RegistryKey<Biome> midlands, double weight) {
		Preconditions.checkNotNull(highlands, "highlands entry is null");
		Preconditions.checkNotNull(midlands, "midlands entry is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_MIDLANDS_MAP.computeIfAbsent(highlands, key -> new WeightedPicker<>()).add(midlands, weight);
		ADDED_BIOMES.add(midlands);
	}

	public static void addEndBarrensReplacement(RegistryKey<Biome> highlands, RegistryKey<Biome> barrens, double weight) {
		Preconditions.checkNotNull(highlands, "highlands entry is null");
		Preconditions.checkNotNull(barrens, "midlands entry is null");
		Preconditions.checkArgument(weight > 0.0, "Weight is less than or equal to 0.0 (got %s)", weight);
		END_BARRENS_MAP.computeIfAbsent(highlands, key -> new WeightedPicker<>()).add(barrens, weight);
		ADDED_BIOMES.add(barrens);
	}

	public static Overrides createOverrides(Registry<Biome> biomeRegistry, long seed) {
		return new Overrides(biomeRegistry, seed);
	}

	/**
	 * An instance of this class is attached to each {@link TheEndBiomeSource}.
	 */
	public static class Overrides {
		public final Set<RegistryEntry<Biome>> customBiomes;
		private final PerlinNoiseSampler sampler;

		// Vanilla entries to compare against
		private final RegistryEntry<Biome> endMidlands;
		private final RegistryEntry<Biome> endBarrens;
		private final RegistryEntry<Biome> endHighlands;

		// Maps where the keys have been resolved to actual entries
		private final Map<RegistryEntry<Biome>, WeightedPicker<RegistryEntry<Biome>>> endBiomesMap;
		private final Map<RegistryEntry<Biome>, WeightedPicker<RegistryEntry<Biome>>> endMidlandsMap;
		private final Map<RegistryEntry<Biome>, WeightedPicker<RegistryEntry<Biome>>> endBarrensMap;

		public Overrides(Registry<Biome> biomeRegistry, long seed) {
			this.customBiomes = ADDED_BIOMES.stream().map(biomeRegistry::entryOf).collect(Collectors.toSet());
			this.sampler = new PerlinNoiseSampler(new ChunkRandom(new AtomicSimpleRandom(seed)));
			this.endMidlands = biomeRegistry.entryOf(BiomeKeys.END_MIDLANDS);
			this.endBarrens = biomeRegistry.entryOf(BiomeKeys.END_BARRENS);
			this.endHighlands = biomeRegistry.entryOf(BiomeKeys.END_HIGHLANDS);

			this.endBiomesMap = resolveOverrides(biomeRegistry, END_BIOMES_MAP);
			this.endMidlandsMap = resolveOverrides(biomeRegistry, END_MIDLANDS_MAP);
			this.endBarrensMap = resolveOverrides(biomeRegistry, END_BARRENS_MAP);
		}

		// Resolves all RegistryKey instances to RegistryEntries
		private Map<RegistryEntry<Biome>, WeightedPicker<RegistryEntry<Biome>>> resolveOverrides(Registry<Biome> biomeRegistry, Map<RegistryKey<Biome>, WeightedPicker<RegistryKey<Biome>>> overrides) {
			Map<RegistryEntry<Biome>, WeightedPicker<RegistryEntry<Biome>>> result = new Object2ObjectOpenCustomHashMap<>(overrides.size(), RegistryEntryHashStrategy.INSTANCE);

			for (Map.Entry<RegistryKey<Biome>, WeightedPicker<RegistryKey<Biome>>> entry : overrides.entrySet()) {
				result.put(biomeRegistry.entryOf(entry.getKey()), entry.getValue().map(biomeRegistry::entryOf));
			}

			return result;
		}

		public RegistryEntry<Biome> pick(int x, int y, int z, RegistryEntry<Biome> vanillaBiome) {
			RegistryEntry<Biome> replacementKey;

			// The x and z of the entry are divided by 64 to ensure custom biomes are large enough; going larger than this]
			// seems to make custom biomes too hard to find.
			boolean isMidlands = vanillaBiome.matches(endMidlands::matchesKey);

			if (isMidlands || vanillaBiome.matches(endBarrens::matchesKey)) {
				// Since the highlands picker is statically populated by InternalBiomeData, picker will never be null.
				WeightedPicker<RegistryEntry<Biome>> highlandsPicker = endBiomesMap.get(endHighlands);
				RegistryEntry<Biome> highlandsKey = highlandsPicker.pickFromNoise(sampler, x / 64.0, 0, z / 64.0);

				if (isMidlands) {
					WeightedPicker<RegistryEntry<Biome>> midlandsPicker = endMidlandsMap.get(highlandsKey);
					replacementKey = (midlandsPicker == null) ? vanillaBiome : midlandsPicker.pickFromNoise(sampler, x / 64.0, 0, z / 64.0);
				} else {
					WeightedPicker<RegistryEntry<Biome>> barrensPicker = endBarrensMap.get(highlandsKey);
					replacementKey = (barrensPicker == null) ? vanillaBiome : barrensPicker.pickFromNoise(sampler, x / 64.0, 0, z / 64.0);
				}
			} else {
				// Since the main island and small islands pickers are statically populated by InternalBiomeData, picker will never be null.
				WeightedPicker<RegistryEntry<Biome>> picker = endBiomesMap.get(vanillaBiome);
				replacementKey = picker.pickFromNoise(sampler, x / 64.0, 0, z / 64.0);
			}

			return replacementKey;
		}
	}

	enum RegistryEntryHashStrategy implements Hash.Strategy<RegistryEntry<?>> {
		INSTANCE;

		@Override
		public boolean equals(RegistryEntry<?> a, RegistryEntry<?> b) {
			if (a == b) return true;
			if (a == null || b == null) return false;
			if (a.getType() != b.getType()) return false;
			// This Optional#get is safe - if a has key, b should also have key
			// given a.getType() != b.getType() check above
			// noinspection OptionalGetWithoutIsPresent
			return a.getKeyOrValue().map(key -> b.getKey().get() == key, b.value()::equals);
		}

		@Override
		public int hashCode(RegistryEntry<?> a) {
			if (a == null) return 0;
			return a.getKeyOrValue().map(System::identityHashCode, Object::hashCode);
		}
	}
}
