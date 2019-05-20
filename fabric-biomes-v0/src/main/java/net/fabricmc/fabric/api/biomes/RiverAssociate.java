package net.fabricmc.fabric.api.biomes;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class RiverAssociate
{
	/**
	 *  RiverAssociate with special function. This specifies no river to generate.
	 */
	public static final RiverAssociate NONE = new RiverAssociate(null);
	
	/**
	 *  Normal, default river biome
	 */
	public static final RiverAssociate WATER = new RiverAssociate(Biomes.RIVER);
	
	/**
	 * Frozen river biome
	 */
	public static final RiverAssociate FROZEN = new RiverAssociate(Biomes.FROZEN_RIVER);
	
	private final int biome;
	
	public RiverAssociate(Biome biome)
	{
		this.biome = Registry.BIOME.getRawId(biome);
	}
	
	public int getBiome()
	{
		return biome;
	}
}
