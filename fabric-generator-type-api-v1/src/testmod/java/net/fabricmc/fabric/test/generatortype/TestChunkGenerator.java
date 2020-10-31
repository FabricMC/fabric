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

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

import net.fabricmc.fabric.api.generatortype.v1.FabricChunkGenerator;

final class TestChunkGenerator extends FabricChunkGenerator<TestGeneratorConfig> {
	public static final Codec<TestChunkGenerator> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			BiomeSource.CODEC.fieldOf("biome_source").forGetter(FabricChunkGenerator::getBiomeSource),
			ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(FabricChunkGenerator::getGeneratorSettings),
			TestGeneratorConfig.CODEC.fieldOf("config").forGetter(FabricChunkGenerator::getGeneratorConfig),
			Codec.LONG.fieldOf("seed").stable().forGetter(FabricChunkGenerator::getSeed))
			.apply(instance, instance.stable(TestChunkGenerator::new)));

	private final int worldHeight = this.getGeneratorConfig().getWorldHeight();
	private final Block[] blocks = Stream.generate(() -> this.getGeneratorConfig().getWorldBlock()).limit(this.worldHeight).toArray(Block[]::new);

	TestChunkGenerator(BiomeSource biomeSource, Supplier<ChunkGeneratorSettings> generatorSettings, TestGeneratorConfig generatorConfig, long seed) {
		super(biomeSource, generatorSettings, generatorConfig, seed);
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return new TestChunkGenerator(this.getBiomeSource().withSeed(seed), this.getGeneratorSettings(), this.getGeneratorConfig(), seed);
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) { }

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
		BlockPos.Mutable blockPos = new BlockPos.Mutable();
		Heightmap worldSurface = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		Heightmap oceanFloor = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);

		for (int y = 0; y < this.blocks.length; ++y) {
			BlockState blockState = blocks[y].getDefaultState();

			if (blockState != null) {
				for (int x = 0; x < 16; ++x) {
					for (int z = 0; z < 16; ++z) {
						chunk.setBlockState(blockPos.set(x, y, z), blockState, false);
						worldSurface.trackUpdate(x, y, z, blockState);
						oceanFloor.trackUpdate(x, y, z, blockState);
					}
				}
			}
		}
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		return this.worldHeight;
	}

	@Override
	public int getSpawnHeight() {
		return this.worldHeight;
	}

	@Override
	public int getSeaLevel() {
		return this.worldHeight - 1;
	}

	@Override
	public BlockView getColumnSample(int x, int z) {
		return new VerticalBlockSample(Arrays.stream(this.blocks).map(Block::getDefaultState).toArray(BlockState[]::new));
	}
}
