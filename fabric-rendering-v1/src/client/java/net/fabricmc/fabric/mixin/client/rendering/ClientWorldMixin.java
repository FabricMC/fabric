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

package net.fabricmc.fabric.mixin.client.rendering;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.ColorResolver;

import net.fabricmc.fabric.api.client.rendering.v1.ColorResolverRegistry;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {
	// Do not use the vanilla map because it is an Object2ObjectArrayMap. Array maps have O(n) retrievals compared to
	// hash maps' O(1) retrievals. If many custom ColorResolvers are used, this may have a non-negligible performance
	// impact.
	@Unique
	private final ConcurrentHashMap<ColorResolver, BiomeColorCache> customColorCache = new ConcurrentHashMap<>();

	// This is only used when lazily creating new BiomeColorCaches in modifyNullCache, but storing it as a field
	// prevents allocating the capturing lambda every time a custom ColorResolver is used.
	@Unique
	private final Function<ColorResolver, BiomeColorCache> customCacheFactory = resolver -> {
		// Unregistered ColorResolvers cannot be used. If a mod passes a (unregistered) capturing lambda as the
		// resolver, customColorCache would quickly fill up because its entries are never discarded (until the whole
		// ClientWorld is closed).
		if (!ColorResolverRegistry.isRegistered(resolver)) {
			throw new UnsupportedOperationException("ClientWorld.getColor called with unregistered ColorResolver " + resolver);
		}

		return new BiomeColorCache(pos -> calculateColor(pos, resolver));
	};

	@Shadow
	public abstract int calculateColor(BlockPos pos, ColorResolver colorResolver);

	@Inject(method = "resetChunkColor(Lnet/minecraft/util/math/ChunkPos;)V", at = @At("RETURN"))
	private void onResetChunkColor(ChunkPos chunkPos, CallbackInfo ci) {
		customColorCache.forEach((resolver, cache) -> cache.reset(chunkPos.x, chunkPos.z));
	}

	@Inject(method = "reloadColor()V", at = @At("RETURN"))
	private void onReloadColor(CallbackInfo ci) {
		customColorCache.forEach((resolver, cache) -> cache.reset());
	}

	@ModifyExpressionValue(method = "getColor(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/biome/ColorResolver;)I", at = @At(value = "INVOKE", target = "it/unimi/dsi/fastutil/objects/Object2ObjectArrayMap.get(Ljava/lang/Object;)Ljava/lang/Object;"))
	private Object modifyNullCache(/* BiomeColorCache */ Object cache, BlockPos pos, ColorResolver resolver) {
		if (cache == null) {
			return customColorCache.computeIfAbsent(resolver, customCacheFactory);
		}

		return cache;
	}
}
