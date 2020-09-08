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

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.layer.AddHillsLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.impl.biome.InternalBiomeData;
import net.fabricmc.fabric.impl.biome.InternalBiomeUtils;
import net.fabricmc.fabric.impl.biome.WeightedBiomePicker;

/**
 * Injects hills biomes specified from {@link OverworldBiomes#addHillsBiome(RegistryKey, RegistryKey, double)} into the default hills layer.
 */
@Mixin(AddHillsLayer.class)
public class MixinAddHillsLayer {
	// This maps between from a biome to it's "modified variant" biome, which was previously modeled via parent biomes
	@Shadow
	private static Int2IntMap field_26727;

	@Inject(at = @At("HEAD"), method = "sample", cancellable = true)
	private void sample(LayerRandomnessSource rand, LayerSampler biomeSampler, LayerSampler noiseSampler, int chunkX, int chunkZ, CallbackInfoReturnable<Integer> info) {
		if (InternalBiomeData.getOverworldHills().isEmpty()) {
			// No use doing anything if there are no hills registered. Fall through to vanilla logic.
			return;
		}

		final int biomeId = biomeSampler.sample(chunkX, chunkZ);
		int noiseSample = noiseSampler.sample(chunkX, chunkZ);
		int processedNoiseSample = (noiseSample - 2) % 29;
		RegistryKey<Biome> key = BuiltinBiomes.fromRawId(biomeId);

		if (key == null) {
			throw new IllegalStateException("Biome sampler returned unregistered Biome ID: " + biomeId);
		}

		WeightedBiomePicker hillPicker = InternalBiomeData.getOverworldHills().get(key);

		if (hillPicker == null) {
			// No hills for this biome, fall through to vanilla logic.

			return;
		}

		if (rand.nextInt(3) == 0 || processedNoiseSample == 0) {
			int biomeReturn = InternalBiomeUtils.getRawId(hillPicker.pickRandom(rand));

			if (processedNoiseSample == 0 && biomeReturn != biomeId) {
				biomeReturn = field_26727.getOrDefault(biomeReturn, biomeId);
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
