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

package net.fabricmc.fabric.test.biome;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.CrimsonForestBiome;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biomes.v1.NetherBiomes;

public class FabricBiomeTest implements ModInitializer {
	public static final String MOD_ID = "fabric-biome-api-v1-testmod";

	@Override public void onInitialize() {
		TestCrimsonForestBiome biome = Registry.register(Registry.BIOME, new Identifier(MOD_ID, "test_crimson_forest"), new TestCrimsonForestBiome());
		NetherBiomes.addNetherBiome(Biomes.BEACH);
		NetherBiomes.addNetherBiome(biome);
	}

	public class TestCrimsonForestBiome extends CrimsonForestBiome {
	}
}
