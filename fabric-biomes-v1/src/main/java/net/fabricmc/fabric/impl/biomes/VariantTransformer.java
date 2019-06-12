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

package net.fabricmc.fabric.impl.biomes;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Deals with picking variants for you
 */
public final class VariantTransformer {

	private final List<BiomeVariant> variants = new ArrayList<>();

	/**
	 * @param variant the variant that the replaced biome is replaced with
	 * @param rarity the reciprocal of the chance of replacement (there is a 1/rarity chance)
	 */
	public void addBiome(Biome variant, int rarity) {
		variants.add(new BiomeVariant(variant, rarity));
	}

	/**
	 * Transforms a biome into a variant randomly depening on rarity
	 *
	 * @param relaced biome to transform
	 * @param random the {@link LayerRandomnessSource} from the layer
	 * @return the transformed biome
	 */
	public Biome transformBiome(Biome relaced, LayerRandomnessSource random) {
		for (BiomeVariant variant : variants) {
			if (random.nextInt(variant.getRarity()) == 0) {
				return variant.getVariant();
			}
		}
		return relaced;
	}

}
