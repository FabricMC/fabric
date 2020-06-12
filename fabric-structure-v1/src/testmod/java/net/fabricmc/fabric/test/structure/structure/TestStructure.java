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

package net.fabricmc.fabric.test.structure.structure;

import net.minecraft.structure.IglooGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import net.fabricmc.fabric.api.structure.v1.FabricStructure;

/**
 * A testing structure that clones the generator of the igloo.
 */
public class TestStructure extends FabricStructure<DefaultFeatureConfig> {
	public TestStructure() {
		super(DefaultFeatureConfig.CODEC);
	}

	@Override
	public GenerationStep.Feature method_28663() {
		return GenerationStep.Feature.SURFACE_STRUCTURES;
	}

	@Override
	public StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return TestStructure.Start::new;
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {
		public Start(StructureFeature<DefaultFeatureConfig> feature, int chunkX, int chunkZ, BlockBox box, int references, long seed) {
			super(feature, chunkX, chunkZ, box, references, seed);
		}

		@Override
		public void init(ChunkGenerator chunkGenerator, StructureManager structureManager, int x, int z, Biome biome, DefaultFeatureConfig featureConfig) {
			BlockRotation blockRotation = BlockRotation.random(this.random);
			IglooGenerator.addPieces(structureManager, new BlockPos(x * 16, 90, z * 16), blockRotation, this.children, this.random);
			this.setBoundingBoxFromChildren();
		}
	}
}
