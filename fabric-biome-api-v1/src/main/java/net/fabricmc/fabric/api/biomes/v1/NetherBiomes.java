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

package net.fabricmc.fabric.api.biomes.v1;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;

/**
 * API that exposes the internals of Minecraft's nether biome code.
 *
 * @deprecated Because of the volatility of world generation in Minecraft 1.16, this API is marked experimental
 * since it is likely to change in future Minecraft versions.
 */
@Deprecated
public final class NetherBiomes {
	private NetherBiomes() {
	}

	/**
	 * Adds a biome to the Nether generator.
	 *
	 * @param biome           The biome to add. Must not be null.
	 * @param mixedNoisePoint data about the given {@link Biome}'s spawning information in the nether.
	 * @see Biome.MixedNoisePoint
	 */
	public static void addNetherBiome(RegistryKey<Biome> biome, Biome.MixedNoisePoint mixedNoisePoint) {
		InternalBiomeData.addNetherBiome(biome, mixedNoisePoint);
	}
}
