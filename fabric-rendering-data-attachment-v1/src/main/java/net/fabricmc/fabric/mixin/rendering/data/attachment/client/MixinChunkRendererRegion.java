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

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;

@Mixin(ChunkRendererRegion.class)
public abstract class MixinChunkRendererRegion implements RenderAttachedBlockView {
	private Int2ObjectOpenHashMap<Object> fabric_renderDataObjects;

	@Shadow
	protected abstract int getIndex(BlockPos pos);

	@Shadow
	protected abstract int getIndex(int x, int y, int z);

	private static final AtomicInteger ERROR_COUNTER = new AtomicInteger();
	private static final Logger LOGGER = LogManager.getLogger();

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
				// We handle this simply by retrying until it works.  Ugly but effective.
				for (;;) {
					try {
						map = mapChunk(chunk, posFrom, posTo, map);
						break;
					} catch (ConcurrentModificationException e) {
						final int count = ERROR_COUNTER.incrementAndGet();

						if (count <= 5) {
							LOGGER.warn("[Render Data Attachment] Encountered CME during render region build. A mod is accessing or changing chunk data outside the main thread. Retrying.", e);

							if (count == 5) {
								LOGGER.info("[Render Data Attachment] Subsequent exceptions will be suppressed.");
							}
						}
					}
				}
			}
		}

		this.fabric_renderDataObjects = map;
	}

	private Int2ObjectOpenHashMap<Object> mapChunk(WorldChunk chunk, BlockPos posFrom, BlockPos posTo, Int2ObjectOpenHashMap<Object> map) {
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

	@Override
	public Object getBlockEntityRenderAttachment(BlockPos pos) {
		return fabric_renderDataObjects == null ? null : fabric_renderDataObjects.get(getIndex(pos));
	}
}
