/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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
package net.fabricmc.fabric.mixin.client.render;

import java.util.Iterator;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.api.client.model.BlockModelData;
import net.fabricmc.fabric.api.client.model.TailoredModel;
import net.fabricmc.fabric.api.client.model.TailoredQuad;
import net.fabricmc.fabric.api.client.render.QuadTailor;
import net.fabricmc.fabric.mixin.client.render.MixinChunkRenderer.ChunkRenderAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block.OffsetType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilder;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ExtendedBlockView;

@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer
{

    @Inject(at = @At("HEAD"), method = "tesselate", cancellable = true)
    public void onTesselate(ExtendedBlockView blockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean checkOcclusion, Random random, long renderSeed, CallbackInfoReturnable<Boolean> info) {
        if(bakedModel instanceof TailoredModel)
        {
            
            // checkOcclusion is always true for chunk rebuilds (hard-coded constant), so we don't bother to pass it
            // woudln't make sense for it to be otherwise
            info.setReturnValue(this.fabricTesselate(blockView, (TailoredModel)bakedModel, blockState, blockPos, bufferBuilder, renderSeed));
            info.cancel();
        }
    }
    
    /**
     * Handles models with enhanced rendering.
     * 
     * @param blockView
     * @param bakedModel
     * @param blockState
     * @param blockPos
     * @param bufferBuilder   will be buffer for layer reported by block
     * @param renderSeed      
     * 
     * @return result for the block render layer.  If other render layers are also populated,
     * this method has to handle initating those buffers and setting flags as appropriate.
     */
    boolean fabricTesselate(ExtendedBlockView blockView, TailoredModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, long renderSeed)
    {
        final BlockRenderLayer primaryLayer = blockState.getBlock().getRenderLayer();
        final ChunkRenderAccess access = MixinChunkRenderer.CURRENT_CHUNK_RENDER.get();
        
        // Retrieve buffer builder array - we'll need it
        BlockLayeredBufferBuilder builders = access.chunkRenderDataTask.getBufferBuilders();
        
        // caller will have initialized the primary buffer for us
        int initializedFlags = 1 << primaryLayer.ordinal(); // <-- TODO: retrieve flags for other layers from new accessor interface on ChunkRenderer
        int resultBitFlags = initializedFlags;
        
        final ModelData data = MODEL_DATA.get().prepare(blockPos, blockView, blockState, renderSeed);
        final Iterator<TailoredQuad> quads = bakedModel.getBlockQuads(data);
        
        while(quads.hasNext()) {
            final TailoredQuad quad = quads.next();
            final int[] vertexData = quad.getVertexData();
            final int index = quad.firstVertexIndex();
            final Direction blockFace = QuadTailor.Quad.getActualFace(vertexData, index);
            if(blockFace == null || data.shouldOutputSide(blockFace))
            {
                final int quadLayerFlags = QuadTailor.Quad.getExtantLayers(vertexData, index);
                resultBitFlags |= quadLayerFlags;
                initializedFlags = intializeBuffersAsNeeded(initializedFlags, quadLayerFlags, access, blockPos);
                tesselateEnhancedQuad(quad, vertexData, index, builders, data);
            }
        }
        
        updateCompletionFlags(resultBitFlags, access.resultFlags);
        
        // don't hold references
        access.clear();
        
        return (resultBitFlags & (1 << primaryLayer.ordinal())) != 0;
    }
    
    /**
     * Outputs one or more quads to appropriate buffers with lighting and coloring
     * based on metadata saved at bake time. Handles randomized position offsets 
     * and block tinting as needed. Block tint is per-quad based on enable flags in metadata.
     * 
     * Diffuse shading honors vertex normals to allow for non-cubic geometry.
     * Likewise, AO calculations are enhanced for same purpose. (Vanilla AO doesn't handle
     * triangles or non-square quads.) 
     * 
     * Face culling has already happened before this point.
     */
    private static void tesselateEnhancedQuad(TailoredQuad quad, int[] vertexData, int index, BlockLayeredBufferBuilder builders, ModelData data) {
        // TODO - MAGIC HAPPENS HERE
        
    }

    /** 
     * Save non-primary result flags back to chunk renderer state
     */
    private static void updateCompletionFlags(int resultBitFlags, boolean[] resultFlags) {
        if((resultBitFlags & (1 << BlockRenderLayer.SOLID.ordinal())) != 0) 
            resultFlags[BlockRenderLayer.SOLID.ordinal()] = true;
        if((resultBitFlags & (1 << BlockRenderLayer.CUTOUT.ordinal())) != 0) 
            resultFlags[BlockRenderLayer.CUTOUT.ordinal()] = true;
        if((resultBitFlags & (1 << BlockRenderLayer.MIPPED_CUTOUT.ordinal())) != 0) 
            resultFlags[BlockRenderLayer.SOLID.ordinal()] = true;
        if((resultBitFlags & (1 << BlockRenderLayer.TRANSLUCENT.ordinal())) != 0) 
            resultFlags[BlockRenderLayer.TRANSLUCENT.ordinal()] = true;
    }
    
    /** Initializes any builds not already initialized and returns updated tracking flags. */
    private static int intializeBuffersAsNeeded(int priorInitializedFlags, int quadLayerFlags, ChunkRenderAccess access, BlockPos pos) {
        final int result = quadLayerFlags | priorInitializedFlags;
        if(priorInitializedFlags != result) {
            intializeBuffersInner(priorInitializedFlags, BlockRenderLayer.SOLID, access, pos);
            intializeBuffersInner(priorInitializedFlags, BlockRenderLayer.MIPPED_CUTOUT, access, pos);
            intializeBuffersInner(priorInitializedFlags, BlockRenderLayer.CUTOUT, access, pos);
            intializeBuffersInner(priorInitializedFlags, BlockRenderLayer.TRANSLUCENT, access, pos);
         }
        return result;
    }
        
    private static void intializeBuffersInner(int priorInitializedFlags, BlockRenderLayer layer, ChunkRenderAccess access, BlockPos pos) {
        if(((1 << layer.ordinal()) & priorInitializedFlags) == 0) {
            BufferBuilder builder = access.chunkRenderDataTask.getBufferBuilders().get(layer.ordinal());
            if (!access.chunkRenderData.method_3649(layer)) {
                access.chunkRenderData.method_3647(layer);
                // TODO: create accessor
               // access.chunkRenderer.method_3655(builder, pos);
            }
        }
    }
    
    private static final ThreadLocal<ModelData> MODEL_DATA = ThreadLocal.withInitial(ModelData::new);
    
    private static class ModelData implements BlockModelData {
        BlockPos pos;
        ExtendedBlockView world;
        BlockState blockState;
        final Random rand = new Random();
        
        protected BlockEntity blockEntity;
        protected boolean needsBlockEntityLookup;
        
        protected int sideLookupCompletionFlags = 0;
        protected int sideLookupResultFlags = 0;
        
        // cache model offsets for plants, etc.
        float offsetX = 0;
        float offsetY = 0;
        float offsetZ = 0;
        
        final ModelData prepare(BlockPos pos, ExtendedBlockView world, BlockState blockState, long randomSeed) {
            this.pos = pos;
            this.world = world;
            this.blockState = blockState;
            this.blockEntity = null;
            this.rand.setSeed(randomSeed);
            needsBlockEntityLookup = true;
            
            if(blockState.getBlock().getOffsetType() == OffsetType.NONE) {
                offsetX = 0;
                offsetY = 0;
                offsetZ = 0;
            }
            else {
                Vec3d offset = blockState.getOffsetPos(world, pos);
                offsetX = (float) offset.x;
                offsetY = (float) offset.y;
                offsetZ = (float) offset.z;
            }
            
            return this;
        }

        @Override
        public final BlockPos pos() {
            return this.pos;
        }

        @Override
        public final ExtendedBlockView world() {
            return world;
        }

        @Override
        public final BlockState blockState() {
            return blockState;
        }

        @Override
        public final BlockEntity blockEntity() {
            // TODO: retrieve from ChunkRenderer hashMap directly if faster
            if(this.needsBlockEntityLookup)
                blockEntity = world.getBlockEntity(pos);
            return blockEntity;
        }

        @Override
        public final Random random() {
            return rand;
        }
        
        final boolean shouldOutputSide(Direction side) {
            final int mask = 1 << (side.ordinal());
            if((sideLookupCompletionFlags & mask) == 0) {
                sideLookupCompletionFlags |= mask;
                boolean result = Block.shouldDrawSide(blockState, world, pos, side);
                if(result)
                    sideLookupResultFlags |= mask;
                return result;
            }
            else
                return (sideLookupResultFlags & mask) != 0;
        }
    }
}
