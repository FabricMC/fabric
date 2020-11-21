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

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.provider.v1.block.BlockApiLookup;

/**
 * Implementation of a cache for block entities. The cache is valid if cachedProvider is not null.
 */
public class BlockApiCacheImpl<T, C> implements BlockApiCache<T, C> {
	private final BlockApiLookupImpl<T, C> lookup;
	private final ServerWorld world;
	private final BlockPos pos;
	private BlockEntity cachedBlockEntity = null;
	private BlockApiLookup.BlockEntityApiProvider<T, C> cachedProvider = null;

	public BlockApiCacheImpl(BlockApiLookupImpl<T, C> lookup, ServerWorld world, BlockPos pos) {
		((ServerWorldCache) world).api_provider_registerCache(pos, this);
		this.lookup = lookup;
		this.world = world;
		this.pos = pos.toImmutable();
	}

	public void invalidate() {
		cachedBlockEntity = null;
		cachedProvider = null;
	}

	@Override
	public T get(C context) {
		if (cachedProvider == null) {
			BlockApiLookup.BlockApiProvider<T, C> provider = lookup.getProvider(world, pos);

			if (provider instanceof BlockApiLookupImpl.WrappedBlockEntityProvider) {
				this.cachedProvider = ((BlockApiLookupImpl.WrappedBlockEntityProvider<T, C>) provider).blockEntityProvider;
				this.cachedBlockEntity = world.getBlockEntity(pos);
			} else if (provider != null) {
				return provider.get(world, pos, context);
			}
		}

		if (cachedProvider != null && cachedBlockEntity != null) {
			return cachedProvider.get(cachedBlockEntity, context);
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
