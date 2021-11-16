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
import net.minecraft.item.HoneycombItem;

public class WaxableBlocksRegistry {
	/**
	 * Registers a block pair as being able to add and remove wax.
	 *
	 * @param unwaxed the unwaxed variant
	 * @param waxed   the waxed variant
	 * @see #registerUnwaxedToWaxed(Block, Block)
	 * @see #registerWaxedToUnwaxed(Block, Block)
	 */
	public static void registerWaxableBlockPair(Block unwaxed, Block waxed) {
		registerUnwaxedToWaxed(unwaxed, waxed);
		registerWaxedToUnwaxed(waxed, unwaxed);
	}

	/**
	 * Registers a block pair as being able to add wax.
	 *
	 * @param unwaxed the unwaxed variant
	 * @param waxed   the waxed variant
	 * @see #registerWaxableBlockPair(Block, Block)
	 * @see #registerWaxedToUnwaxed(Block, Block)
	 */
	public static void registerUnwaxedToWaxed(Block unwaxed, Block waxed) {
		Objects.requireNonNull(unwaxed, "Unwaxed block cannot be null!");
		Objects.requireNonNull(waxed, "Waxed block cannot be null!");
		HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().put(unwaxed, waxed);
	}

	/**
	 * Registers a block pair as being able to remove wax.
	 *
	 * @param waxed   the waxed variant
	 * @param unwaxed the unwaxed variant
	 * @see #registerWaxableBlockPair(Block, Block)
	 * @see #registerUnwaxedToWaxed(Block, Block)
	 */
	public static void registerWaxedToUnwaxed(Block waxed, Block unwaxed) {
		Objects.requireNonNull(unwaxed, "Unwaxed block cannot be null!");
		Objects.requireNonNull(waxed, "Waxed block cannot be null!");
		HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(waxed, unwaxed);
	}
}
