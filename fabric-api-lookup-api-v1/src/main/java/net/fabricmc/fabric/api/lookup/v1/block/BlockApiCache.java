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

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.impl.lookup.block.BlockApiCacheImpl;
import net.fabricmc.fabric.impl.lookup.block.BlockApiLookupImpl;

/**
 * A {@link BlockApiLookup} bound to a {@link ServerWorld} and a position, providing much faster Api access.
 * See {@link BlockApiLookup} for more information and example code.
 */
public interface BlockApiCache<T, C> {
	/**
	 * Retrieve an Api from a block in the world, using the world and the position passed at creation time.
	 */
	@Nullable
	T get(C context);

	/**
	 * Create a new instance bound to the passed {@link ServerWorld} and position, and querying the same Apis as the passed
	 * lookup.
	 */
	static <T, C> BlockApiCache<T, C> create(BlockApiLookup<T, C> lookup, ServerWorld world, BlockPos pos) {
		Objects.requireNonNull(pos, "Pos cannot be null");
		Objects.requireNonNull(world, "World cannot be null");

		if (!(lookup instanceof BlockApiLookupImpl)) {
			throw new IllegalArgumentException("BlockApiLookup must be a BlockApiLookupImpl");
		}

		return new BlockApiCacheImpl<>((BlockApiLookupImpl<T, C>) lookup, world, pos);
	}
}
