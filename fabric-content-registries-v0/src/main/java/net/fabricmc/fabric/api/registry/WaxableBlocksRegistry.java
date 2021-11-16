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
import net.minecraft.item.HoneycombItem;

import net.fabricmc.fabric.api.util.OxidizableFamily;
import net.fabricmc.fabric.api.util.WaxableBlockPair;

public class WaxableBlocksRegistry {
	private static final BiMap<Block, Block> UNWAXED_TO_WAXED_BLOCKS = HashBiMap.create();
	private static final BiMap<Block, Block> WAXED_TO_UNWAXED_BLOCKS = HashBiMap.create();

	static {
		// Put all vanilla entries in here, just in case!
		UNWAXED_TO_WAXED_BLOCKS.putAll(HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get());
		WAXED_TO_UNWAXED_BLOCKS.putAll(HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get());
	}

	/**
	 * Registers a waxable block pair.
	 * Unnecessary if part of a registered {@link OxidizableFamily}.
	 *
	 * @param blocks the blocks to register
	 * @see OxidizableBlocksRegistry#registerFamily(OxidizableFamily)
	 * @see #registerWaxablePair(Block, Block)
	 */
	public static void registerWaxablePair(WaxableBlockPair blocks) {
		UNWAXED_TO_WAXED_BLOCKS.put(blocks.unwaxed(), blocks.waxed());
		WAXED_TO_UNWAXED_BLOCKS.put(blocks.waxed(), blocks.unwaxed());
	}

	/**
	 * Registers a waxable block.
	 * Unnecessary if part of a registered {@link OxidizableFamily}.
	 *
	 * @param unwaxed the unwaxed variant
	 * @param waxed   the waxed variant
	 * @see OxidizableBlocksRegistry#registerFamily(OxidizableFamily)
	 * @see #registerWaxablePair(WaxableBlockPair)
	 */
	public static void registerWaxablePair(Block unwaxed, Block waxed) {
		registerWaxablePair(new WaxableBlockPair(unwaxed, waxed));
	}

	/**
	 * Registers multiple waxable blocks.
	 * Unnecessary if part of a registered {@link OxidizableFamily}.
	 *
	 * @param blocks the blocks to register
	 * @see OxidizableBlocksRegistry#registerFamily(OxidizableFamily)
	 */
	public static void registerWaxablePairs(WaxableBlockPair... blocks) {
		for (WaxableBlockPair pair : blocks) {
			registerWaxablePair(pair);
		}
	}

	/**
	 * Registers multiple waxable blocks.
	 * Unnecessary if part of a registered {@link OxidizableFamily}.
	 *
	 * @param blocks the blocks to register
	 * @see OxidizableBlocksRegistry#registerFamily(OxidizableFamily)
	 */
	public static void registerWaxablePairs(Iterable<WaxableBlockPair> blocks) {
		for (WaxableBlockPair pair : blocks) {
			registerWaxablePair(pair);
		}
	}

	/**
	 * Gets the map of unwaxed blocks to waxed counterparts.
	 *
	 * @return the map
	 */
	public static BiMap<Block, Block> getUnwaxedToWaxedBlocks() {
		return UNWAXED_TO_WAXED_BLOCKS;
	}

	/**
	 * Gets the map of waxed blocks to unwaxed counterparts.
	 *
	 * @return the map
	 */
	public static BiMap<Block, Block> getWaxedToUnwaxedBlocks() {
		return WAXED_TO_UNWAXED_BLOCKS;
	}

	/**
	 * Gets an Optional of the {@link Block} that would result from the given block being waxed.
	 * If no such block exists, the Optional will be empty.
	 *
	 * @param block the block to be waxed
	 * @return the waxed block
	 */
	public static Optional<Block> getWaxedBlock(Block block) {
		return Optional.ofNullable(getUnwaxedToWaxedBlocks().get(block));
	}

	/**
	 * Gets an Optional of the {@link BlockState} that would result from the given state being waxed.
	 * If no such state exists, the Optional will be empty.
	 *
	 * @param state the state to be waxed
	 * @return the waxed state
	 */
	public static Optional<BlockState> getWaxedState(BlockState state) {
		return getWaxedBlock(state.getBlock()).map((block) -> block.getStateWithProperties(state));
	}

	/**
	 * Gets an Optional of the {@link Block} that would result from the given block having its wax removed.
	 * If no such block exists, the Optional will be empty.
	 *
	 * @param block the block whose wax is to be removed
	 * @return the unwaxed block
	 */
	public static Optional<Block> getUnwaxedBlock(Block block) {
		return Optional.ofNullable(getWaxedToUnwaxedBlocks().get(block));
	}

	/**
	 * Gets an Optional of the {@link BlockState} that would result from the given state having its wax removed.
	 * If no such state exists, the Optional will be empty.
	 *
	 * @param state the state whose wax is to be removed
	 * @return the unwaxed state
	 */
	public static Optional<BlockState> getUnwaxedState(BlockState state) {
		return getUnwaxedBlock(state.getBlock()).map((block) -> block.getStateWithProperties(state));
	}
}
