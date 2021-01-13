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

package net.fabricmc.fabric.impl.lookup.block;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.lookup.v1.ApiProviderMap;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.mixin.lookup.BlockEntityTypeAccessor;

public final class BlockApiLookupImpl<T, C> implements BlockApiLookup<T, C> {
	private static final Logger LOGGER = LogManager.getLogger("fabric-api-lookup-api-v1");
	private final ApiProviderMap<Block, BlockApiProvider<T, C>> providerMap = ApiProviderMap.create();
	private final List<FallbackApiProvider<T, C>> fallbackProviders = new CopyOnWriteArrayList<>();

	@Nullable
	@Override
	public T get(World world, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity, C context) {
		// This call checks for null world and pos.
		// Providers have the final say whether a null context is allowed.

		// Get the block state and the block entity
		if (blockEntity == null) {
			if (state == null) {
				state = world.getBlockState(pos);
			}

			if (state.getBlock().hasBlockEntity()) {
				blockEntity = world.getBlockEntity(pos);
			}
		} else {
			if (state == null) {
				state = blockEntity.getCachedState();
			}
		}

		@Nullable
		BlockApiProvider<T, C> provider = getProvider(state.getBlock());
		T instance = null;

		// Query the providers at the position
		if (provider instanceof WrappedBlockEntityProvider) {
			if (blockEntity != null) {
				instance = ((WrappedBlockEntityProvider<T, C>) provider).blockEntityProvider.get(blockEntity, context);
			}
		} else if (provider != null) {
			instance = provider.get(world, pos, state, context);
		}

		if (instance != null) {
			return instance;
		}

		// Query the fallback providers
		for (FallbackApiProvider<T, C> fallbackProvider : fallbackProviders) {
			instance = fallbackProvider.get(world, pos, state, blockEntity, context);

			if (instance != null) {
				return instance;
			}
		}

		return null;
	}

	@Override
	public void registerForBlocks(BlockApiProvider<T, C> provider, Block... blocks) {
		Objects.requireNonNull(provider, "BlockApiProvider cannot be null");
		Objects.requireNonNull(blocks, "Block... cannot be null");

		if (blocks.length == 0) {
			throw new IllegalArgumentException("Must register at least one Block instance with a BlockApiProvider");
		}

		for (final Block block : blocks) {
			Objects.requireNonNull(block, "encountered null block while registering a block API provider mapping");

			if (providerMap.putIfAbsent(block, provider) != null) {
				LOGGER.warn("Encountered duplicate API provider registration for block: " + Registry.BLOCK.getId(block));
			}
		}
	}

	@Override
	public void registerForBlockEntities(BlockEntityApiProvider<T, C> provider, BlockEntityType<?>... blockEntityTypes) {
		Objects.requireNonNull(provider, "encountered null BlockEntityApiProvider");
		Objects.requireNonNull(blockEntityTypes, "BlockEntityType... cannot be null");

		if (blockEntityTypes.length == 0) {
			throw new IllegalArgumentException("Must register at least one BlockEntityType instance with a BlockEntityApiProvider");
		}

		for (final BlockEntityType<?> blockEntityType : blockEntityTypes) {
			Objects.requireNonNull(blockEntityType, "encountered null block entity type while registering a block entity API provider mapping");

			final Block[] blocks = ((BlockEntityTypeAccessor) blockEntityType).getBlocks().toArray(new Block[0]);
			final BlockApiProvider<T, C> blockProvider = new WrappedBlockEntityProvider<>(provider);

			registerForBlocks(blockProvider, blocks);
		}
	}

	@Override
	public void registerFallback(FallbackApiProvider<T, C> fallbackProvider) {
		Objects.requireNonNull(fallbackProvider, "FallbackApiProvider cannot be null");

		fallbackProviders.add(fallbackProvider);
	}

	@Nullable
	public BlockApiProvider<T, C> getProvider(Block block) {
		return providerMap.get(block);
	}

	public List<FallbackApiProvider<T, C>> getFallbackProviders() {
		return fallbackProviders;
	}

	static final class WrappedBlockEntityProvider<T, C> implements BlockApiProvider<T, C> {
		final BlockEntityApiProvider<T, C> blockEntityProvider;

		WrappedBlockEntityProvider(BlockEntityApiProvider<T, C> blockEntityProvider) {
			this.blockEntityProvider = blockEntityProvider;
		}

		@Override
		public @Nullable T get(World world, BlockPos pos, BlockState state, C context) {
			// implementations refer to block entity provider field
			throw new UnsupportedOperationException("This should never be called!");
		}
	}
}
