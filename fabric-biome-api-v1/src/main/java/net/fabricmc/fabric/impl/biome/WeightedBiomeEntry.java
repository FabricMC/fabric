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

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

/**
 * Represents a biome and its corresponding weight.
 */
<<<<<<< HEAD
<<<<<<< HEAD:fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/impl/biome/WeightedBiomeEntry.java
=======
>>>>>>> 7c36835a... Revert "Revert "Rename ContinentalBiomeEntry to WeightedBiomeEntry""
<<<<<<< HEAD:fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/impl/biome/ContinentalBiomeEntry.java
final class ContinentalBiomeEntry {
	private final RegistryKey<Biome> biome;
=======
final class WeightedBiomeEntry {
<<<<<<< HEAD
=======
final class ContinentalBiomeEntry {
>>>>>>> c2aa4ab0... Revert "Rename ContinentalBiomeEntry to WeightedBiomeEntry":fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/impl/biome/ContinentalBiomeEntry.java
	private final Biome biome;
>>>>>>> 849197e1... Rename ContinentalBiomeEntry to WeightedBiomeEntry:fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/impl/biome/WeightedBiomeEntry.java
=======
	private final Biome biome;
>>>>>>> 7c36835a... Revert "Revert "Rename ContinentalBiomeEntry to WeightedBiomeEntry"":fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/impl/biome/WeightedBiomeEntry.java
>>>>>>> 7c36835a... Revert "Revert "Rename ContinentalBiomeEntry to WeightedBiomeEntry""
	private final double weight;
	private final double upperWeightBound;

	/**
	 * @param biome the biome
	 * @param weight how often a biome will be chosen
	 * @param upperWeightBound the upper weight bound within the context of the other entries, used for the binary search
	 */
<<<<<<< HEAD
<<<<<<< HEAD:fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/impl/biome/WeightedBiomeEntry.java
=======
>>>>>>> 7c36835a... Revert "Revert "Rename ContinentalBiomeEntry to WeightedBiomeEntry""
<<<<<<< HEAD:fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/impl/biome/ContinentalBiomeEntry.java
	ContinentalBiomeEntry(final RegistryKey<Biome> biome, final double weight, final double upperWeightBound) {
=======
	WeightedBiomeEntry(final Biome biome, final double weight, final double upperWeightBound) {
<<<<<<< HEAD
>>>>>>> 849197e1... Rename ContinentalBiomeEntry to WeightedBiomeEntry:fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/impl/biome/WeightedBiomeEntry.java
=======
	ContinentalBiomeEntry(final Biome biome, final double weight, final double upperWeightBound) {
>>>>>>> c2aa4ab0... Revert "Rename ContinentalBiomeEntry to WeightedBiomeEntry":fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/impl/biome/ContinentalBiomeEntry.java
=======
>>>>>>> 7c36835a... Revert "Revert "Rename ContinentalBiomeEntry to WeightedBiomeEntry"":fabric-biome-api-v1/src/main/java/net/fabricmc/fabric/impl/biome/WeightedBiomeEntry.java
>>>>>>> 7c36835a... Revert "Revert "Rename ContinentalBiomeEntry to WeightedBiomeEntry""
		this.biome = biome;
		this.weight = weight;
		this.upperWeightBound = upperWeightBound;
	}

	RegistryKey<Biome> getBiome() {
		return biome;
	}

	double getWeight() {
		return weight;
	}

	/**
	 * @return the upper weight boundary for the search
	 */
	double getUpperWeightBound() {
		return upperWeightBound;
	}
<<<<<<< HEAD
}
=======
}
>>>>>>> 7c36835a... Revert "Revert "Rename ContinentalBiomeEntry to WeightedBiomeEntry""
