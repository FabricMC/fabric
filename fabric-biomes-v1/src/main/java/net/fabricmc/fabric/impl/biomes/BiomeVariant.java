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
 * Represents a biome variant and its corresponding rarity
 */
public class BiomeVariant {

	private final Biome variant;
	private final double rarity;

	/**
	 * @param variant the variant biome
	 * @param rarity the rarity of the biome variant
	 */
	protected BiomeVariant(final Biome variant, final double rarity) {
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
	 * @return The rarity of the biome variant
	 */
	public double getRarity() {
		return rarity;
	}

}
