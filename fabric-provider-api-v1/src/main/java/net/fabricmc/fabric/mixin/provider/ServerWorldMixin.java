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

package net.fabricmc.fabric.mixin.provider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.impl.provider.block.BlockApiCacheImpl;
import net.fabricmc.fabric.impl.provider.block.ServerWorldCache;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ServerWorldCache {
	private final Map<BlockPos, List<WeakReference<BlockApiCacheImpl<?, ?>>>> api_provider_caches = new Object2ReferenceOpenHashMap<>();

	@Override
	public void api_provider_registerCache(BlockPos pos, BlockApiCacheImpl<?, ?> cache) {
		List<WeakReference<BlockApiCacheImpl<?, ?>>> caches = api_provider_caches.computeIfAbsent(pos.toImmutable(), ignored -> new ArrayList<>());
		caches.removeIf(weakReference -> weakReference.get() == null);
		caches.add(new WeakReference<>(cache));
	}

	@Override
	public void api_provider_invalidateCache(BlockPos pos) {
		List<WeakReference<BlockApiCacheImpl<?, ?>>> caches = api_provider_caches.get(pos);

		if (caches != null) {
			caches.removeIf(weakReference -> weakReference.get() == null);

			if (caches.size() == 0) {
				api_provider_caches.remove(pos);
			} else {
				caches.forEach(weakReference -> {
					BlockApiCacheImpl<?, ?> cache = weakReference.get();

					if (cache != null) {
						cache.invalidate();
					}
				});
			}
		}
	}
}
