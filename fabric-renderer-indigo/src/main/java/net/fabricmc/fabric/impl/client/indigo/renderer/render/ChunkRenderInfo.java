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

package net.fabricmc.fabric.impl.client.indigo.renderer.render;

import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;

import net.minecraft.block.Block.OffsetType;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderTask;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ExtendedBlockView;

import net.fabricmc.fabric.impl.client.indigo.Indigo;
import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessBufferBuilder;
import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessChunkRenderer;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoLuminanceFix;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MutableQuadViewImpl;

/**
 * Holds, manages and provides access to the chunk-related state
 * needed by fallback and mesh consumers during terrain rendering.
 *
 * <p>Exception: per-block position offsets are tracked here so they can
 * be applied together with chunk offsets.
 */
public class ChunkRenderInfo {
	/**
	 * Serves same function as brightness cache in Mojang's AO calculator,
	 * with some differences as follows...
	 *
	 * <ul><li>Mojang uses Object2Int.  This uses Long2Int for performance and to avoid
	 * creating new immutable BlockPos references.  But will break if someone
	 * wants to expand Y limit or world borders.  If we want to support that may
	 * need to switch or make configurable.
	 *
	 * <li>Mojang overrides the map methods to limit the cache to 50 values.
	 * However, a render chunk only has 18^3 blocks in it, and the cache is cleared every chunk.
	 * For performance and simplicity, we just let map grow to the size of the render chunk.
	 *
	 * <li>Mojang only uses the cache for Ao.  Here it is used for all brightness
	 * lookups, including flat lighting.
	 *
	 * <li>The Mojang cache is a separate threadlocal with a threadlocal boolean to
	 * enable disable. Cache clearing happens with the disable. There's no use case for
	 * us when the cache needs to be disabled (and no apparent case in Mojang's code either)
	 * so we simply clear the cache at the start of each new chunk. It is also
	 * not a threadlocal because it's held within a threadlocal BlockRenderer.</ul>
	 */
	private final Long2IntOpenHashMap brightnessCache;
	private final Long2FloatOpenHashMap aoLevelCache;

	private final BlockRenderInfo blockInfo;
	private final BlockPos.Mutable chunkOrigin = new BlockPos.Mutable();
	ChunkRenderTask chunkTask;
	ChunkRenderData chunkData;
	ChunkRenderer chunkRenderer;
	ExtendedBlockView blockView;
	boolean[] resultFlags;

	private final AccessBufferBuilder[] buffers = new AccessBufferBuilder[4];
	private final BlockRenderLayer[] LAYERS = BlockRenderLayer.values();

	private double chunkOffsetX;
	private double chunkOffsetY;
	private double chunkOffsetZ;

	// chunk offset + block pos offset + model offsets for plants, etc.
	private float offsetX = 0;
	private float offsetY = 0;
	private float offsetZ = 0;

	ChunkRenderInfo(BlockRenderInfo blockInfo) {
		this.blockInfo = blockInfo;
		brightnessCache = new Long2IntOpenHashMap();
		brightnessCache.defaultReturnValue(Integer.MAX_VALUE);
		aoLevelCache = new Long2FloatOpenHashMap();
		aoLevelCache.defaultReturnValue(Float.MAX_VALUE);
	}

	void setBlockView(ChunkRendererRegion blockView) {
		this.blockView = blockView;
	}

	void setChunkTask(ChunkRenderTask chunkTask) {
		this.chunkTask = chunkTask;
	}

	void prepare(ChunkRenderer chunkRenderer, BlockPos.Mutable chunkOrigin, boolean[] resultFlags) {
		this.chunkOrigin.set(chunkOrigin);
		this.chunkData = chunkTask.getRenderData();
		this.chunkRenderer = chunkRenderer;
		this.resultFlags = resultFlags;
		buffers[0] = null;
		buffers[1] = null;
		buffers[2] = null;
		buffers[3] = null;
		chunkOffsetX = -chunkOrigin.getX();
		chunkOffsetY = -chunkOrigin.getY();
		chunkOffsetZ = -chunkOrigin.getZ();
		brightnessCache.clear();
		aoLevelCache.clear();
	}

	void release() {
		chunkData = null;
		chunkTask = null;
		chunkRenderer = null;
		buffers[0] = null;
		buffers[1] = null;
		buffers[2] = null;
		buffers[3] = null;
	}

	void beginBlock() {
		final BlockState blockState = blockInfo.blockState;
		final BlockPos blockPos = blockInfo.blockPos;

		// When we are using the BufferBuilder input methods, the builder will
		// add the chunk offset for us, so we should only apply the block offset.
		if (Indigo.ENSURE_VERTEX_FORMAT_COMPATIBILITY) {
			offsetX = (blockPos.getX());
			offsetY = (blockPos.getY());
			offsetZ = (blockPos.getZ());
		} else {
			offsetX = (float) (chunkOffsetX + blockPos.getX());
			offsetY = (float) (chunkOffsetY + blockPos.getY());
			offsetZ = (float) (chunkOffsetZ + blockPos.getZ());
		}

		if (blockState.getBlock().getOffsetType() != OffsetType.NONE) {
			Vec3d offset = blockState.getOffsetPos(blockInfo.blockView, blockPos);
			offsetX += (float) offset.x;
			offsetY += (float) offset.y;
			offsetZ += (float) offset.z;
		}
	}

	/** Lazily retrieves output buffer for given layer, initializing as needed. */
	public AccessBufferBuilder getInitializedBuffer(int layerIndex) {
		// redundant for first layer, but probably not faster to check
		resultFlags[layerIndex] = true;

		AccessBufferBuilder result = buffers[layerIndex];

		if (result == null) {
			BufferBuilder builder = chunkTask.getBufferBuilders().get(layerIndex);
			buffers[layerIndex] = (AccessBufferBuilder) builder;
			BlockRenderLayer layer = LAYERS[layerIndex];

			if (!chunkData.isBufferInitialized(layer)) {
				chunkData.markBufferInitialized(layer); // start buffer
				((AccessChunkRenderer) chunkRenderer).fabric_beginBufferBuilding(builder, chunkOrigin);
			}

			result = (AccessBufferBuilder) builder;
		}

		return result;
	}

	/**
	 * Applies position offset for chunk and, if present, block random offset.
	 */
	void applyOffsets(MutableQuadViewImpl q) {
		for (int i = 0; i < 4; i++) {
			q.pos(i, q.x(i) + offsetX, q.y(i) + offsetY, q.z(i) + offsetZ);
		}
	}

	/**
	 * Cached values for {@link BlockState#getBlockBrightness(ExtendedBlockView, BlockPos)}.
	 * See also the comments for {@link #brightnessCache}.
	 */
	int cachedBrightness(BlockPos pos) {
		long key = pos.asLong();
		int result = brightnessCache.get(key);

		if (result == Integer.MAX_VALUE) {
			result = blockView.getBlockState(pos).getBlockBrightness(blockView, pos);
			brightnessCache.put(key, result);
		}

		return result;
	}

	float cachedAoLevel(BlockPos pos) {
		long key = pos.asLong();
		float result = aoLevelCache.get(key);

		if (result == Float.MAX_VALUE) {
			result = AoLuminanceFix.INSTANCE.apply(blockView, pos);
			aoLevelCache.put(key, result);
		}

		return result;
	}
}
