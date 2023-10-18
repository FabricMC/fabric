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
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.util.Block2ObjectMap;
import net.fabricmc.fabric.impl.content.registry.SpreadableBlockRegistryImpl;

/**
 * Registries of blocks to block states, defining the block state to replace a
 * block with when a particular type of other block spreads to it.
 */
public interface SpreadableBlockRegistry extends Block2ObjectMap<BlockState> {
	/**
	 * Registry ID for Minecraft Grass Block type spreadable blocks.
	 */
	Identifier GRASS = new Identifier("grass");
	/**
	 * Registry ID for Minecraft Mycelium Block type spreadable blocks.
	 */
	Identifier MYCELIUM = new Identifier("mycelium");

	/**
	 * Get the registry for a given registry ID, or create a new registry for the ID
	 * if none currently exists.  For example, to get the registry for Mycelium block
	 * spread and then register a modded block pair:
	 *
	 * <pre>{@code
	 * SpreadableBlockRegistry.getOrCreateInstance(SpreadableBlockRegistry.MYCELIUM)
	 * 		.add(MyModBlocks.MY_DIRT, MyModBlocks.MY_MYCELIUM.getDefaultState());
	 * }</pre>
	 *
	 * @param type The registry type Identifier for the desired spreadable block registry
	 * @return The SpreadableBlockRegistry for the given ID
	 */
	static SpreadableBlockRegistry getOrCreateInstance(Identifier type) {
		return SpreadableBlockRegistryImpl.getOrCreateInstance(type);
	}

	/**
	 * Fetch the spreadable block state (if any) for a given bare block state.
	 *
	 * @param bareBlockState The bare block state to search the registry for
	 * @return The replacement spreadable block state for this registry, if any
	 */
	BlockState get(BlockState bareBlockState);

	/**
	 * Fetch the spreadable block state (if any) for a given bare block.
	 *
	 * @param bareBlock The bare block to search the registry for
	 * @return The replacement spreadable block state for this registry, if any
	 */
	BlockState get(Block bareBlock);

	/**
	 * Add a registry entry to this registry for the given bare block to spreadable block conversion.
	 *
	 * @param bareBlock The bare block which can be converted to this type of spreadable block
	 * @param spreadBlock The spreadable block state which will replace the bare block
	 */
	void add(Block bareBlock, BlockState spreadBlock);
}
