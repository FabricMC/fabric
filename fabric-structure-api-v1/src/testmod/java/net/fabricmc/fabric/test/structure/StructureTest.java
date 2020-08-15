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

package net.fabricmc.fabric.test.structure;

import java.util.Random;

import com.mojang.serialization.Codec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePieceWithDimensions;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import net.fabricmc.fabric.api.structure.v1.FabricStructureBuilder;

public class StructureTest {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final StructureFeature<DefaultFeatureConfig> STRUCTURE = new TestStructureFeature(DefaultFeatureConfig.CODEC);
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ? extends StructureFeature<DefaultFeatureConfig>> CONFIGURED_STRUCTURE = STRUCTURE.configure(new DefaultFeatureConfig());
	public static final StructurePieceType PIECE = TestStructureGenerator::new;

	static {
		LOGGER.info("Registering test structure");
		FabricStructureBuilder.create(new Identifier("fabric", "test_structure"), STRUCTURE)
				.step(GenerationStep.Feature.SURFACE_STRUCTURES)
				.defaultConfig(32, 8, 12345)
				.superflatFeature(CONFIGURED_STRUCTURE)
				.adjustsSurface()
				.register();
		Registry.register(Registry.STRUCTURE_PIECE, new Identifier("fabric", "test_structure_piece"), PIECE);
	}

	public static class TestStructureFeature extends StructureFeature<DefaultFeatureConfig> {
		public TestStructureFeature(Codec<DefaultFeatureConfig> codec) {
			super(codec);
		}

		@Override
		public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
			return Start::new;
		}

		public static class Start extends StructureStart<DefaultFeatureConfig> {
			public Start(StructureFeature<DefaultFeatureConfig> feature, int chunkX, int chunkZ, BlockBox box, int references, long seed) {
				super(feature, chunkX, chunkZ, box, references, seed);
			}

			@Override
			public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, int chunkX, int chunkZ, Biome biome, DefaultFeatureConfig featureConfig) {
				int blockX = chunkX * 16;
				int blockZ = chunkZ * 16;
				int blockY = chunkGenerator.getHeight(blockX, blockZ, Heightmap.Type.WORLD_SURFACE_WG);

				TestStructureGenerator generator = new TestStructureGenerator(random, blockX, blockY, blockZ);
				this.children.add(generator);
				setBoundingBoxFromChildren();
			}
		}
	}

	public static class TestStructureGenerator extends StructurePieceWithDimensions {
		protected TestStructureGenerator(Random random, int x, int y, int z) {
			super(PIECE, random, x, y, z, 48, 16, 48);
		}

		protected TestStructureGenerator(StructureManager structureManager, CompoundTag compoundTag) {
			super(PIECE, compoundTag);
		}

		@Override
		public boolean generate(StructureWorldAccess structureWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos blockPos) {
			for (int x = 0; x < 48; x++) {
				for (int z = 0; z < 48; z++) {
					for (int y = 0; y < 16; y++) {
						this.addBlock(structureWorldAccess, Blocks.DIAMOND_BLOCK.getDefaultState(), x, y, z, boundingBox);
					}
				}
			}

			return true;
		}
	}
}
