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

package net.fabricmc.fabric.test.generatortype;

import java.util.function.Supplier;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

import net.fabricmc.fabric.api.generatortype.v1.FabricGeneratorConfig;
import net.fabricmc.fabric.api.generatortype.v1.FabricGeneratorType;

final class TestGeneratorType extends FabricGeneratorType<TestGeneratorConfig> {
	TestGeneratorType(Identifier name) {
		super(name, true);
	}

	@Override
	public ChunkGenerator getChunkGenerator(BiomeSource biomeSource, Supplier<ChunkGeneratorSettings> generatorSettings, FabricGeneratorConfig generatorConfig, long seed) {
		return new TestChunkGenerator(biomeSource, generatorSettings, (TestGeneratorConfig) generatorConfig, seed);
	}

	@Override
	public RegistryKey<ChunkGeneratorSettings> getGeneratorSettings() {
		return ChunkGeneratorSettings.OVERWORLD;
	}

	@Override
	public BiomeSource getBiomeSource(Registry<Biome> biomeRegistry, long seed) {
		return new TestBiomeSource(biomeRegistry, seed);
	}

	@Override
	public TestGeneratorConfig getGeneratorConfig(Registry<Biome> biomeRegistry) {
		return new TestGeneratorConfig(Blocks.STONE, 64);
	}

	@Override
	public GeneratorType.ScreenProvider getCustomizationScreenFactory() {
		return (screen, generatorOptions) -> new CustomizationScreen().getCustomizationScreen(screen, generatorOptions);
	}

	private final class CustomizationScreen {
		private Screen getCustomizationScreen(CreateWorldScreen parent, GeneratorOptions generatorOptions) {
			ChunkGenerator chunkGenerator = generatorOptions.getChunkGenerator();
			return new TestCustomizationScreen(parent, (config) -> parent.moreOptionsDialog.setGeneratorOptions(new GeneratorOptions(generatorOptions.getSeed(), generatorOptions.shouldGenerateStructures(), generatorOptions.hasBonusChest(),
					GeneratorOptions.method_28608(parent.moreOptionsDialog.method_29700().get(Registry.DIMENSION_TYPE_KEY), generatorOptions.getDimensions(),
							new TestChunkGenerator(TestGeneratorType.this.getBiomeSource(parent.moreOptionsDialog.method_29700().get(Registry.BIOME_KEY), generatorOptions.getSeed()),
									() -> parent.moreOptionsDialog.method_29700().get(Registry.NOISE_SETTINGS_WORLDGEN).getOrThrow(TestGeneratorType.this.getGeneratorSettings()), config, generatorOptions.getSeed())))),
					chunkGenerator instanceof TestChunkGenerator ? ((TestChunkGenerator) chunkGenerator).getGeneratorConfig() : getGeneratorConfig(parent.moreOptionsDialog.method_29700().get(Registry.BIOME_KEY)));
		}
	}
}
