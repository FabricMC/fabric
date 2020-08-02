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

import net.minecraft.block.Blocks;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biomes.v1.NetherBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.FabricBiomes;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;

public class FabricBiomeTest implements ModInitializer {
	public static final String MOD_ID = "fabric-biome-api-v1-testmod";

	@Override public void onInitialize() {
		Biome biome = Registry.register(BuiltinRegistries.BIOME, new Identifier(MOD_ID, "test_crimson_forest"), DefaultBiomeCreator.createCrimsonForest());
		NetherBiomes.addNetherBiome(Biomes.BEACH, new Biome.MixedNoisePoint(0.0F, 0.5F, 0.0F, 0.0F, 0.1F));
		NetherBiomes.addNetherBiome(biome, new Biome.MixedNoisePoint(0.0F, 0.5F, 0.0F, 0.0F, 0.275F));

		Biome customPlains = Registry.register(BuiltinRegistries.BIOME, new Identifier(MOD_ID, "custom_plains"), DefaultBiomeCreator.createPlains(null, false));
		OverworldBiomes.addBiomeVariant(Biomes.PLAINS, customPlains, 1);

		//Loop over existing biomes
		BuiltinRegistries.BIOME.forEach(this::handleBiome);

		//Listen for other biomes being registered
		RegistryEntryAddedCallback.event(BuiltinRegistries.BIOME).register((rawId, identifier, newBiome) -> handleBiome(newBiome));
	}

	private void handleBiome(Biome biome) {
		if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
			FabricBiomes.addFeatureToBiome(biome, GenerationStep.Feature.UNDERGROUND_ORES,
					Feature.ORE.configure(
						new OreFeatureConfig(
							OreFeatureConfig.Rules.BASE_STONE_OVERWORLD,
							Blocks.DIAMOND_BLOCK.getDefaultState(),
							8 //Ore vein size
						))
						.method_30377(64) //Max y level
						.spreadHorizontally()
						.repeat(16) //Number of veins per chunk
			);
		}
	}
}
