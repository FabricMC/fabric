package net.fabricmc.fabric.mixin.biomes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.impl.biomes.BiomeAssociate;
import net.fabricmc.fabric.impl.biomes.BiomeLists;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.AddHillsLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.LayerRandomnessSource;
import net.minecraft.world.biome.layer.LayerSampler;

@Mixin(AddHillsLayer.class)
public class HillsLayerMixin
{
	@Inject(at = @At(value = "HEAD"), method = "sample", cancellable = true)
	private void sample(LayerRandomnessSource rand, LayerSampler biomeSampler, LayerSampler layerSampler_2, int chunkX, int chunkZ,
			CallbackInfoReturnable<Integer> info)
	{
		final int previousBiome = biomeSampler.sample(chunkX, chunkZ);
		
		int int_66 = layerSampler_2.sample(chunkX, chunkZ);

		int int_42 = (int_66 - 2) % 29;

		final Biome prevBiome = Registry.BIOME.get(previousBiome);
		
		if (BiomeLists.HILLS_MAP.containsKey(prevBiome) && (rand.nextInt(3) == 0 || int_42 == 0))
		{
			BiomeAssociate associate = BiomeLists.HILLS_MAP.get(prevBiome);

			int biomeReturn = associate.pickRandomBiome(rand);

			Biome biome_42;
			
			if (int_42 == 0 && biomeReturn != previousBiome)
			{
				biome_42 = Biome.getParentBiome((Biome)Registry.BIOME.get(biomeReturn));
				biomeReturn = biome_42 == null ? previousBiome : Registry.BIOME.getRawId(biome_42);
			}

			if (biomeReturn != previousBiome)
			{
				int int_43 = 0;
				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX, chunkZ - 1), previousBiome))
				{
					++int_43;
				}

				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX + 1, chunkZ), previousBiome))
				{
					++int_43;
				}

				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX - 1, chunkZ), previousBiome))
				{
					++int_43;
				}

				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX, chunkZ + 1), previousBiome))
				{
					++int_43;
				}

				if (int_43 >= 3)
				{
					info.setReturnValue(biomeReturn);
				}
			}
		}
	}
}
