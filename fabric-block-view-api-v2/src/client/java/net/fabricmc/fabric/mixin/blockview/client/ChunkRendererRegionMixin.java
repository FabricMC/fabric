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

package net.fabricmc.fabric.mixin.blockview.client;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.blockview.client.RenderDataMapConsumer;

@Mixin(ChunkRendererRegion.class)
public abstract class ChunkRendererRegionMixin implements BlockRenderView, RenderDataMapConsumer {
	@Shadow
	@Final
	protected World world;

	@Unique
	@Nullable
	private Long2ObjectMap<Object> fabric_renderDataMap;

	@Override
	public Object getBlockEntityRenderData(BlockPos pos) {
		return fabric_renderDataMap == null ? null : fabric_renderDataMap.get(pos.asLong());
	}

	/**
	 * Called in {@link ChunkRendererRegionBuilderMixin}.
	 */
	@Override
	public void fabric_acceptRenderDataMap(Long2ObjectMap<Object> renderDataMap) {
		this.fabric_renderDataMap = renderDataMap;
	}

	@Override
	public boolean hasBiomes() {
		return true;
	}

	@Override
	public RegistryEntry<Biome> getBiomeFabric(BlockPos pos) {
		return world.getBiome(pos);
	}
}
