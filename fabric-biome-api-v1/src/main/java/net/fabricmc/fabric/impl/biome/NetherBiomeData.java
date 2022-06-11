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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

/**
 * Internal data for modding Vanilla's {@link MultiNoiseBiomeSource.Preset#NETHER}.
 */
@ApiStatus.Internal
public final class NetherBiomeData {
	// Cached sets of the biomes that would generate from Vanilla's default biome source without consideration
	// for data packs (as those would be distinct biome sources).
	private static final Set<RegistryKey<Biome>> NETHER_BIOMES = new HashSet<>();

	private static final Map<RegistryKey<Biome>, MultiNoiseUtil.NoiseHypercube> NETHER_BIOME_NOISE_POINTS = new HashMap<>();

	private static final Logger LOGGER = LogUtils.getLogger();

	private NetherBiomeData() {
	}

	public static void addNetherBiome(RegistryKey<Biome> biome, MultiNoiseUtil.NoiseHypercube spawnNoisePoint) {
		Preconditions.checkArgument(biome != null, "Biome is null");
		Preconditions.checkArgument(spawnNoisePoint != null, "MultiNoiseUtil.NoiseValuePoint is null");
		NETHER_BIOME_NOISE_POINTS.put(biome, spawnNoisePoint);
		clearBiomeSourceCache();
	}

	public static Map<RegistryKey<Biome>, MultiNoiseUtil.NoiseHypercube> getNetherBiomeNoisePoints() {
		return NETHER_BIOME_NOISE_POINTS;
	}

	public static boolean canGenerateInNether(RegistryKey<Biome> biome) {
		if (NETHER_BIOMES.isEmpty()) {
			MultiNoiseBiomeSource source = MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(BuiltinRegistries.BIOME);

			for (RegistryEntry<Biome> entry : source.getBiomes()) {
				BuiltinRegistries.BIOME.getKey(entry.value()).ifPresent(NETHER_BIOMES::add);
			}
		}

		return NETHER_BIOMES.contains(biome) || NETHER_BIOME_NOISE_POINTS.containsKey(biome);
	}

	private static void clearBiomeSourceCache() {
		NETHER_BIOMES.clear(); // Clear cached biome source data
	}

	private static MultiNoiseUtil.Entries<RegistryEntry<Biome>> withModdedBiomeEntries(MultiNoiseUtil.Entries<RegistryEntry<Biome>> entries, Registry<Biome> biomeRegistry) {
		if (NETHER_BIOME_NOISE_POINTS.isEmpty()) {
			return entries;
		}

		ArrayList<Pair<MultiNoiseUtil.NoiseHypercube, RegistryEntry<Biome>>> entryList = new ArrayList<>(entries.getEntries());

		for (Map.Entry<RegistryKey<Biome>, MultiNoiseUtil.NoiseHypercube> entry : NETHER_BIOME_NOISE_POINTS.entrySet()) {
			if (biomeRegistry.contains(entry.getKey())) {
				entryList.add(Pair.of(entry.getValue(), biomeRegistry.entryOf(entry.getKey())));
			} else {
				LOGGER.warn("Nether biome {} not loaded", entry.getKey().getValue());
			}
		}

		return new MultiNoiseUtil.Entries<>(entryList);
	}

	public static void modifyBiomeSource(Registry<Biome> biomeRegistry, BiomeSource biomeSource) {
		if (biomeSource instanceof MultiNoiseBiomeSource multiNoiseBiomeSource) {
			if (((BiomeSourceAccess) multiNoiseBiomeSource).fabric_shouldModifyBiomeEntries() && multiNoiseBiomeSource.matchesInstance(MultiNoiseBiomeSource.Preset.NETHER)) {
				multiNoiseBiomeSource.biomeEntries = NetherBiomeData.withModdedBiomeEntries(
						MultiNoiseBiomeSource.Preset.NETHER.biomeSourceFunction.apply(biomeRegistry),
						biomeRegistry);
				multiNoiseBiomeSource.biomes = multiNoiseBiomeSource.biomeEntries.getEntries().stream().map(Pair::getSecond).collect(Collectors.toSet());
				((BiomeSourceAccess) multiNoiseBiomeSource).fabric_setModifyBiomeEntries(false);
			}
		}
	}
}
