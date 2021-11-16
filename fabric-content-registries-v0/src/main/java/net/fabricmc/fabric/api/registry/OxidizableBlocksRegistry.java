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

package net.fabricmc.fabric.api.registry;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.Oxidizable;

public class OxidizableBlocksRegistry {
	/**
	 * Registers a block pair as being able to increase and decrease oxidization.
	 *
	 * @param less the variant with less oxidization
	 * @param more the variant with more oxidization
	 * @see #registerOxidizationLevelIncrease(Block, Block)
	 * @see #registerOxidizationLevelDecrease(Block, Block)
	 */
	public static void registerOxidizableBlockPair(Block less, Block more) {
		registerOxidizationLevelIncrease(less, more);
		registerOxidizationLevelDecrease(more, less);
	}

	/**
	 * Registers a block pair as being able to increase oxidization.
	 *
	 * @param original the original variant
	 * @param increased the increased oxidization variant
	 * @see #registerOxidizationLevelDecrease(Block, Block)
	 * @see #registerOxidizableBlockPair(Block, Block)
	 */
	public static void registerOxidizationLevelIncrease(Block original, Block increased) {
		Objects.requireNonNull(original, "Oxidizable block cannot be null!");
		Objects.requireNonNull(increased, "Oxidizable block cannot be null!");
		Oxidizable.OXIDATION_LEVEL_INCREASES.get().put(original, increased);
	}

	/**
	 * Registers a block pair as being able to decrease oxidization.
	 *
	 * @param original the original variant
	 * @param decreased the decreased oxidization variant
	 * @see #registerOxidizationLevelIncrease(Block, Block)
	 * @see #registerOxidizableBlockPair(Block, Block)
	 */
	public static void registerOxidizationLevelDecrease(Block original, Block decreased) {
		Objects.requireNonNull(original, "Oxidizable block cannot be null!");
		Objects.requireNonNull(decreased, "Oxidizable block cannot be null!");
		Oxidizable.OXIDATION_LEVEL_DECREASES.get().put(original, decreased);
	}
}
