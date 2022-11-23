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

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.biome.v1.NetherBiomes;
import net.fabricmc.fabric.api.biome.v1.TheEndBiomes;

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

	public static final RegistryKey<Biome> TEST_CRIMSON_FOREST = RegistryKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "test_crimson_forest"));
	public static final RegistryKey<Biome> CUSTOM_PLAINS = RegistryKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "custom_plains"));
	public static final RegistryKey<Biome> TEST_END_HIGHLANDS = RegistryKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "test_end_highlands"));
	public static final RegistryKey<Biome> TEST_END_MIDLANDS = RegistryKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "test_end_midlands"));
	public static final RegistryKey<Biome> TEST_END_BARRRENS = RegistryKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "test_end_barrens"));

	public static final List<RegistryKey<Biome>> TEST_BIOMES = List.of(
			TEST_CRIMSON_FOREST,
			CUSTOM_PLAINS,
			TEST_END_HIGHLANDS,
			TEST_END_MIDLANDS,
			TEST_END_BARRRENS
	);

	public static final RegistryKey<ConfiguredFeature<?, ?>> COMMON_DESERT_WELL = RegistryKey.of(
			RegistryKeys.CONFIGURED_FEATURE,
			new Identifier(FabricBiomeTest.MOD_ID, "fab_desert_well")
	);
	public static final RegistryKey<PlacedFeature> PLACED_COMMON_DESERT_WELL = RegistryKey.of(
			RegistryKeys.PLACED_FEATURE,
			new Identifier(FabricBiomeTest.MOD_ID, "fab_desert_well")
	);

	@Override
	public void onInitialize() {
		NetherBiomes.addNetherBiome(BiomeKeys.PLAINS, MultiNoiseUtil.createNoiseHypercube(0.0F, 0.5F, 0.0F, 0.0F, 0.0f, 0, 0.1F));
		NetherBiomes.addNetherBiome(TEST_CRIMSON_FOREST, MultiNoiseUtil.createNoiseHypercube(0.0F, -0.15F, 0.0f, 0.0F, 0.0f, 0.0F, 0.2F));

		// TESTING HINT: to get to the end:
		// /execute in minecraft:the_end run tp @s 0 90 0
		TheEndBiomes.addHighlandsBiome(BiomeKeys.PLAINS, 5.0);
		TheEndBiomes.addHighlandsBiome(TEST_END_HIGHLANDS, 5.0);
		TheEndBiomes.addMidlandsBiome(TEST_END_HIGHLANDS, TEST_END_MIDLANDS, 10.0);
		TheEndBiomes.addBarrensBiome(TEST_END_HIGHLANDS, TEST_END_BARRRENS, 10.0);

		BiomeModifications.create(new Identifier("fabric:test_mod"))
				.add(ModificationPhase.ADDITIONS,
						BiomeSelectors.foundInOverworld(),
						modification -> modification.getWeather().setDownfall(100))
				.add(ModificationPhase.ADDITIONS,
						BiomeSelectors.includeByKey(BiomeKeys.DESERT), // TODO: switch to fabric desert biome tag once it is there?
						context -> {
							context.getGenerationSettings().addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION,
									PLACED_COMMON_DESERT_WELL
							);
						})
				.add(ModificationPhase.ADDITIONS,
						BiomeSelectors.tag(TagKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "tag_selector_test"))),
						context -> context.getEffects().setSkyColor(0x770000));

		// Make sure data packs can define dynamic registry contents
		// See #2225, #2261
		BiomeModifications.addFeature(
				BiomeSelectors.foundInOverworld(),
				GenerationStep.Feature.VEGETAL_DECORATION,
				RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(MOD_ID, "concrete_pile"))
		);

		// Make sure data packs can define biomes
		NetherBiomes.addNetherBiome(
				RegistryKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "example_biome")),
				MultiNoiseUtil.createNoiseHypercube(1.0f, 0.0f, 0.0f, 0.0f, 0.2f, 0.5f, 0.3f)
		);
		TheEndBiomes.addHighlandsBiome(
				RegistryKey.of(RegistryKeys.BIOME, new Identifier(MOD_ID, "example_biome")),
				10.0
		);
	}
}
