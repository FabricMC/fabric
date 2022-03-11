package net.fabricmc.fabric.api.tags.v1;

import net.minecraft.tag.TagKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.v1.TagRegistration;

public class CommonBiomeTags {
	public static final TagKey<Biome> IN_OVERWORLD = register("in_overworld");
	// The following are based on Biome categories, see Biome$Category for details
	public static final TagKey<Biome> IN_THE_END = register("in_the_end");
	public static final TagKey<Biome> IN_NETHER = register("in_nether");
	public static final TagKey<Biome> TAIGA = register("taiga");
	public static final TagKey<Biome> EXTREME_HILLS = register("extreme_hills");
	public static final TagKey<Biome> JUNGLE = register("jungle");
	public static final TagKey<Biome> MESA = register("mesa");
	public static final TagKey<Biome> PLAINS = register("plains");
	public static final TagKey<Biome> SAVANNA = register("savanna");
	public static final TagKey<Biome> ICY = register("icy");
	public static final TagKey<Biome> BEACH = register("beach");
	public static final TagKey<Biome> FOREST = register("forest");
	public static final TagKey<Biome> OCEAN = register("ocean");
	public static final TagKey<Biome> DESERT = register("desert");
	public static final TagKey<Biome> RIVER = register("river");
	public static final TagKey<Biome> SWAMP = register("swamp");
	public static final TagKey<Biome> MUSHROOM = register("mushroom");
	public static final TagKey<Biome> UNDERGROUND = register("underground");
	public static final TagKey<Biome> MOUNTAIN = register("mountain");

	public static final TagKey<Biome> CLIMATE_HOT = register("climate_hot");
	public static final TagKey<Biome> CLIMATE_TEMPERATE = register("climate_temperate");
	public static final TagKey<Biome> CLIMATE_COLD = register("climate_cold");
	public static final TagKey<Biome> CLIMATE_WET = register("climate_wet");
	public static final TagKey<Biome> CLIMATE_DRY = register("climate_dry");
	public static final TagKey<Biome> VEGETATION_SPARSE = register("vegetation_sparse");
	public static final TagKey<Biome> VEGETATION_DENSE = register("vegetation_dense");
	public static final TagKey<Biome> TREE_CONIFEROUS = register("tree_coniferous");
	public static final TagKey<Biome> TREE_SAVANNA = register("tree_savanna");
	public static final TagKey<Biome> TREE_JUNGLE = register("tree_jungle");
	public static final TagKey<Biome> TREE_DECIDUOUS = register("tree_deciduous");
	public static final TagKey<Biome> VOID = register("void");
	public static final TagKey<Biome> MOUNTAIN_PEAK = register("mountain_peak");
	public static final TagKey<Biome> MOUNTAIN_SLOPE = register("mountain_slope");
	public static final TagKey<Biome> AQUATIC = register("aquatic");
	public static final TagKey<Biome> WASTELAND = register("wasteland");
	public static final TagKey<Biome> DEAD = register("dead");

	public static final TagKey<Biome> BADLANDS = register("badlands");
	public static final TagKey<Biome> CAVES = register("caves");
	public static final TagKey<Biome> END_ISLANDS = register("end_islands");
	public static final TagKey<Biome> NETHER_FORESTS = register("nether_forests");
	public static final TagKey<Biome> SNOWY_PLAINS = register("snowy_plains");
	public static final TagKey<Biome> STONY_SHORES = register("stony_shores");
	public static final TagKey<Biome> FLOWER_FORESTS = register("flower_forests");

	private static TagKey<Biome> register(String tagID) {
		return TagRegistration.BIOME_TAG_REGISTRATION.registerCommon(tagID);
	}

	private static TagKey<Biome> registerFabric(String tagID) {
		return TagRegistration.BIOME_TAG_REGISTRATION.registerFabric(tagID);
	}
}
