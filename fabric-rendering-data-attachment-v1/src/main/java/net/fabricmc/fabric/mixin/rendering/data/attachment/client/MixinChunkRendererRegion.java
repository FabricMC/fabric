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

package net.fabricmc.fabric.mixin.rendering.data.attachment.client;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.fabricmc.fabric.impl.rendering.data.attachment.RenderDataObjectConsumer;

@Mixin(ChunkRendererRegion.class)
public abstract class MixinChunkRendererRegion implements RenderAttachedBlockView, RenderDataObjectConsumer {
	private Long2ObjectOpenHashMap<Object> fabric_renderDataObjects;

	@Override
	public Object getBlockEntityRenderAttachment(BlockPos pos) {
		return fabric_renderDataObjects == null ? null : fabric_renderDataObjects.get(pos.asLong());
	}

	// Called in MixinChunkRendererRegionBuilder
	@Override
	public void fabric_acceptRenderDataObjects(Long2ObjectOpenHashMap<Object> renderDataObjects) {
		this.fabric_renderDataObjects = renderDataObjects;
	}
}
