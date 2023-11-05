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

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.util.Block2ObjectMap;
import net.fabricmc.fabric.impl.content.registry.SpreadableBlockRegistryImpl;

/**
 * Registries of Blocks to BlockStates, defining the spreadable BlockState to replace
 * a bare Block with when a particular type of spreadable Block spreads to the bare black.
 *
 * <p>For example, to get the registry for Mycelium block spread and then register
 * a modded block pair where Mycelium spreading to MY_DIRT generates MY_MYCELIUM:
 *
 * <pre>{@code
 * SpreadableBlockRegistry.getOrCreateInstance(SpreadableBlockRegistry.MYCELIUM)
 *        .add(MyModBlocks.MY_DIRT, MyModBlocks.MY_MYCELIUM.getDefaultState());
 * }</pre>
 */
@ApiStatus.NonExtendable
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
	 * if none currently exists.
	 *
	 * @param type The registry type Identifier for the desired spreadable block registry
	 * @return The SpreadableBlockRegistry for the given ID
	 */
	static SpreadableBlockRegistry getOrCreateInstance(Identifier type) {
		return SpreadableBlockRegistryImpl.getOrCreateInstance(type);
	}

	/**
	 * Gets an immutable copy of the spreadable block types map, which maps the
	 * canonical or "primary" block of the type to the registry for the type.
	 *
	 * <p>For example, this could be used when implementing a custom block which
	 * becomes whichever spreadable block there is the most of nearby.
	 *
	 * @return An immutable copy of the spreadable block types map
	 */
	static ImmutableMap<Block, Identifier> getSpreadableTypes() {
		return SpreadableBlockRegistryImpl.getSpreadableTypes();
	}

	/**
	 * Gets the spreadable block state (if any) for a given bare block state.
	 *
	 * <p>For example, the GRASS registry should return the modded grass block state
	 * when queried with the corresponding registered modded dirt block state.
	 *
	 * @param bareBlockState The bare block state to search the registry for
	 * @return The replacement spreadable block state for this registry, if any
	 */
	@Nullable BlockState get(BlockState bareBlockState);

	/**
	 * Gets the spreadable block state (if any) for a given bare block.
	 *
	 * <p>For example, the GRASS registry should return the modded grass block
	 * state when queried with the corresponding registered modded dirt block.
	 *
	 * @param bareBlock The bare block to search the registry for
	 * @return The replacement spreadable block state for this registry, if any
	 */
	@Nullable BlockState get(Block bareBlock);

	/**
	 * Adds a registry entry to this registry for the given bare block to spreadable block conversion.
	 *
	 * <p>For example, a mod adding a custom dirt type would register its modded dirt block and
	 * the corresponding modded grass block state to the GRASS registry.
	 *
	 * @param bareBlock The bare block which can be converted to this type of spreadable block
	 * @param spreadBlock The spreadable block state which will replace the bare block
	 */
	void add(Block bareBlock, BlockState spreadBlock);
}
