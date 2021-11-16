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

import net.minecraft.block.Block;
import net.minecraft.item.HoneycombItem;

import net.fabricmc.fabric.api.util.OxidizableFamily;
import net.fabricmc.fabric.api.util.WaxableBlockPair;

public class WaxableBlocksRegistry {
	/**
	 * Registers a waxable block pair.
	 * Unnecessary if part of a registered {@link OxidizableFamily}.
	 *
	 * @param blocks the blocks to register
	 * @see OxidizableBlocksRegistry#registerFamily(OxidizableFamily)
	 * @see #registerWaxablePair(Block, Block)
	 */
	public static void registerWaxablePair(WaxableBlockPair blocks) {
		HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().put(blocks.unwaxed(), blocks.waxed());
		HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(blocks.waxed(), blocks.unwaxed());
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
}
