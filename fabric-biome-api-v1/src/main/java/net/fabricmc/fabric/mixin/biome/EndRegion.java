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

package net.fabricmc.fabric.api.biomes.v1;

/**
 * Represents the different regions of the biomes of the end.
 */
public enum EndRegion {
	/**
	 * Corresponds to the central end island and the surrounding empty space.
	 */
	MAIN_ISLAND,
	/**
	 * Corresponds to the End Highlands biome.
	 */
	HIGHLANDS,
	/**
	 * Corresponds to the End Midlands biome.
	 */
	MIDLANDS,
	/**
	 * Corresponds to the End Barrens biome.
	 */
	BARRENS,
	/**
	 * Corresponds to the Small End Islands biome.
	 */
	SMALL_ISLANDS
}
