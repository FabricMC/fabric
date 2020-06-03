package net.fabricmc.fabric.test;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biomes.v1.NetherBiomes;
import net.fabricmc.fabric.test.biome.TestCrimsonForestBiome;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;

public class FabricBiomeTest implements ModInitializer
{
	public static final String MOD_ID = "fabric-biome-api-v1-testmod";

	@Override public void onInitialize()
	{
		TestCrimsonForestBiome biome = Registry.register(Registry.BIOME, new Identifier(MOD_ID, "test_crimson_forest"), new TestCrimsonForestBiome());
		NetherBiomes.addNetherBiome(Biomes.BEACH);
		NetherBiomes.addNetherBiome(biome);
	}
}
