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

package net.fabricmc.fabric.mixin.biome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.AddHillsLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.impl.biome.InternalBiomeData;
import net.fabricmc.fabric.impl.biome.WeightedBiomePicker;

/**
 * Injects hills biomes specified from {@link OverworldBiomes#addHillsBiome(Biome, Biome, double)}into the default hills layer.
 */
@Mixin(AddHillsLayer.class)
public class MixinAddHillsLayer {
	@Inject(at = @At("HEAD"), method = "sample", cancellable = true)
	private void sample(LayerRandomnessSource rand, LayerSampler biomeSampler, LayerSampler noiseSampler, int chunkX, int chunkZ, CallbackInfoReturnable<Integer> info) {
		if (InternalBiomeData.getOverworldHills().isEmpty()) {
			// No use doing anything if there are no hills registered. Fall through to vanilla logic.
			return;
		}

		final int biomeId = biomeSampler.sample(chunkX, chunkZ);
		int noiseSample = noiseSampler.sample(chunkX, chunkZ);
		int processedNoiseSample = (noiseSample - 2) % 29;
		final Biome biome = Registry.BIOME.get(biomeId);

		WeightedBiomePicker hillPicker = InternalBiomeData.getOverworldHills().get(biome);

		if (hillPicker == null) {
			// No hills for this biome, fall through to vanilla logic.

			return;
		}

		if (rand.nextInt(3) == 0 || processedNoiseSample == 0) {
			int biomeReturn = Registry.BIOME.getRawId(hillPicker.pickRandom(rand));
			Biome parent;

			if (processedNoiseSample == 0 && biomeReturn != biomeId) {
				parent = Biome.getModifiedBiome(Registry.BIOME.get(biomeReturn));
				biomeReturn = parent == null ? biomeId : Registry.BIOME.getRawId(parent);
			}

			if (biomeReturn != biomeId) {
				int similarity = 0;

				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX, chunkZ - 1), biomeId)) {
					++similarity;
				}

				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX + 1, chunkZ), biomeId)) {
					++similarity;
				}

				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX - 1, chunkZ), biomeId)) {
					++similarity;
				}

				if (BiomeLayers.areSimilar(biomeSampler.sample(chunkX, chunkZ + 1), biomeId)) {
					++similarity;
				}

				if (similarity >= 3) {
					info.setReturnValue(biomeReturn);
					return;
				}
			}
		}

		// Cancel vanilla logic.
		info.setReturnValue(biomeId);
	}
}
