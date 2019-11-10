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
 * Represents the climates of biomes on the overworld continents.
 */
public enum OverworldClimate {
	/**
	 * Includes Snowy Tundra (with a weight of 3) and Snowy Taiga (with a weight of 1).
	 */
	SNOWY,

	/**
	 * Includes Forest, Taiga, Mountains, and Plains (all with weights of 1).
	 */
	COOL,

	/**
	 * Includes Forest, Dark Forest, Mountains, Plains, Birch Forest, and Swamp (all with weights of 1).
	 */
	TEMPERATE,

	/**
	 * Includes Desert (with a weight of 3), Savanna (with a weight of 2), and Plains (with a weight of 1).
	 */
	DRY
}
