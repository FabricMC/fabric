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

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.List;

/**
 * Internal utilities used for biome sampling
 */
public final class InternalBiomeUtils {

	private InternalBiomeUtils() {
	}

	/**
	 * @param north raw id of the biome to the north
	 * @param east raw id of the biome to the east
	 * @param south raw id of the biome to the south
	 * @param west raw id of the biome to the west
	 * @param center central biome that comparisons are relative to
	 * @return whether the central biome is an edge of a biome
	 */
	public static boolean isEdge(int north, int east, int south, int west, int center) {
		return north != center || east != center || south != center || west != center;
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

	public static int searchForBiome(double reqWeightSum, int vanillaArrayWeight, List<BaseBiomeEntry> moddedBiomes) {
		reqWeightSum -= vanillaArrayWeight;
		int low = 0;
		int high = moddedBiomes.size() - 1;
		while (low < high) {
			int mid = (high + low) >>> 1;
			if (reqWeightSum < moddedBiomes.get(mid).getUpperWeightBound()) {
				high = mid;
			}
			else {
				low = mid + 1;
			}
		}
		return low;
	}

}
