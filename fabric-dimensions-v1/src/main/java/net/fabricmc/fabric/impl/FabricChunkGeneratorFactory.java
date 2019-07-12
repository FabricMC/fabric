package net.fabricmc.fabric.impl;

import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

/**
 * Responsible for creating the FabricChunkGeneratorType's ChunkGenerator instance.
 * Called when a new instance of a ChunkGenerator is requested in the ChunkGeneratorType.
 * @param <C> ChunkGenerator config
 * @param <T> ChunkGenerator
 */
@FunctionalInterface
public interface FabricChunkGeneratorFactory<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> {
	T create(World world, BiomeSource source, C config);
}
