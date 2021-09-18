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
import java.util.Set;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;

/**
 * Internal data for modding Vanilla's {@link MultiNoiseBiomeSource.Preset#OVERWORLD}.
 */
@ApiStatus.Internal
public final class OverworldBiomeData {
	private static final Set<RegistryKey<Biome>> OVERWORLD_BIOMES = new HashSet<>();

	private OverworldBiomeData() {
	}

	public static boolean canGenerateInOverworld(RegistryKey<Biome> biome) {
		if (OVERWORLD_BIOMES.isEmpty()) {
			MultiNoiseBiomeSource source = MultiNoiseBiomeSource.Preset.OVERWORLD.getBiomeSource(BuiltinRegistries.BIOME);

			for (Biome netherBiome : source.getBiomes()) {
				BuiltinRegistries.BIOME.getKey(netherBiome).ifPresent(OVERWORLD_BIOMES::add);
			}
		}

		return OVERWORLD_BIOMES.contains(biome);
	}

	public static void clearOverworldCache() {
		OVERWORLD_BIOMES.clear();
	}
}
