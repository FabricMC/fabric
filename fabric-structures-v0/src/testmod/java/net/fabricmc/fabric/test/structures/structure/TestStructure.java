package net.fabricmc.fabric.test.structures.structure;

import net.fabricmc.fabric.api.structures.v0.FabricStructure;
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

/**
 *
 */
public class TestStructure extends FabricStructure<DefaultFeatureConfig> {
	public TestStructure() {
		super(DefaultFeatureConfig.CODEC);
	}

	@Override
	public GenerationStep.Feature generationStep() {
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
