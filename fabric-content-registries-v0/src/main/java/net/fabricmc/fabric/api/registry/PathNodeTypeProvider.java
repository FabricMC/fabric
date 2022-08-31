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

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

/**
 * Provider of {@link PathNodeType}.
 */
public interface PathNodeTypeProvider {
	/**
	 * Gets the {@link PathNodeType} for the specified position.
	 *
	 * <p>Is possible to specify what to return if the block is a direct target of an entity path,
	 * or is a neighbor block that the entity will find in the path.
	 *
	 * <p>For example, for cactus you should specify DAMAGE_CACTUS if the block is a direct target (neighbor = false)
	 * to specify that an entity should not pass through or above the block because it will cause damage,
	 * and DANGER_CACTUS if the cactus will be found as a neighbor block in the entity path (neighbor = true)
	 * to specify that the entity should not get close to the block because here is danger.
	 *
	 * @param state    Current block state.
	 * @param world    Current world.
	 * @param pos      Current position.
	 * @param neighbor Specifies if the block is not a directly targeted block, but a neighbor block in the path.
	 */
	@Nullable
	PathNodeType getPathNodeType(BlockState state, BlockView world, BlockPos pos, boolean neighbor);
}
