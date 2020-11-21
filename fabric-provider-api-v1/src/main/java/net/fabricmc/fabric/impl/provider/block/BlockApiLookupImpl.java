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

package net.fabricmc.fabric.impl.provider.block;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.provider.v1.ApiProviderMap;
import net.fabricmc.fabric.api.provider.v1.ContextKey;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookup;
import net.fabricmc.fabric.mixin.provider.BlockEntityTypeAccessor;

public final class BlockApiLookupImpl<T, C> implements BlockApiLookup<T, C> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ApiProviderMap<Block, BlockApiProvider<T, C>> providerMap = ApiProviderMap.create();
	private final Identifier id;
	private final ContextKey<C> contextKey;

	BlockApiLookupImpl(Identifier apiId, ContextKey<C> contextKey) {
		this.id = apiId;
		this.contextKey = contextKey;
	}

	@Nullable
	@Override
	public T get(World world, BlockPos pos, C context) {
		// This call checks for null world and pos.
		// Providers have the final say whether a null context is allowed.
		@Nullable
		BlockApiProvider<T, C> provider = getProvider(world, pos);

		if (provider != null) {
			return provider.get(world, pos, context);
		}

		return null;
	}

	@Override
	public void registerForBlocks(BlockApiProvider<T, C> provider, Block... blocks) {
		Objects.requireNonNull(provider, "BlockApiProvider cannot be null");

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

		for (final BlockEntityType<?> blockEntityType : blockEntityTypes) {
			Objects.requireNonNull(blockEntityType, "encountered null block entity type while registering a block entity API provider mapping");

			final Block[] blocks = ((BlockEntityTypeAccessor) blockEntityType).getBlocks().toArray(new Block[0]);
			final BlockApiProvider<T, C> blockProvider = new WrappedBlockEntityProvider<>(provider);

			registerForBlocks(blockProvider, blocks);
		}
	}

	@Override
	public Identifier getApiId() {
		return id;
	}

	@Override
	public ContextKey<C> getContextKey() {
		return contextKey;
	}

	@Nullable
	public BlockApiProvider<T, C> getProvider(World world, BlockPos pos) {
		Objects.requireNonNull(world, "World cannot be null");
		Objects.requireNonNull(pos, "Block pos cannot be null");

		return providerMap.get(world.getBlockState(pos).getBlock());
	}

	public static class WrappedBlockEntityProvider<T, C> implements BlockApiProvider<T, C> {
		public final BlockEntityApiProvider<T, C> blockEntityProvider;

		public WrappedBlockEntityProvider(BlockEntityApiProvider<T, C> blockEntityProvider) {
			this.blockEntityProvider = blockEntityProvider;
		}

		@Override
		public @Nullable T get(World world, BlockPos pos, C context) {
			@Nullable final BlockEntity blockEntity = world.getBlockEntity(pos);

			if (blockEntity != null) {
				return blockEntityProvider.get(blockEntity, context);
			}

			return null;
		}
	}
}
