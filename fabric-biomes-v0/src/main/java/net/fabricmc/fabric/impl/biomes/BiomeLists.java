package net.fabricmc.fabric.impl.biomes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fabricmc.fabric.api.biomes.BiomeClimate;
import net.fabricmc.fabric.api.biomes.RiverAssociate;
import net.minecraft.util.Pair;
import net.minecraft.world.biome.Biome;

public class BiomeLists
{
	public static final Map<Biome, BiomeAssociate> HILLS_MAP = new HashMap<>();
	public static final Map<Biome, BiomeAssociate> SHORE_MAP = new HashMap<>();
	public static final Map<Biome, BiomeAssociate> EDGE_MAP = new HashMap<>();
	public static final Map<Biome, RiverAssociate> RIVER_MAP = new HashMap<>();
	
	public static final List<Pair<Integer, BiomeClimate>> INJECTED_BIOME_LIST = new ArrayList<>();
	public static final Set<Biome> CUSTOM_BIOMES = new HashSet<>();
	
	public static final Set<Biome> SPAWN_BIOMES = new HashSet<>();
	
	public static void addCustomBiome(Biome biome)
	{
		CUSTOM_BIOMES.add(biome);
	}
}
