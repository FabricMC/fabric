package net.fabricmc.fabric.mixin.dimension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(DimensionOptions.class)
public interface DimensionOptionsAccessor {
	@Accessor("chunkGenerator")
	void fabric_setChunkGenerator(ChunkGenerator chunkGenerator);
}
