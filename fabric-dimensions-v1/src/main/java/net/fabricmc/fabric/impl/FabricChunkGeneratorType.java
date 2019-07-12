package net.fabricmc.fabric.impl;

import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

import java.util.function.Supplier;

public class FabricChunkGeneratorType<C extends ChunkGeneratorConfig, T extends ChunkGenerator<C>> extends ChunkGeneratorType<C, T>
{
	private FabricChunkGeneratorFactory<C, T> factory;

	public FabricChunkGeneratorType(FabricChunkGeneratorFactory<C, T> factory, boolean buffetScreenOption, Supplier settingsSupplier)
	{
		super(null, buffetScreenOption, settingsSupplier);
		this.factory = factory;
	}

	@Override
	public T create(World world, BiomeSource biomeSource, C config)
	{
		return factory.create(world, biomeSource, config);
	}
}
