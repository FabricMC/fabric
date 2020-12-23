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

package net.fabricmc.fabric.api.lookup.v1.block;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.impl.lookup.block.BlockApiCacheImpl;
import net.fabricmc.fabric.impl.lookup.block.BlockApiLookupImpl;

/**
 * A {@link BlockApiLookup} bound to a {@link ServerWorld} and a position, providing much faster Api access.
 * Refer to {@link BlockApiLookup} for example code.
 *
 * <p>{@link BlockApiLookup#get(World, BlockPos, BlockState, BlockEntity, Object) BlockApiLookup#get()} looks up
 * the block state, the block entity, and the Api provider registered for the target block.
 *
 * <br>This object caches the block entity at the target position, and the last used Api provider, removing those queries.
 * If a block entity is available or if the block state is passed as a parameter, the block state doesn't have to be looked up either.
 *
 * @see BlockApiLookup
 */
public interface BlockApiCache<T, C> {
	/**
	 * Retrieve an Api from a block in the world, using the world and the position passed at creation time.
	 *
	 * @apiNote if the block state is known, it is more efficient to use {@link BlockApiCache#get(BlockState, Object)}.
	 */
	@Nullable
	default T get(C context) {
		return get(null, context);
	}

	/**
	 * Retrieve an Api from a block in the world, using the world and the position passed at creation time.
	 *
	 * @param state The block state at the target position, or null if unknown
	 */
	@Nullable
	T get(@Nullable BlockState state, C context);

	/**
	 * Create a new instance bound to the passed {@link ServerWorld} and position, and querying the same Apis as the passed lookup.
	 */
	static <T, C> BlockApiCache<T, C> create(BlockApiLookup<T, C> lookup, ServerWorld world, BlockPos pos) {
		Objects.requireNonNull(pos, "Pos cannot be null");
		Objects.requireNonNull(world, "World cannot be null");

		if (!(lookup instanceof BlockApiLookupImpl)) {
			throw new IllegalArgumentException("Cannot cache foreign implementation of BlockApiLookup. Use `BlockApiLookupRegistry.getLookup(Identifier, Class<T>, Class<C>);` to get the block api lookup");
		}

		return new BlockApiCacheImpl<>((BlockApiLookupImpl<T, C>) lookup, world, pos);
	}
}
