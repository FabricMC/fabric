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

package net.fabricmc.indigo.renderer.render;

import it.unimi.dsi.fastutil.longs.Long2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.indigo.Indigo;
import net.fabricmc.indigo.renderer.accessor.AccessBufferBuilder;
import net.fabricmc.indigo.renderer.accessor.AccessChunkRenderer;
import net.fabricmc.indigo.renderer.accessor.AccessChunkRendererData;
import net.fabricmc.indigo.renderer.aocalc.AoLuminanceFix;
import net.fabricmc.indigo.renderer.mesh.MutableQuadViewImpl;
import net.minecraft.block.Block.OffsetType;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilder;
import net.minecraft.client.render.chunk.ChunkBatcher.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkBatcher.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;

/**
 * Holds, manages and provides access to the chunk-related state
 * needed by fallback and mesh consumers during terrain rendering.<p>
 * 
 * Exception: per-block position offsets are tracked here so they can
 * be applied together with chunk offsets.
 */
public class ChunkRenderInfo {
    /**
     * Serves same function as brightness cache in Mojang's AO calculator,
     * with some differences as follows...<p>
     * 
     * 1) Mojang uses Object2Int.  This uses Long2Int for performance and to avoid
     * creating new immutable BlockPos references.  But will break if someone
     * wants to expand Y limit or world borders.  If we want to support that may
     * need to switch or make configurable.<p>
     * 
     * 2) Mojang overrides the map methods to limit the cache to 50 values.
     * However, a render chunk only has 18^3 blocks in it, and the cache is cleared every chunk.
     * For performance and simplicity, we just let map grow to the size of the render chunk.
     * 
     * 3) Mojang only uses the cache for Ao.  Here it is used for all brightness
     * lookups, including flat lighting.
     * 
     * 4) The Mojang cache is a separate threadlocal with a threadlocal boolean to 
     * enable disable. Cache clearing happens with the disable. There's no use case for 
     * us when the cache needs to be disabled (and no apparent case in Mojang's code either)
     * so we simply clear the cache at the start of each new chunk. It is also
     * not a threadlocal because it's held within a threadlocal BlockRenderer.
     */
    private final Long2IntOpenHashMap brightnessCache;
    private final Long2FloatOpenHashMap aoLevelCache;
    
    private final BlockRenderInfo blockInfo;
    private final BlockPos.Mutable chunkOrigin = new BlockPos.Mutable();
    AccessChunkRendererData chunkData;
    ChunkRenderer chunkRenderer;
    BlockLayeredBufferBuilder builders;
    BlockRenderView blockView;
    
    private final Object2ObjectOpenHashMap<BlockRenderLayer, AccessBufferBuilder> buffers = new Object2ObjectOpenHashMap<>();
    
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
    
    void prepare(
    		ChunkRendererRegion blockView,
    		ChunkRenderer chunkRenderer, 
    		ChunkRenderData chunkData, 
    		BlockLayeredBufferBuilder builders) {
    	this.blockView = blockView;
        this.chunkOrigin.set(chunkRenderer.getOrigin());
        this.chunkData = (AccessChunkRendererData) chunkData;
        this.chunkRenderer = chunkRenderer;
        this.builders = builders;
        buffers.clear();
        chunkOffsetX = -chunkOrigin.getX();
        chunkOffsetY = -chunkOrigin.getY();
        chunkOffsetZ = -chunkOrigin.getZ();
        brightnessCache.clear();
        aoLevelCache.clear();
    }
    
    void release() {
        chunkData = null;
        chunkRenderer = null;
        buffers.clear();
    }
    
    void beginBlock() {
        final BlockState blockState = blockInfo.blockState;
        final BlockPos blockPos = blockInfo.blockPos;
        
        // When we are using the BufferBuilder input methods, the builder will
        // add the chunk offset for us, so we should only apply the block offset.
        if(Indigo.ENSURE_VERTEX_FORMAT_COMPATIBILITY) {
            offsetX = (float) (blockPos.getX());
            offsetY = (float) (blockPos.getY());
            offsetZ = (float) (blockPos.getZ());
        } else {
            offsetX = (float) (chunkOffsetX + blockPos.getX());
            offsetY = (float) (chunkOffsetY + blockPos.getY());
            offsetZ = (float) (chunkOffsetZ + blockPos.getZ());
        }
        
        if(blockState.getBlock().getOffsetType() != OffsetType.NONE) {
            Vec3d offset = blockState.getOffsetPos(blockInfo.blockView, blockPos);
            offsetX += (float)offset.x;
            offsetY += (float)offset.y;
            offsetZ += (float)offset.z;
        }
    }
    
    
    /** Lazily retrieves output buffer for given layer, initializing as needed. */
    public AccessBufferBuilder getInitializedBuffer(BlockRenderLayer renderLayer) {
        AccessBufferBuilder result = buffers.get(renderLayer);
        if (result == null) {
        	BufferBuilder builder = builders.get(renderLayer);
        	result = (AccessBufferBuilder) builder;
        	chunkData.fabric_markPopulated(renderLayer);
            buffers.put(renderLayer, result);
            if (chunkData.fabric_markInitialized(renderLayer)) {
                ((AccessChunkRenderer) chunkRenderer).fabric_beginBufferBuilding(builder, chunkOrigin);
            }
        }
        return result;
    }
    
    /**
     * Applies position offset for chunk and, if present, block random offset.
     */
    void applyOffsets(MutableQuadViewImpl q) {
        for(int i = 0; i < 4; i++) {
            q.pos(i, q.x(i) + offsetX, q.y(i) + offsetY, q.z(i) + offsetZ);
        }
    }
    
    /**
     * Cached values for {@link BlockState#getBlockBrightness(BlockRenderView, BlockPos)}.
     * See also the comments for {@link #brightnessCache}.
     */
    int cachedBrightness(BlockPos pos) {
        long key = pos.asLong();
        int result = brightnessCache.get(key);
        if (result == Integer.MAX_VALUE) {
        	result = blockView.getLightmapIndex(blockView.getBlockState(pos), pos);
            brightnessCache.put(key, result);
        }
        return result;
    }
    
    float cachedAoLevel(BlockPos pos)
    {
        long key = pos.asLong();
        float result = aoLevelCache.get(key);
        if (result == Float.MAX_VALUE) {
            result = AoLuminanceFix.INSTANCE.apply(blockView, pos);
            aoLevelCache.put(key, result);
        }
        return result;
    }
}
