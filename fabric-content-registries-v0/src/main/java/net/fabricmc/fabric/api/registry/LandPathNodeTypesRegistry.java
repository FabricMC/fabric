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

import java.util.HashMap;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

/**
 * A registry for associations between blocks and path node types, for land entities.
 */
public final class LandPathNodeTypesRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(LandPathNodeTypesRegistry.class);
	private static final HashMap<Block, PathNodeTypeProvider> NODE_TYPES = new HashMap<>();

	private LandPathNodeTypesRegistry() {
	}

	/**
	 * Registers a {@link PathNodeType} for the specified block.
	 *
	 * @param block    Block to register.
	 * @param nodeType {@link PathNodeType} to associate to the block.
	 */
	public static void register(Block block, PathNodeType nodeType) {
		Objects.requireNonNull(block, "Block cannot be null!");
		Objects.requireNonNull(nodeType, "PathNodeType cannot be null!");

		//Registers a provider that always returns the specified node type.
		register(block, (state, world, pos) -> nodeType);
	}

	/**
	 * Registers a {@link PathNodeTypeProvider} for the specified block.
	 *
	 * @param block    Block to register.
	 * @param provider {@link PathNodeTypeProvider} to associate to the block.
	 */
	public static void register(Block block, PathNodeTypeProvider provider) {
		Objects.requireNonNull(block, "Block cannot be null!");
		Objects.requireNonNull(provider, "PathNodeTypeProvider cannot be null!");

		//Registers the provider.
		PathNodeTypeProvider old = NODE_TYPES.put(block, provider);

		if (old != null) {
			LOGGER.debug("Replaced PathNodeType provider for the block {}", block);
		}
	}

	/**
	 * Gets the {@link PathNodeType} for the specified position.
	 *
	 * @param state Current block state.
	 * @param world Current world.
	 * @param pos   Current position.
	 */
	public static @Nullable PathNodeType getPathNodeType(BlockState state, BlockView world, BlockPos pos) {
		Objects.requireNonNull(state, "BlockState cannot be null!");
		Objects.requireNonNull(world, "BlockView cannot be null!");
		Objects.requireNonNull(pos, "BlockPos cannot be null!");

		//Gets the node type for the block in the specified position.
		PathNodeTypeProvider provider = NODE_TYPES.get(state.getBlock());
		return provider != null ? provider.getPathNodeType(state, world, pos) : null;
	}
}
