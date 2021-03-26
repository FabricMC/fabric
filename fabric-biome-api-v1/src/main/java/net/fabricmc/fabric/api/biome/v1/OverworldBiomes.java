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

package net.fabricmc.fabric.api.biome.v1;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.biome.InternalBiomeData;

/**
 * API that exposes some internals of the minecraft default biome source for the overworld.
 *
 * <p><b>Experimental feature</b>, may be removed or changed without further notice.
 */
@Deprecated
public final class OverworldBiomes {
	private OverworldBiomes() {
	}

	/**
	 * Adds the biome to the specified climate group, with the specified weight. This is only for the biomes that make up the initial continents in generation.
	 *
	 * @param biome   the biome to be added
	 * @param climate the climate group whereto the biome is added
	 * @param weight  the weight of the entry. The weight in this method corresponds to its selection likelihood, with
	 *                heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
	 * @see OverworldClimate for a list of vanilla biome weights
	 */
	public static void addContinentalBiome(RegistryKey<Biome> biome, OverworldClimate climate, double weight) {
		InternalBiomeData.addOverworldContinentalBiome(climate, biome, weight);
	}

	/**
	 * Adds the biome as a hills variant of the parent biome, with the specified weight.
	 *
	 * @param parent the biome to where the hills variant is added
	 * @param hills  the biome to be set as a hills variant
	 * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
	 *               heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
	 *               Mods should use 1.0 as the default/normal weight.
	 */
	public static void addHillsBiome(RegistryKey<Biome> parent, RegistryKey<Biome> hills, double weight) {
		InternalBiomeData.addOverworldHillsBiome(parent, hills, weight);
	}

	/**
	 * Adds the biome as a shore/beach biome for the parent biome, with the specified weight.
	 *
	 * @param parent the base biome to where the shore biome is added
	 * @param shore  the biome to be added as a shore biome
	 * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
	 *               heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
	 *               Mods should use 1.0 as the default/normal weight.
	 */
	public static void addShoreBiome(RegistryKey<Biome> parent, RegistryKey<Biome> shore, double weight) {
		InternalBiomeData.addOverworldShoreBiome(parent, shore, weight);
	}

	/**
	 * Adds the biome as an an edge biome (excluding as a beach) of the parent biome, with the specified weight.
	 *
	 * @param parent the base biome to where the edge biome is added
	 * @param edge   the biome to be added as an edge biome
	 * @param weight the weight of the entry. The weight in this method corresponds to its selection likelihood, with
	 *               heavier biomes being more likely to be selected and lighter biomes being selected with less likelihood.
	 *               Mods should use 1.0 as the default/normal weight.
	 */
	public static void addEdgeBiome(RegistryKey<Biome> parent, RegistryKey<Biome> edge, double weight) {
		InternalBiomeData.addOverworldEdgeBiome(parent, edge, weight);
	}

	/**
	 * Adds a 'variant' biome which replaces another biome on occasion.
	 *
	 * <p>For example, addBiomeVariant(Biomes.JUNGLE, Biomes.DESERT, 0.2) will replace 20% of jungles with deserts.
	 * This method is rather useful for replacing biomes not generated through standard methods, such as oceans,
	 * deep oceans, jungles, mushroom islands, etc. When replacing ocean and deep ocean biomes, one must specify
	 * the biome without temperature (Biomes.OCEAN / Biomes.DEEP_OCEAN) only, as ocean temperatures have not been
	 * assigned; additionally, one must not specify climates for oceans, deep oceans, or mushroom islands, as they do not have
	 * any climate assigned at this point in the generation.
	 *
	 * @param replaced the base biome that is replaced by a variant
	 * @param variant  the biome to be added as a variant
	 * @param chance   the chance of replacement of the biome into the variant
	 * @param climates the climates in which the variants will occur in (none listed = add variant to all climates)
	 */
	public static void addBiomeVariant(RegistryKey<Biome> replaced, RegistryKey<Biome> variant, double chance, OverworldClimate... climates) {
		InternalBiomeData.addOverworldBiomeReplacement(replaced, variant, chance, climates);
	}

	/**
	 * Sets the river type that will generate in the biome. If null is passed as the river biome, then rivers will not
	 * generate in this biome.
	 *
	 * @param parent the base biome in which the river biome is to be set
	 * @param river  the river biome for this biome
	 */
	public static void setRiverBiome(RegistryKey<Biome> parent, RegistryKey<Biome> river) {
		InternalBiomeData.setOverworldRiverBiome(parent, river);
	}
}
