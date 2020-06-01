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

package net.fabricmc.fabric.test.level.generator;

import com.mojang.datafixers.Dynamic;

import net.minecraft.client.gui.screen.CustomizeFlatLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.biome.source.FixedBiomeSourceConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.level.generator.v1.FabricLevelGeneratorType;

public class LevelGeneratorTest implements ModInitializer {
	private static final FabricLevelGeneratorType GENERATOR_TYPE = new FabricLevelGeneratorType(new Identifier("fabric", "fabric_test")) {
		@Override
		public ChunkGenerator<? extends ChunkGeneratorConfig> createChunkGenerator(World world) {
			FlatChunkGeneratorConfig flatChunkGeneratorConfig = FlatChunkGeneratorConfig.fromDynamic(new Dynamic<>(NbtOps.INSTANCE, world.getLevelProperties().getGeneratorOptions()));
			FixedBiomeSourceConfig fixedBiomeSourceConfig = BiomeSourceType.FIXED.getConfig(world.getLevelProperties()).setBiome(flatChunkGeneratorConfig.getBiome());
			return ChunkGeneratorType.FLAT.create(world, BiomeSourceType.FIXED.applyConfig(fixedBiomeSourceConfig), flatChunkGeneratorConfig);
		}

		@Override
		@Environment(EnvType.CLIENT)
		public Screen getCustomizationScreen(CreateWorldScreen parent) {
			return new CustomizeFlatLevelScreen(parent, parent.generatorOptionsTag);
		}
	};

	@Override
	public void onInitialize() {
		GENERATOR_TYPE.setCustomizable(true);
	}
}
