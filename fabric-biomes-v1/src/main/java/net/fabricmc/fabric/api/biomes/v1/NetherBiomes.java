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

import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.biome.NetherBiomesImpl;

/**
 * API that exposes internals' of Minecraft's nether biome code.
 */
public class NetherBiomes {
	private NetherBiomes() { }

	/**
	 * Adds a biome to the Nether generator. Biomes must set their own noise generation values in the biome settings class.
	 *
	 * @param biome The biome to add.
	 */
	public static void addNetherBiome(Biome biome) {
		NetherBiomesImpl.addNetherBiome(biome);
	}
}
