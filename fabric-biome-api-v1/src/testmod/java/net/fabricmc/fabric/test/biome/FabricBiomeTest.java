
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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biome.v1.OverworldClimate;
import net.fabricmc.fabric.api.biome.v1.TheEndBiomes;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.*;

/**
 * <b>NOTES FOR TESTING:</b>
 * When running with this test-mod, also test this when running a dedicated server since there
 * are significant differences between server + client and how they sync biomes.
 *
 * <p>Ingame, you can use <code>/locatebiome</code> since we use nether- and end-biomes in the overworld,
 * and vice-versa, making them easy to find to verify the injection worked.
 *
 * <p>If you don't find a biome right away, teleport far away (~10000 blocks) from spawn and try again.
 */
public class FabricBiomeTest implements ModInitializer {
	public static final String MOD_ID = "fabric-biome-api-v1-testmod";

	private static final RegistryKey<Biome> TEST_CRIMSON_FOREST = RegistryKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "test_crimson_forest"));
	private static final RegistryKey<Biome> CUSTOM_PLAINS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "custom_plains"));
	private static final RegistryKey<Biome> TEST_END_HIGHLANDS = RegistryKey.of(Registry.BIOME_KEY, new Identifier(MOD_ID, "test_end_highlands"));

	private static BlockState STONE = Blocks.STONE.getDefaultState();
	private static ConfiguredSurfaceBuilder<TernarySurfaceConfig> TEST_END_SURFACE_BUILDER = register("end", SurfaceBuilder.DEFAULT.method_30478(new TernarySurfaceConfig(STONE, STONE, STONE)));

	@Override
	public void onInitialize() {
		Registry.register(BuiltinRegistries.BIOME, TEST_CRIMSON_FOREST.getValue(), DefaultBiomeCreator.createCrimsonForest());

		NetherBiomes.addNetherBiome(BiomeKeys.BEACH, new Biome.MixedNoisePoint(0.0F, 0.5F, 0.0F, 0.0F, 0.1F));
		NetherBiomes.addNetherBiome(TEST_CRIMSON_FOREST, new Biome.MixedNoisePoint(0.0F, 0.5F, 0.0F, 0.0F, 0.275F));

		Registry.register(BuiltinRegistries.BIOME, CUSTOM_PLAINS.getValue(), DefaultBiomeCreator.createPlains(false));
		OverworldBiomes.addBiomeVariant(BiomeKeys.PLAINS, CUSTOM_PLAINS, 1);

		Registry.register(BuiltinRegistries.BIOME, TEST_END_HIGHLANDS.getValue(), createEndHighlands());
		// TESTING HINT: to get to the end:
		// /execute in minecraft:the_end run tp @s 0 90 0
		TheEndBiomes.addHighlandsBiome(TEST_END_HIGHLANDS, 5.0);

		OverworldBiomes.addEdgeBiome(BiomeKeys.PLAINS, BiomeKeys.END_BARRENS, 0.9);

		OverworldBiomes.addShoreBiome(BiomeKeys.FOREST, BiomeKeys.NETHER_WASTES, 0.9);

		OverworldBiomes.addHillsBiome(BiomeKeys.BAMBOO_JUNGLE, BiomeKeys.BASALT_DELTAS, 0.9);

		OverworldBiomes.addContinentalBiome(BiomeKeys.END_HIGHLANDS, OverworldClimate.DRY, 0.5);
	}

	// These are used for testing the spacing of custom end biomes.
	private static Biome createEndHighlands() {
		GenerationSettings.Builder builder = (new GenerationSettings.Builder()).surfaceBuilder(TEST_END_SURFACE_BUILDER).structureFeature(ConfiguredStructureFeatures.END_CITY).feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_GATEWAY).feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CHORUS_PLANT);
		return composeEndSpawnSettings(builder);
	}

	private static Biome composeEndSpawnSettings(GenerationSettings.Builder builder) {
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addEndMobs(builder2);
		return (new Biome.Builder()).precipitation(Biome.Precipitation.NONE).category(Biome.Category.THEEND).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).effects((new BiomeEffects.Builder()).waterColor(4159204).waterFogColor(329011).fogColor(10518688).skyColor(0).moodSound(BiomeMoodSound.CAVE).build()).spawnSettings(builder2.build()).generationSettings(builder.build()).build();
	}

	private static <SC extends SurfaceConfig> ConfiguredSurfaceBuilder<SC> register(String id, ConfiguredSurfaceBuilder<SC> configuredSurfaceBuilder) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, id, configuredSurfaceBuilder);
	}
}
