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

package net.fabricmc.fabric.impl.biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import net.fabricmc.fabric.api.biome.v1.OverworldClimate;

/**
 * Deals with picking variants for you.
 */
final class VariantTransformer {
	private final SubTransformer defaultTransformer = new SubTransformer();
	private final Map<OverworldClimate, SubTransformer> transformers = new HashMap<>();

	/**
	 * @param variant the variant that the replaced biome is replaced with
	 * @param chance the chance of replacement of the biome into the variant
	 * @param climates the climates that the variant can replace the base biome in, empty/null indicates all climates
	 */
	void addBiome(RegistryKey<Biome> variant, double chance, OverworldClimate[] climates) {
		if (climates == null || climates.length == 0) {
			defaultTransformer.addBiome(variant, chance);
			climates = OverworldClimate.values();
		}

		for (OverworldClimate climate : climates) {
			transformers.computeIfAbsent(climate, c -> new SubTransformer()).addBiome(variant, chance);
		}
	}

	/**
	 * Transforms a biome into a variant randomly depending on its chance.
	 *
	 * @param replaced biome to transform
	 * @param random the {@link LayerRandomnessSource} from the layer
	 * @return the transformed biome
	 */
	RegistryKey<Biome> transformBiome(RegistryKey<Biome> replaced, LayerRandomnessSource random, OverworldClimate climate) {
		if (climate == null) {
			return defaultTransformer.transformBiome(replaced, random);
		}

		SubTransformer transformer = transformers.get(climate);

		if (transformer != null) {
			return transformer.transformBiome(replaced, random);
		} else {
			return replaced;
		}
	}

	static final class SubTransformer {
		private final List<BiomeVariant> variants = new ArrayList<>();

		/**
		 * @param variant the variant that the replaced biome is replaced with
		 * @param chance the chance of replacement of the biome into the variant
		 */
		private void addBiome(RegistryKey<Biome> variant, double chance) {
			variants.add(new BiomeVariant(variant, chance));
		}

		/**
		 * Transforms a biome into a variant randomly depending on its chance.
		 *
		 * @param replaced biome to transform
		 * @param random the {@link LayerRandomnessSource} from the layer
		 * @return the transformed biome
		 */
		private RegistryKey<Biome> transformBiome(RegistryKey<Biome> replaced, LayerRandomnessSource random) {
			for (BiomeVariant variant : variants) {
				if (random.nextInt(Integer.MAX_VALUE) < variant.getChance() * Integer.MAX_VALUE) {
					return variant.getVariant();
				}
			}

			return replaced;
		}
	}
}
