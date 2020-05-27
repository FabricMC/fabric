package net.fabricmc.fabric.api.dimension.v1;

import net.minecraft.world.gen.chunk.ChunkGenerator;

@FunctionalInterface
public interface ChunkGeneratorFactory {

	/**
	 * Create a {@link ChunkGenerator} given the seed.
	 *
	 * @param seed the seed of the server
	 * @return a {@link ChunkGenerator}
	 */
	ChunkGenerator create(long seed);
}
