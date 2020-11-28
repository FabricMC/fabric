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

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.impl.event.lifecycle.LoadedChunksCache;

@Mixin(World.class)
public abstract class WorldMixin implements LoadedChunksCache {
	@Shadow
	public abstract boolean isClient();

	@Shadow
	public abstract Profiler getProfiler();

	@Unique
	private final Set<ChunkPos> loadedChunks = new HashSet<>();

	@Inject(at = @At("RETURN"), method = "tickBlockEntities")
	protected void tickWorldAfterBlockEntities(CallbackInfo ci) {
		if (!this.isClient()) {
			ServerTickEvents.END_WORLD_TICK.invoker().onEndTick((ServerWorld) (Object) this);
		}
	}

	@Override
	public Set<ChunkPos> fabric_getLoadedPositions() {
		return this.loadedChunks;
	}

	@Override
	public void fabric_markLoaded(ChunkPos pos) {
		this.loadedChunks.add(pos);
	}

	@Override
	public void fabric_markUnloaded(ChunkPos pos) {
		this.loadedChunks.remove(pos);
	}
}
