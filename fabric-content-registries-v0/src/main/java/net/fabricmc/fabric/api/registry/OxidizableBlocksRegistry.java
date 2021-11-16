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

import java.util.Optional;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;

import net.fabricmc.fabric.api.util.OxidizableFamily;

public class OxidizableBlocksRegistry {
	private static final BiMap<Block, Block> OXIDIZATION_LEVEL_INCREASES = HashBiMap.create();
	private static final BiMap<Block, Block> OXIDIZATION_LEVEL_DECREASES = HashBiMap.create();

	static {
		// Put all vanilla entries in here, just in case!
		OXIDIZATION_LEVEL_INCREASES.putAll(Oxidizable.OXIDATION_LEVEL_INCREASES.get());
		OXIDIZATION_LEVEL_DECREASES.putAll(Oxidizable.OXIDATION_LEVEL_DECREASES.get());
	}

	/**
	 * Registers multiple {@link OxidizableFamily}s.
	 *
	 * @param families the families to register
	 */
	public static void registerFamilies(OxidizableFamily... families) {
		for (OxidizableFamily family : families) {
			registerFamily(family);
		}
	}

	/**
	 * Registers multiple {@link OxidizableFamily}s.
	 *
	 * @param families the families to register
	 */
	public static void registerFamilies(Iterable<OxidizableFamily> families) {
		for (OxidizableFamily family : families) {
			registerFamily(family);
		}
	}

	/**
	 * Registers an {@link OxidizableFamily}.
	 *
	 * @param family the {@link OxidizableFamily} to register
	 */
	public static void registerFamily(OxidizableFamily family) {
		OXIDIZATION_LEVEL_INCREASES.putAll(family.oxidizationLevelIncreasesMap());
		OXIDIZATION_LEVEL_DECREASES.putAll(family.oxidizationLevelDecreasesMap());
		WaxableBlocksRegistry.registerWaxablePairs(family.waxableBlockPairs());
	}

	/**
	 * Gets the map of {@link Oxidizable.OxidizationLevel} increases.
	 *
	 * @return the map
	 */
	public static BiMap<Block, Block> getOxidizationLevelIncreases() {
		return OXIDIZATION_LEVEL_INCREASES;
	}

	/**
	 * Gets the map of {@link Oxidizable.OxidizationLevel} decreases.
	 *
	 * @return the map
	 */
	public static BiMap<Block, Block> getOxidizationLevelDecreases() {
		return OXIDIZATION_LEVEL_DECREASES;
	}

	/**
	 * Gets an Optional of the {@link Block} that would result from a decrease in {@link Oxidizable.OxidizationLevel} from the given block.
	 * If no such block exists, the Optional will be empty.
	 *
	 * @param block the block whose oxidization is to be decreased
	 * @return the block with decreased oxidization, in an optional
	 */
	public static Optional<Block> getDecreasedOxidizationBlock(Block block) {
		return Optional.ofNullable(getOxidizationLevelDecreases().get(block));
	}

	/**
	 * Gets an Optional of the {@link BlockState} that would result from a decrease in {@link Oxidizable.OxidizationLevel} from the given state.
	 * If no such state exists, the Optional will be empty.
	 *
	 * @param state the state whose oxidization is to be decreased
	 * @return the state with decreased oxidization, in an optional
	 */
	public static Optional<BlockState> getDecreasedOxidizationState(BlockState state) {
		return getDecreasedOxidizationBlock(state.getBlock()).map((block) -> block.getStateWithProperties(state));
	}

	/**
	 * Gets an Optional of the {@link Block} that would result from an increase in {@link Oxidizable.OxidizationLevel} from the given block.
	 * If no such block exists, the Optional will be empty.
	 *
	 * @param block the block whose oxidization is to be increased
	 * @return the block with increased oxidization, in an optional
	 */
	public static Optional<Block> getIncreasedOxidizationBlock(Block block) {
		return Optional.ofNullable(getOxidizationLevelIncreases().get(block));
	}

	/**
	 * Gets an Optional of the {@link BlockState} that would result from an increase in {@link Oxidizable.OxidizationLevel} from the given state.
	 * If no such state exists, the Optional will be empty.
	 *
	 * @param state the state whose oxidization is to be increased
	 * @return the state with increased oxidization, in an optional
	 */
	public static Optional<BlockState> getIncreasedOxidizationState(BlockState state) {
		return getIncreasedOxidizationBlock(state.getBlock()).map((block) -> block.getStateWithProperties(state));
	}

	/**
	 * Gets a form of the given {@link Block} with no oxidization.
	 *
	 * @param block the block
	 * @return the block without oxidization
	 */
	public static Block getUnaffectedOxidizationBlock(Block block) {
		Block block2 = block;

		for (Block block3 = getOxidizationLevelDecreases().get(block); block3 != null; block3 = getOxidizationLevelDecreases().get(block3)) {
			block2 = block3;
		}

		return block2;
	}

	/**
	 * Gets a form of the given {@link BlockState} with no oxidization.
	 *
	 * @param state the state
	 * @return the state without oxidization
	 */
	public static BlockState getUnaffectedOxidizationState(BlockState state) {
		return getUnaffectedOxidizationBlock(state.getBlock()).getStateWithProperties(state);
	}
}
