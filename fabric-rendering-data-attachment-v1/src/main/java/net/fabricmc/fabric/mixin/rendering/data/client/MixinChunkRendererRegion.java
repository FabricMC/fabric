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

package net.fabricmc.fabric.mixin.rendering.data.client;

import java.util.ConcurrentModificationException;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(ChunkRendererRegion.class)
public abstract class MixinChunkRendererRegion implements RenderAttachedBlockView {
	private Int2ObjectOpenHashMap<Object> fabric_renderDataObjects;

	@Shadow
	protected abstract int getIndex(BlockPos pos);

	@Shadow
	protected abstract int getIndex(int x, int y, int z);

	@Inject(at = @At("RETURN"), method = "<init>")
	public void init(World world, int cxOff, int czOff, WorldChunk[][] chunks, BlockPos posFrom, BlockPos posTo, CallbackInfo info) {
		// instantiated lazily - avoids allocation for chunks without any data objects - which is most of them!
		Int2ObjectOpenHashMap<Object> map = null;

		for (WorldChunk[] chunkOuter : chunks) {
			for (WorldChunk chunk : chunkOuter) {
				// Hash maps in chunks should generally not be modified outside of client thread
				// but does happen in practice, due to mods or inconsistent vanilla behaviors, causing 
				// CMEs when we iterate the map.  (Vanilla does not iterate these maps when it builds
				// the chunk cache and does not suffer from this problem.)
				// 
				// We handle this by trying to iterating the block entity map first, because that 
				// usually works and is most efficient.  If we encounter a CME, we retry, querying the 
				// map by iterating block positions. This method is a little slower but will not cause a CME.
				try {
					map = mapChunkFast(chunk, posFrom, posTo, map);
				} catch (ConcurrentModificationException e) {
					map = mapChunkSafely(chunk, posFrom, posTo, map);
				}
			}
		}

		this.fabric_renderDataObjects = map;
	}

	private Int2ObjectOpenHashMap<Object> mapChunkFast(WorldChunk chunk, BlockPos posFrom, BlockPos posTo, Int2ObjectOpenHashMap<Object> map) {
		final int xMin = posFrom.getX();
		final int xMax = posTo.getX();
		final int zMin = posFrom.getZ();
		final int zMax = posTo.getZ();
		final int yMin = posFrom.getY();
		final int yMax = posTo.getY();

		for (Map.Entry<BlockPos, BlockEntity> entry : chunk.getBlockEntities().entrySet()) {
			final BlockPos entPos = entry.getKey();
			
			if (entPos.getX() >= xMin && entPos.getX() <= xMax 
					&& entPos.getY() >= yMin && entPos.getY() <= yMax
					&& entPos.getZ() >= zMin && entPos.getZ() <= zMax) {

				final Object o = ((RenderAttachmentBlockEntity) entry.getValue()).getRenderAttachmentData();
				if (o != null) {
					if (map == null) {
						map = new Int2ObjectOpenHashMap<>();
					}
					map.put(getIndex(entPos), o);
				}
			}
		}
		return map;
	}

	private Int2ObjectOpenHashMap<Object> mapChunkSafely(WorldChunk chunk, BlockPos posFrom, BlockPos posTo, Int2ObjectOpenHashMap<Object> map) {
		final Map<BlockPos, BlockEntity> blockEntities = chunk.getBlockEntities();
		
		int beCount = blockEntities.size();
		if (beCount == 0) {
			return map;
		}

		final ChunkPos chunkPos = chunk.getPos();
		final int xMin = Math.max(posFrom.getX(), chunkPos.getStartX());
		final int xMax = Math.min(posTo.getX(), chunkPos.getStartX() + 16);
		final int zMin = Math.max(posFrom.getZ(), chunkPos.getStartZ());
		final int zMax = Math.min(posTo.getZ(), chunkPos.getStartZ() + 16);
		final int yMax = posTo.getY();
		final BlockPos.PooledMutable searchPos = BlockPos.PooledMutable.get();

		for (int x = xMin; x < xMax; x++) {
			for (int y = posFrom.getY(); y < yMax; y++) {
				for (int z = zMin; z < zMax; z++) {
					final BlockEntity be = blockEntities.get(searchPos.set(x, y, z));
					if (be != null) {
						final Object o = ((RenderAttachmentBlockEntity) be).getRenderAttachmentData();
						if (o != null) {
							if (map == null) {
								map = new Int2ObjectOpenHashMap<>();
							}
							map.put(getIndex(x, y, z), o);
						}

						if (--beCount == 0) {
							return map;
						}
					}
				}
			}
		}

		searchPos.close();

		return map;
	}

	@Override
	public Object getBlockEntityRenderAttachment(BlockPos pos) {
		return fabric_renderDataObjects == null ? null : fabric_renderDataObjects.get(getIndex(pos));
	}
}
