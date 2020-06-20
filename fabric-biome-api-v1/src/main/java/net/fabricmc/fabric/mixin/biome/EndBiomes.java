package net.fabricmc.fabric.api.biomes.v1;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;
import net.minecraft.world.biome.Biome;

public final class EndBiomes {
	public static void addBiome(Biome biome, EndRegion region, double weight) {
		InternalBiomeData.addEndBiome(biome, region, weight);
	}
}
