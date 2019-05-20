package net.fabricmc.fabric.impl.biomes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

public class BiomeAssociate
{
	private int weightSum = 0;
	private List<Integer> biomes = new ArrayList<>();
	
	public void addBiomeWithWeight(Biome biome, int weight)
	{
		this.weightSum += weight;
		int b = Registry.BIOME.getRawId(biome);
		
		for (int i = 0; i < weight; ++i)
			biomes.add(b);
	}
	
	public int pickRandomBiome(LayerRandomnessSource rand)
	{
		int b = biomes.get(rand.nextInt(weightSum));
		
		return b;
	}
}