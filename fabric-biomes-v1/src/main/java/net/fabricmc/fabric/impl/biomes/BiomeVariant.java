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

/**
 * A pojo for biome variants and their corrosponding rarities
 */
public class BiomeVariant {

	private Biome variant;
	private int rarity;

	/**
	 * @param variant the variant biome
	 * @param rarity the reciprocal of the chance of replacement (there is a 1/rarity chance)
	 */
	protected BiomeVariant(final Biome variant, final int rarity) {
		this.variant = variant;
		this.rarity = rarity;
	}

	/**
	 * @return The variant biome
	 */
	public Biome getVariant() {
		return variant;
	}

	/**
	 * @return the reciprocal of the chance of replacement (there is a 1/rarity chance)
	 */
	public int getRarity() {
		return rarity;
	}

}
