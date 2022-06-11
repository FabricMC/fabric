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
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

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

	public static Overrides createOverrides(Registry<Biome> biomeRegistry) {
		return new Overrides(biomeRegistry);
	}

	/**
	 * An instance of this class is attached to each {@link TheEndBiomeSource}.
	 */
	public static class Overrides {
		public final Set<RegistryEntry<Biome>> customBiomes;

		// Vanilla entries to compare against
		private final RegistryEntry<Biome> endMidlands;
		private final RegistryEntry<Biome> endBarrens;
		private final RegistryEntry<Biome> endHighlands;

		// Maps where the keys have been resolved to actual entries
		private final @Nullable Map<RegistryEntry<Biome>, WeightedPicker<RegistryEntry<Biome>>> endBiomesMap;
		private final @Nullable Map<RegistryEntry<Biome>, WeightedPicker<RegistryEntry<Biome>>> endMidlandsMap;
		private final @Nullable Map<RegistryEntry<Biome>, WeightedPicker<RegistryEntry<Biome>>> endBarrensMap;

		// cache for our own sampler (used for random biome replacement selection)
		private final Map<MultiNoiseUtil.MultiNoiseSampler, PerlinNoiseSampler> samplers = new WeakHashMap<>();

		public Overrides(Registry<Biome> biomeRegistry) {
			this.customBiomes = ADDED_BIOMES.stream().map(biomeRegistry::entryOf).collect(Collectors.toSet());

			this.endMidlands = biomeRegistry.entryOf(BiomeKeys.END_MIDLANDS);
			this.endBarrens = biomeRegistry.entryOf(BiomeKeys.END_BARRENS);
			this.endHighlands = biomeRegistry.entryOf(BiomeKeys.END_HIGHLANDS);

			this.endBiomesMap = resolveOverrides(biomeRegistry, END_BIOMES_MAP);
			this.endMidlandsMap = resolveOverrides(biomeRegistry, END_MIDLANDS_MAP);
			this.endBarrensMap = resolveOverrides(biomeRegistry, END_BARRENS_MAP);
		}

		// Resolves all RegistryKey instances to RegistryEntries
		private @Nullable Map<RegistryEntry<Biome>, WeightedPicker<RegistryEntry<Biome>>> resolveOverrides(Registry<Biome> biomeRegistry, Map<RegistryKey<Biome>, WeightedPicker<RegistryKey<Biome>>> overrides) {
			Map<RegistryEntry<Biome>, WeightedPicker<RegistryEntry<Biome>>> result = new IdentityHashMap<>(overrides.size());

			for (Map.Entry<RegistryKey<Biome>, WeightedPicker<RegistryKey<Biome>>> entry : overrides.entrySet()) {
				WeightedPicker<RegistryKey<Biome>> picker = entry.getValue();
				if (picker.getEntryCount() <= 1) continue; // don't use no-op entries

				result.put(biomeRegistry.entryOf(entry.getKey()), picker.map(biomeRegistry::entryOf));
			}

			return result.isEmpty() ? null : result;
		}

		public RegistryEntry<Biome> pick(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise, RegistryEntry<Biome> vanillaBiome) {
			if (vanillaBiome == endMidlands || vanillaBiome == endBarrens) {
				// select a random highlands biome replacement, then try to replace it with a midlands or barrens biome replacement
				RegistryEntry<Biome> highlandsReplacement = pick(endHighlands, endHighlands, endBiomesMap, x, z, noise);
				Map<RegistryEntry<Biome>, WeightedPicker<RegistryEntry<Biome>>> map = vanillaBiome == endMidlands ? endMidlandsMap : endBarrensMap;

				return pick(highlandsReplacement, vanillaBiome, map, x, z, noise);
			} else {
				assert END_BIOMES_MAP.containsKey(vanillaBiome.getKey().orElseThrow());

				return pick(vanillaBiome, vanillaBiome, endBiomesMap, x, z, noise);
			}
		}

		private <T> T pick(T key, T defaultValue, Map<T, WeightedPicker<T>> pickers, int x, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
			if (pickers == null) return defaultValue;

			WeightedPicker<T> picker = pickers.get(key);
			if (picker == null || picker.getEntryCount() <= 1) return defaultValue;

			// The x and z of the entry are divided by 64 to ensure custom biomes are large enough; going larger than this
			// seems to make custom biomes too hard to find.
			return picker.pickFromNoise(getSampler(noise), x / 64.0, 0, z / 64.0);
		}

		private synchronized PerlinNoiseSampler getSampler(MultiNoiseUtil.MultiNoiseSampler noise) {
			PerlinNoiseSampler ret = samplers.get(noise);

			if (ret == null) {
				Long seed = ((MultiNoiseSamplerHooks) (Object) noise).fabric_getSeed();

				if (seed == null) {
					throw new IllegalStateException("MultiNoiseSampler doesn't have a seed set, created using different method?");
				}

				ret = new PerlinNoiseSampler(new ChunkRandom(new CheckedRandom(seed)));
				samplers.put(noise, ret);
			}

			return ret;
		}
	}
}
