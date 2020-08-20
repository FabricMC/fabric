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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltInBiomes;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biomes.v1.FabricBiomes;
import net.fabricmc.fabric.api.biomes.v1.event.BiomeLoadingCallback;

public class FabricBiomeTest implements ModInitializer {
	public static final String MOD_ID = "fabric-biome-api-v1-testmod";

	public static final RegistryKey<Biome> TEST_CRIMSON_FOREST = RegistryKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "test_crimson_forest"));
	public static final RegistryKey<Biome> TEST_PLAINS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "test_plains"));

	@Override public void onInitialize() {
		// Works fine
		FabricBiomes.register(TEST_PLAINS, DefaultBiomeCreator.createPlains(false));
		FabricBiomes.register(TEST_CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());

		// TODO: Not working
		FabricBiomes.addToOverworld(TEST_PLAINS);

		// Works fine
		FabricBiomes.addToNether(TEST_CRIMSON_FOREST, new Biome.MixedNoisePoint(0.0f, 0.5f, 0.0f, 0.0f, 0.1f));

		// TODO: Not working (should this work?)
		FabricBiomes.addToNether(BuiltInBiomes.BEACH, new Biome.MixedNoisePoint(0.0f, 0.5f, 0.0f, 0.0f, 0.275f));

		// Works fine
		BiomeLoadingCallback.EVENT.register((biomeRegistryKey, biomeBuilder) -> {
			if (biomeRegistryKey.equals(BuiltInBiomes.PLAINS)) {
				biomeBuilder.structureFeature(() -> ConfiguredStructureFeatures.DESERT_PYRAMID);
			}
		});
	}
}
