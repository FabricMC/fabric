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

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.SquarePlacementModifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DataGeneratorEntrypoint implements net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint {
	public static final RegistryKey<ConfiguredFeature<?, ?>> COMMON_DESERT_WELL = RegistryKey.of(
			RegistryKeys.CONFIGURED_FEATURE,
			new Identifier(FabricBiomeTest.MOD_ID, "fab_desert_well")
	);
	public static final RegistryKey<PlacedFeature> PLACED_COMMON_DESERT_WELL = RegistryKey.of(
			RegistryKeys.PLACED_FEATURE,
			new Identifier(FabricBiomeTest.MOD_ID, "fab_desert_well")
	);

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
		FabricDataGenerator.Pack pack = dataGenerator.createPack();
		pack.addProvider(WorldgenProvider::new);
		pack.addProvider(TestBiomeTagProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, this::bootstrapConfiguredFeatures);
		registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, this::bootstrapPlacedFeatures);
		registryBuilder.addRegistry(RegistryKeys.BIOME, TestBiomes::bootstrap);
	}

	private void bootstrapConfiguredFeatures(Registerable<ConfiguredFeature<?, ?>> registerable) {
		ConfiguredFeatures.register(registerable, COMMON_DESERT_WELL, Feature.DESERT_WELL);
	}

	private void bootstrapPlacedFeatures(Registerable<PlacedFeature> registerable) {
		RegistryEntryLookup<ConfiguredFeature<?, ?>> configuredFeatures = registerable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
		RegistryEntry<ConfiguredFeature<?, ?>> commonDesertWell = configuredFeatures.getOrThrow(COMMON_DESERT_WELL);

		// The placement config is taken from the vanilla desert well, but no randomness
		PlacedFeatures.register(registerable, PLACED_COMMON_DESERT_WELL, commonDesertWell,
				SquarePlacementModifier.of(),
				PlacedFeatures.MOTION_BLOCKING_HEIGHTMAP,
				BiomePlacementModifier.of()
		);
	}
}
