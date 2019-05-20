/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
