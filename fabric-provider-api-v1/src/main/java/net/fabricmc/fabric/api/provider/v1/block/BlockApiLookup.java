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

package net.fabricmc.fabric.api.provider.v1.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.provider.v1.ApiLookup;

public interface BlockApiLookup<T, C> extends ApiLookup<C> {
	@Nullable
	T get(World world, BlockPos pos, C context);

	void registerForBlocks(BlockApiProvider<T, C> provider, Block... blocks);

	void registerForBlockEntities(BlockEntityApiProvider<T, C> provider, BlockEntityType<?>... blockEntityTypes);

	void registerBlockEntityFallback(BlockEntityApiProvider<T, C> fallbackProvider);

	void registerBlockFallback(BlockApiProvider<T, C> fallbackProvider);

	@FunctionalInterface
	interface BlockApiProvider<T, C> {
		@Nullable
		T get(World world, BlockPos pos, C context);
	}

	@FunctionalInterface
	interface BlockEntityApiProvider<T, C> {
		@Nullable
		T get(@NotNull BlockEntity blockEntity, C context);
	}
}
