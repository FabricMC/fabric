package net.fabricmc.fabric.test.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class VoidChunkGenerator extends ChunkGenerator {
	// Just an example of adding a custom boolean
	protected final boolean customBool;

	public static final Codec<VoidChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
			instance.group(
					BiomeSource.field_24713.fieldOf("biome_source")
							.forGetter((generator) -> generator.biomeSource),
					Codec.BOOL.fieldOf("custom_bool")
							.forGetter((generator) -> generator.customBool)
			)
			.apply(instance, instance.stable(VoidChunkGenerator::new))
	);

	public VoidChunkGenerator(BiomeSource biomeSource, boolean customBool) {
		super(biomeSource, new StructuresConfig(false));
		this.customBool = customBool;
	}

	@Override
	protected Codec<? extends ChunkGenerator> method_28506() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return this;
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		return 0;
	}

	@Override
	public BlockView getColumnSample(int x, int z) {
		return new VerticalBlockSample(new BlockState[0]);
	}
}
