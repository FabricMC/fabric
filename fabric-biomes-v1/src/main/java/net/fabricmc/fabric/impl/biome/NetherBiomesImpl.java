package net.fabricmc.fabric.impl.biome;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.world.biome.Biome;

/**
 * Sets internal use only! Stores data that is used by the Nether dimension mixin.
 */
public class NetherBiomesImpl {
	private NetherBiomesImpl() { }

	private static final Set<Biome> netherBiomes = new HashSet<>();

	public static void addNetherBiome(Biome biome) {
		netherBiomes.add(biome);
	}

	public static Set<Biome> getNetherBiomes() {
		return netherBiomes;
	}
}
