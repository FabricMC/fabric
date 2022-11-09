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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.util.registry.RegistryWrapper;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.OverworldBiomeCreator;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.TheNetherBiomeCreator;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.EndPlacedFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricWorldgenProvider;

public class WorldgenProvider extends FabricWorldgenProvider {
	public WorldgenProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
		entries.add(FabricBiomeTest.TEST_CRIMSON_FOREST, TheNetherBiomeCreator.createCrimsonForest(entries.placedFeatures(), entries.configuredCarvers()));
		entries.add(FabricBiomeTest.CUSTOM_PLAINS, OverworldBiomeCreator.createPlains(entries.placedFeatures(), entries.configuredCarvers(), false, false, false));
		entries.add(FabricBiomeTest.TEST_END_HIGHLANDS, createEndHighlands(entries));
		;
		entries.add(FabricBiomeTest.TEST_END_MIDLANDS, createEndMidlands(entries));
		entries.add(FabricBiomeTest.TEST_END_BARRRENS, createEndBarrens(entries));

		ConfiguredFeature<?, ?> COMMON_DESERT_WELL = new ConfiguredFeature<>(Feature.DESERT_WELL, DefaultFeatureConfig.INSTANCE);

		RegistryEntry<ConfiguredFeature<?, ?>> featureRef = entries.add(FabricBiomeTest.COMMON_DESERT_WELL, COMMON_DESERT_WELL);

		// The placement config is taken from the vanilla desert well, but no randomness
		PlacedFeature PLACED_COMMON_DESERT_WELL = new PlacedFeature(featureRef, List.of(SquarePlacementModifier.of(), PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP, BiomePlacementModifier.of()));
		entries.add(FabricBiomeTest.PLACED_COMMON_DESERT_WELL, PLACED_COMMON_DESERT_WELL);
	}

	@Override
	public String getName() {
		return "Fabric Biome Testmod";
	}

	// These are used for testing the spacing of custom end biomes.
	private static Biome createEndHighlands(Entries entries) {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
				.feature(GenerationStep.Feature.SURFACE_STRUCTURES, entries.ref(EndPlacedFeatures.END_GATEWAY_RETURN));
		return composeEndSpawnSettings(builder);
	}

	public static Biome createEndMidlands(Entries entries) {
		GenerationSettings.Builder builder = (new GenerationSettings.Builder());
		return composeEndSpawnSettings(builder);
	}

	public static Biome createEndBarrens(Entries entries) {
		GenerationSettings.Builder builder = (new GenerationSettings.Builder());
		return composeEndSpawnSettings(builder);
	}

	private static Biome composeEndSpawnSettings(GenerationSettings.Builder builder) {
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addPlainsMobs(builder2);
		return (new Biome.Builder()).precipitation(Biome.Precipitation.NONE).temperature(0.5F).downfall(0.5F).effects((new BiomeEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(10518688).skyColor(0).moodSound(BiomeMoodSound.CAVE).build()).spawnSettings(builder2.build()).generationSettings(builder.build()).build();
	}
}
