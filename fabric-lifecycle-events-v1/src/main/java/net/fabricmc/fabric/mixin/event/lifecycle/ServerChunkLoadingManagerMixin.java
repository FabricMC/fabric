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

package net.fabricmc.fabric.mixin.event.lifecycle;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkLoadingManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

@Mixin(ServerChunkLoadingManager.class)
public abstract class ServerChunkLoadingManagerMixin {
	@Shadow
	@Final
	private ServerWorld world;

	// Chunk (Un)Load events, An explanation:
	// Must of this code is wrapped inside of futures and consumers, so it's generally a mess.

	/**
	 * Injection is inside of tryUnloadChunk.
	 * We inject just after "setLoadedToWorld" is made false, since here the WorldChunk is guaranteed to be unloaded.
	 */
	@Inject(method = "method_60440", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/WorldChunk;setLoadedToWorld(Z)V", shift = At.Shift.AFTER))
	private void onChunkUnload(ChunkHolder chunkHolder, long l, CallbackInfo ci) {
		ServerChunkEvents.CHUNK_UNLOAD.invoker().onChunkUnload(this.world, (WorldChunk) chunkHolder.getLatest());
	}
}
