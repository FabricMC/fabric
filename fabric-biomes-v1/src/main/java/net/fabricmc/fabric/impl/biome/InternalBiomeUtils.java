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

import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;

/**
 * Internal utilities used for biome sampling.
 */
public final class InternalBiomeUtils {
	private InternalBiomeUtils() { }

	/**
	 * @param north raw id of the biome to the north
	 * @param east raw id of the biome to the east
	 * @param south raw id of the biome to the south
	 * @param west raw id of the biome to the west
	 * @param center central biome that comparisons are relative to
	 * @return whether the central biome is an edge of a biome
	 */
	public static boolean isEdge(int north, int east, int south, int west, int center) {
		return areUnsimilar(center, north) || areUnsimilar(center, east) || areUnsimilar(center, south) || areUnsimilar(center, west);
	}

	/**
	 * @param mainBiomeId the main raw biome id in comparison
	 * @param secondaryBiomeId the secondary raw biome id in comparison
	 * @return whether the two biomes are unsimilar
	 */
	private static boolean areUnsimilar(int mainBiomeId, int secondaryBiomeId) {
		if (mainBiomeId == secondaryBiomeId) { // for efficiency, determine if the ids are equal first
			return false;
		} else {
			Biome secondaryBiome = Registry.BIOME.get(secondaryBiomeId);
			Biome mainBiome = Registry.BIOME.get(mainBiomeId);

			boolean isUnsimilar = secondaryBiome.hasParent() ? !(mainBiomeId == Registry.BIOME.getRawId(Registry.BIOME.get(new Identifier(secondaryBiome.getParent())))) : true;
			isUnsimilar = isUnsimilar && (mainBiome.hasParent() ? !(secondaryBiomeId == Registry.BIOME.getRawId(Registry.BIOME.get(new Identifier(mainBiome.getParent())))) : true);

			return isUnsimilar;
		}
	}

	/**
	 * @param north raw id of the biome to the north
	 * @param east raw id of the biome to the east
	 * @param south raw id of the biome to the south
	 * @param west raw id of the biome to the west
	 * @return whether a biome in any direction is an ocean around the central biome
	 */
	public static boolean neighborsOcean(int north, int east, int south, int west) {
		return isOceanBiome(north) || isOceanBiome(east) || isOceanBiome(south) || isOceanBiome(west);
	}

	private static boolean isOceanBiome(int id) {
		Biome biome = Registry.BIOME.get(id);
		return biome != null && biome.getCategory() == Biome.Category.OCEAN;
	}

	public static int searchForBiome(double reqWeightSum, int vanillaArrayWeight, List<ContinentalBiomeEntry> moddedBiomes) {
		reqWeightSum -= vanillaArrayWeight;
		int low = 0;
		int high = moddedBiomes.size() - 1;

		while (low < high) {
			int mid = (high + low) >>> 1;

			if (reqWeightSum < moddedBiomes.get(mid).getUpperWeightBound()) {
				high = mid;
			} else {
				low = mid + 1;
			}
		}

		return low;
	}

	/**
	 * Potentially transforms a biome into its variants based on the provided randomness source.
	 *
	 * @param random The randomness source
	 * @param existing The base biome
	 * @param climate The climate in which the biome resides, or null to indicate an unknown climate
	 * @return The potentially transformed biome
	 */
	public static int transformBiome(LayerRandomnessSource random, Biome existing, OverworldClimate climate) {
		Map<Biome, VariantTransformer> overworldVariantTransformers = InternalBiomeData.getOverworldVariantTransformers();
		VariantTransformer transformer = overworldVariantTransformers.get(existing);

		if (transformer != null) {
			return Registry.BIOME.getRawId(transformer.transformBiome(existing, random, climate));
		}

		return Registry.BIOME.getRawId(existing);
	}

	public static void injectBiomesIntoClimate(LayerRandomnessSource random, int[] vanillaArray, OverworldClimate climate, IntConsumer result) {
		WeightedBiomePicker picker = InternalBiomeData.getOverworldModdedContinentalBiomePickers().get(climate);

		if (picker == null || picker.getCurrentWeightTotal() <= 0.0) {
			// Return early, there are no modded biomes.
			// Since we don't pass any values to the IntConsumer, this falls through to vanilla logic.
			// Thus, this prevents Fabric from changing vanilla biome selection behavior without biome mods in this case.

			return;
		}

		int vanillaArrayWeight = vanillaArray.length;
		double reqWeightSum = random.nextInt(Integer.MAX_VALUE) * (vanillaArray.length + picker.getCurrentWeightTotal()) / Integer.MAX_VALUE;

		if (reqWeightSum < vanillaArray.length) {
			// Vanilla biome; look it up from the vanilla array and transform accordingly.

			result.accept(transformBiome(random, Registry.BIOME.get(vanillaArray[(int) reqWeightSum]), climate));
		} else {
			// Modded biome; use a binary search, and then transform accordingly.

			ContinentalBiomeEntry found = picker.search(reqWeightSum - vanillaArrayWeight);

			result.accept(transformBiome(random, found.getBiome(), climate));
		}
	}
}
