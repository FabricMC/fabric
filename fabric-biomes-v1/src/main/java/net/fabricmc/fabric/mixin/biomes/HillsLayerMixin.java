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

import net.fabricmc.fabric.impl.biomes.WeightedBiomePicker;
import net.fabricmc.fabric.impl.biomes.InternalBiomeData;
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
	private void sample(LayerRandomnessSource rand, LayerSampler biomeSampler, LayerSampler noiseLayerSampler, int chunkX, int chunkZ,
			CallbackInfoReturnable<Integer> info)
	{
		final int fabric_biomeId = biomeSampler.sample(chunkX, chunkZ);
		
		int fabric_noiseSample = noiseLayerSampler.sample(chunkX, chunkZ);

		int fabric_processedNoiseSample = (fabric_noiseSample - 2) % 29;
		
		final Biome fabric_biome = Registry.BIOME.get(fabric_biomeId);
		
		if (InternalBiomeData.HILLS_MAP.containsKey(fabric_biome) && (rand.nextInt(3) == 0 || fabric_processedNoiseSample == 0))
		{
			WeightedBiomePicker biomePicker = InternalBiomeData.HILLS_MAP.get(fabric_biome);

			int biomeReturn = biomePicker.pickRandom(rand);

			Biome parent;
			
			if (fabric_processedNoiseSample == 0 && biomeReturn != fabric_biomeId)
			{
				parent = Biome.getParentBiome((Biome)Registry.BIOME.get(biomeReturn));
				biomeReturn = parent == null ? fabric_biomeId : Registry.BIOME.getRawId(parent);
			}

			if (biomeReturn != fabric_biomeId)
			{
				int similarity = 0;
				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX, chunkZ - 1), fabric_biomeId))
				{
					++similarity;
				}

				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX + 1, chunkZ), fabric_biomeId))
				{
					++similarity;
				}

				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX - 1, chunkZ), fabric_biomeId))
				{
					++similarity;
				}

				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX, chunkZ + 1), fabric_biomeId))
				{
					++similarity;
				}

				if (similarity >= 3)
				{
					info.setReturnValue(biomeReturn);
				}
			}
		}
	}
}
