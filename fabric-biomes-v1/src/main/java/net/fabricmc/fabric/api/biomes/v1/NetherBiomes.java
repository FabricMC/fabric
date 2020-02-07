package net.fabricmc.fabric.api.biomes.v1;

import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.biome.NetherBiomesImpl;

/**
 * API that exposes internals' of Minecraft's nether biome code.
 */
public class NetherBiomes {
	private NetherBiomes() { }

	/**
	 * Adds a biome to the Nether generator. Biomes must set their own noise generation values in the biome settings class.
	 *
	 * @param biome The biome to add.
	 */
	public static void addNetherBiome(Biome biome) {
		NetherBiomesImpl.addNetherBiome(biome);
	}
}
