package net.fabricmc.fabric.impl.biomes;

import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

/**
 * A pojo for biome & weight
 */
public class BiomeEntry {

	public Biome biome;
	public double weight;
	public double upperWeightBound;

	/**
	 * @param biome the biome
	 * @param weight how often a biome will be chosen. Most vanilla biomes are a frequency of 1.
	 * @param climate the climate of the biome entry, just used to store weights
	 */
	public BiomeEntry(final Biome biome, final double weight, OverworldClimate climate) {
		this.biome = biome;
		this.weight = weight;
		InternalBiomeData.OVERWORLD_MODDED_WEIGHT_TOTALS.computeIfPresent(climate, (mapClimate, mapWeight) -> mapWeight + weight);
		InternalBiomeData.OVERWORLD_MODDED_WEIGHT_TOTALS.putIfAbsent(climate, weight);
		upperWeightBound = InternalBiomeData.OVERWORLD_MODDED_WEIGHT_TOTALS.get(climate);
	}

	/**
	 * @return the biome
	 */
	public Biome getBiome() {
		return biome;
	}

	/**
	 * @return the
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @return The raw id for the biome
	 */
	public int getRawId() {
		return Registry.BIOME.getRawId(biome);
	}

}
