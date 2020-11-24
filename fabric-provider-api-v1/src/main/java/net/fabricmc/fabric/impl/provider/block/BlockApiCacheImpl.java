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

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookup;

/**
 * Implementation of a cache for block entities.
 */
public class BlockApiCacheImpl<T, C> implements BlockApiCache<T, C> {
	private final BlockApiLookupImpl<T, C> lookup;
	private final ServerWorld world;
	private final BlockPos pos;
	private BlockEntity cachedBlockEntity = null;
	private boolean blockEntityCacheValid = false;
	private BlockApiLookup.BlockEntityApiProvider<T, C> cachedProvider = null;

	public BlockApiCacheImpl(BlockApiLookupImpl<T, C> lookup, ServerWorld world, BlockPos pos) {
		((ServerWorldCache) world).api_provider_registerCache(pos, this);
		this.lookup = lookup;
		this.world = world;
		this.pos = pos.toImmutable();
	}

	public void invalidate() {
		blockEntityCacheValid = false;
		cachedBlockEntity = null;
		cachedProvider = null;
	}

	@Nullable
	@Override
	public T get(C context) {
		// Cache BE provider and BE if possible, otherwise query using the regular block provider
		if (cachedProvider == null) {
			BlockApiLookup.BlockApiProvider<T, C> provider = lookup.getProvider(world, pos);

			if (provider instanceof BlockApiLookupImpl.WrappedBlockEntityProvider) {
				cachedProvider = ((BlockApiLookupImpl.WrappedBlockEntityProvider<T, C>) provider).blockEntityProvider;
				cachedBlockEntity = world.getBlockEntity(pos);
				blockEntityCacheValid = true;
			} else if (provider != null) {
				T instance = provider.get(world, pos, context);

				if (instance != null) {
					return instance;
				}
			}
		}

		// Query using the cached block entity provider
		if (cachedProvider != null && cachedBlockEntity != null) {
			T instance = cachedProvider.get(cachedBlockEntity, context);

			if (instance != null) {
				return instance;
			}
		}

		for (BlockApiLookup.BlockEntityApiProvider<T, C> fallbackProvider : lookup.getBlockEntityFallbackProviders()) {
			if (!blockEntityCacheValid) {
				cachedBlockEntity = world.getBlockEntity(pos);
				blockEntityCacheValid = true;
			}

			if (cachedBlockEntity != null) {
				T instance = fallbackProvider.get(cachedBlockEntity, context);

				if (instance != null) {
					return instance;
				}
			}
		}

		for (BlockApiLookup.BlockApiProvider<T, C> fallbackProvider : lookup.getFallbackProviders()) {
			T instance = fallbackProvider.get(world, pos, context);

			if (instance != null) {
				return instance;
			}
		}

		return null;
	}

	static {
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
			((ServerWorldCache) world).api_provider_invalidateCache(blockEntity.getPos());
		});
		ServerBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((blockEntity, world) -> {
			((ServerWorldCache) world).api_provider_invalidateCache(blockEntity.getPos());
		});
	}
}
