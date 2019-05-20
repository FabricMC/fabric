package net.fabricmc.fabric.api.biomes;

import static net.fabricmc.fabric.impl.biomes.BiomeLists.EDGE_MAP;
import static net.fabricmc.fabric.impl.biomes.BiomeLists.HILLS_MAP;
import static net.fabricmc.fabric.impl.biomes.BiomeLists.INJECTED_BIOME_LIST;
import static net.fabricmc.fabric.impl.biomes.BiomeLists.RIVER_MAP;
import static net.fabricmc.fabric.impl.biomes.BiomeLists.SHORE_MAP;

import net.fabricmc.fabric.impl.biomes.BiomeAssociate;
import net.fabricmc.fabric.impl.biomes.BiomeLists;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class BiomeManager
{
	/**
	 * Adds the biome to the specified climate group
	 * 
	 * @param biome
	 * @param climate
	 */
	public static void addBiomeToClimate(Biome biome, BiomeClimate climate)
	{
		INJECTED_BIOME_LIST.add(new Pair<Integer, BiomeClimate>(Registry.BIOME.getRawId(biome), climate));
		
		BiomeLists.addCustomBiome(biome);
	}
	
	/**
	 * Adds the biome hillsBiome as a hills variant of baseBiome, with the specified weight
	 * 
	 * @param baseBiome
	 * @param hillsBiome
	 * @param weight
	 */
	public static void addHillsBiome(Biome baseBiome, Biome hillsBiome, int weight)
	{
		if (!HILLS_MAP.containsKey(baseBiome))
			HILLS_MAP.put(baseBiome, new BiomeAssociate());
		
		HILLS_MAP.get(baseBiome).addBiomeWithWeight(hillsBiome, weight);
		
		BiomeLists.addCustomBiome(hillsBiome);
	}
	
	/**
	 * Adds the biome shoreBiome as the beach biome for baseBiome, with the specified weight
	 * 
	 * @param baseBiome
	 * @param shoreBiome
	 * @param weight
	 */
	public static void addShoreBiome(Biome baseBiome, Biome shoreBiome, int weight)
	{
		if (!SHORE_MAP.containsKey(baseBiome))
			SHORE_MAP.put(baseBiome, new BiomeAssociate());
		
		SHORE_MAP.get(baseBiome).addBiomeWithWeight(shoreBiome, weight);
		
		BiomeLists.addCustomBiome(shoreBiome);
	}
	
	/**
	 * Adds the biome edgeBiome as the edge biome (excluding as a beach) of the biome baseBiome, with the specified weight
	 * 
	 * @param baseBiome
	 * @param edgeBiome
	 * @param weight
	 */
	public static void addEdgeBiome(Biome baseBiome, Biome edgeBiome, int weight)
	{
		if (!EDGE_MAP.containsKey(baseBiome))
			EDGE_MAP.put(baseBiome, new BiomeAssociate());
		
		EDGE_MAP.get(baseBiome).addBiomeWithWeight(edgeBiome, weight);
		
		BiomeLists.addCustomBiome(edgeBiome);
	}
	
	/**
	 * Sets the river type that will generate in the biome
	 * 
	 * @param baseBiome
	 * @param riverType
	 */
	public static void setRiverBiome(Biome baseBiome, RiverAssociate riverType)
	{
		RIVER_MAP.put(baseBiome, riverType);
		
		BiomeLists.addCustomBiome(Registry.BIOME.get(riverType.getBiome()));
	}
	
	/**
	 * Adds the biome to spawn biomes, so that the player may spawn in the biome
	 * 
	 * @param biome
	 */
	public static void addSpawnBiome(Biome biome)
	{
		BiomeLists.SPAWN_BIOMES.add(biome);
	}
}
