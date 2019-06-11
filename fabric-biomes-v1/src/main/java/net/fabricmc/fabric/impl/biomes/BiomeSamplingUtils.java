package net.fabricmc.fabric.impl.biomes;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public final class BiomeSamplingUtils
{
	private BiomeSamplingUtils() {}
	
	public static boolean isEdge(int north, int east, int south, int west, int biome)
	{
		return north != biome || east != biome || south != biome || west != biome;
	}

	public static boolean isShore(int north, int east, int south, int west)
	{
		return isOceanBiome(north) || isOceanBiome(east) || isOceanBiome(south) || isOceanBiome(west);
	}
	
	private static boolean isOceanBiome(int id)
	{
		Biome biome = Registry.BIOME.get(id);
		return biome != null && biome.getCategory() == Biome.Category.OCEAN;
	}
}
