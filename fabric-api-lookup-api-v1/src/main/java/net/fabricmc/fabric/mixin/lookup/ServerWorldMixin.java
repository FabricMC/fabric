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

package net.fabricmc.fabric.mixin.lookup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.impl.lookup.block.BlockApiCacheImpl;
import net.fabricmc.fabric.impl.lookup.block.ServerWorldCache;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ServerWorldCache {
	@Unique
	private final Map<BlockPos, List<WeakReference<BlockApiCacheImpl<?, ?>>>> api_lookup_caches = new Object2ReferenceOpenHashMap<>();
	// This field ensures that the api_lookup_caches map is iterated over every now and then to clean up
	@Unique
	private int api_lookup_accessesWithoutCleanup = 0;

	@Override
	public void api_provider_registerCache(BlockPos pos, BlockApiCacheImpl<?, ?> cache) {
		List<WeakReference<BlockApiCacheImpl<?, ?>>> caches = api_lookup_caches.computeIfAbsent(pos.toImmutable(), ignored -> new ArrayList<>());
		caches.removeIf(weakReference -> weakReference.get() == null);
		caches.add(new WeakReference<>(cache));
		api_lookup_accessesWithoutCleanup++;
	}

	@Override
	public void api_provider_invalidateCache(BlockPos pos) {
		List<WeakReference<BlockApiCacheImpl<?, ?>>> caches = api_lookup_caches.get(pos);

		if (caches != null) {
			caches.removeIf(weakReference -> weakReference.get() == null);

			if (caches.size() == 0) {
				api_lookup_caches.remove(pos);
			} else {
				caches.forEach(weakReference -> {
					BlockApiCacheImpl<?, ?> cache = weakReference.get();

					if (cache != null) {
						cache.invalidate();
					}
				});
			}
		}

		api_lookup_accessesWithoutCleanup++;

		if (api_lookup_accessesWithoutCleanup > 2 * api_lookup_caches.size()) {
			api_lookup_caches.entrySet().removeIf(entry -> {
				entry.getValue().removeIf(weakReference -> weakReference.get() == null);
				return entry.getValue().isEmpty();
			});
			api_lookup_accessesWithoutCleanup = 0;
		}
	}
}
