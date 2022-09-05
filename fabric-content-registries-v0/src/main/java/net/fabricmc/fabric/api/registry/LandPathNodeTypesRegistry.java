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

import java.util.IdentityHashMap;
import java.util.Map;
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
 * A registry to associate specific node types to blocks.
 * Specifying a node type for a block will change the way an entity recognizes the block when trying to pathfind.
 * For example, you can specify that a block is dangerous and should be avoided by entities.
 * This works only for entities that move on air and land.
 */
public final class LandPathNodeTypesRegistry {
	private static final Logger LOGGER = LoggerFactory.getLogger(LandPathNodeTypesRegistry.class);
	private static final Map<Block, PathNodeTypeProvider> NODE_TYPES = new IdentityHashMap<>();

	private LandPathNodeTypesRegistry() {
	}

	/**
	 * Registers a {@link PathNodeType} for the specified block.
	 * This will override the default block behavior.
	 * For example, you can make a safe block as dangerous and vice-versa.
	 * Duplicated registrations for the same block will replace the previous registration.
	 *
	 * @param block              Block to register.
	 * @param nodeType           {@link PathNodeType} to associate with the block.
	 *                           (Pass {@code null} to not specify a node type and use the default behavior)
	 * @param nodeTypeIfNeighbor {@link PathNodeType} to associate to the block, if is a neighbor block in the path.
	 *                           (Pass {@code null} to not specify a node type and use the default behavior)
	 */
	public static void register(Block block, @Nullable PathNodeType nodeType, @Nullable PathNodeType nodeTypeIfNeighbor) {
		Objects.requireNonNull(block, "Block cannot be null!");

		// Registers a provider that always returns the specified node type.
		register(block, (state, world, pos, neighbor) -> neighbor ? nodeTypeIfNeighbor : nodeType);
	}

	/**
	 * Registers a {@link PathNodeTypeProvider} for the specified block.
	 * This will override the default block behavior.
	 * For example, you can make a safe block as dangerous and vice-versa.
	 * Duplicated registrations for the same block will replace the previous registrations.
	 *
	 * @param block    Block to register.
	 * @param provider {@link PathNodeTypeProvider} to associate with the block.
	 */
	public static void register(Block block, PathNodeTypeProvider provider) {
		Objects.requireNonNull(block, "Block cannot be null!");
		Objects.requireNonNull(provider, "PathNodeTypeProvider cannot be null!");

		// Registers the provider.
		PathNodeTypeProvider old = NODE_TYPES.put(block, provider);

		if (old != null) {
			LOGGER.debug("Replaced PathNodeType provider for the block {}", block);
		}
	}

	/**
	 * Gets the custom {@link PathNodeType} registered for the specified block at the specified position.
	 *
	 * <p>If no custom {@link PathNodeType} is registered for the block, it returns {@code null}.
	 * You cannot use this method to retrieve vanilla block node types.
	 *
	 * @param state    Current block state.
	 * @param world    Current world.
	 * @param pos      Current position.
	 * @param neighbor Specifies if the block is not a directly targeted block, but a neighbor block in the path.
	 * @return the custom {@link PathNodeType} registered for the specified block at the specified position.
	 */
	@Nullable
	public static PathNodeType getPathNodeType(BlockState state, BlockView world, BlockPos pos, boolean neighbor) {
		Objects.requireNonNull(state, "BlockState cannot be null!");
		Objects.requireNonNull(world, "BlockView cannot be null!");
		Objects.requireNonNull(pos, "BlockPos cannot be null!");

		// Gets the node type for the block at the specified position.
		PathNodeTypeProvider provider = NODE_TYPES.get(state.getBlock());
		return provider != null ? provider.getPathNodeType(state, world, pos, neighbor) : null;
	}

	/**
	 * A functional interface that provides the {@link PathNodeType}, given the block state and position.
	 */
	@FunctionalInterface
	public interface PathNodeTypeProvider {
		/**
		 * Gets the {@link PathNodeType} for the specified block at the specified position.
		 *
		 * <p>You can specify what to return if the block is a direct target of an entity path,
		 * or a neighbor block that the entity will find in the path.
		 *
		 * <p>For example, for a cactus-like block you should specify {@link PathNodeType#DAMAGE_CACTUS} if the block
		 * is a direct target ({@code neighbor == false}) to specify that an entity should not pass through or above
		 * the block because it will cause damage, and {@link PathNodeType#DANGER_CACTUS} if the cactus will be found
		 * as a neighbor block in the entity path ({@code neighbor == true}) to specify that the entity should not get
		 * close to the block because is dangerous.
		 *
		 * @param state    Current block state.
		 * @param world    Current world.
		 * @param pos      Current position.
		 * @param neighbor Specifies if the block is not a directly targeted block, but a neighbor block in the path.
		 * @return the custom {@link PathNodeType} registered for the specified block at the specified position.
		 */
		@Nullable
		PathNodeType getPathNodeType(BlockState state, BlockView world, BlockPos pos, boolean neighbor);
	}
}
