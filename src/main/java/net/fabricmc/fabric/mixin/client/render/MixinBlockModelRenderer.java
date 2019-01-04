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

import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.api.client.model.FabricBakedModel;
import net.fabricmc.fabric.api.client.model.FabricBakedQuad;
import net.fabricmc.fabric.api.client.model.RenderCacheView;
import net.fabricmc.fabric.api.client.render.FabricQuadBakery;
import net.fabricmc.fabric.api.client.render.RenderConfiguration;
import net.fabricmc.fabric.mixin.client.render.MixinChunkRenderer.ChunkRenderAccess;
import net.minecraft.block.Block;
import net.minecraft.block.Block.OffsetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilder;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ExtendedBlockView;

@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer
{

    @Inject(at = @At("HEAD"), method = "tesselate", cancellable = true)
    public void onTesselate(ExtendedBlockView blockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean checkOcclusion, Random random, long renderSeed, CallbackInfoReturnable<Boolean> info) {
        if(bakedModel instanceof FabricBakedModel) {
            // checkOcclusion is always true for chunk rebuilds (hard-coded constant), so we don't bother to pass it
            // woudln't make sense for it to be otherwise
            info.setReturnValue(this.fabricTesselate(blockView, (FabricBakedModel)bakedModel, blockState, blockPos, bufferBuilder, renderSeed));
            info.cancel();
        } else if (RenderConfiguration.useConsistentLighting()) {
	        info.setReturnValue(this.standardTesselate(blockView, bakedModel, blockState, blockPos, bufferBuilder, renderSeed));
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
    boolean fabricTesselate(ExtendedBlockView blockView, FabricBakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, long renderSeed)
    {
        final BlockRenderLayer primaryLayer = blockState.getBlock().getRenderLayer();
        final ChunkRenderAccess access = MixinChunkRenderer.CURRENT_CHUNK_RENDER.get();
        
        // Retrieve buffer builder array - we'll need it
        BlockLayeredBufferBuilder builders = access.chunkRenderDataTask.getBufferBuilders();
        
        // caller will have initialized the primary buffer for us
        int initializedFlags = 1 << primaryLayer.ordinal(); // <-- TODO: retrieve flags for other layers from new accessor interface on ChunkRenderer
        int resultBitFlags = initializedFlags;
        
        final ModelData data = MODEL_DATA.get().prepare(blockPos, blockView, blockState, renderSeed);
        final List<FabricBakedQuad> quads = bakedModel.getFabricBlockQuads(data, blockState, blockPos, data.random);
        final int limit = quads.size();
        for(int i = 0; i < limit; i++) {
            final FabricBakedQuad quad = quads.get(i);
            final int[] vertexData = quad.getVertexData();
            final int index = quad.firstVertexIndex();
            final Direction blockFace = FabricQuadBakery.Quad.getActualFace(vertexData, index);
            if(blockFace == null || data.shouldOutputSide(blockFace)) {
                final int quadLayerFlags = FabricQuadBakery.Quad.getExtantLayers(vertexData, index);
                resultBitFlags |= quadLayerFlags;
                initializedFlags = intializeBuffersAsNeeded(initializedFlags, quadLayerFlags, access, blockPos);
                access.lighter.lightFabricBakedQuad(vertexData, index, builders, data);
            }
        }
        
        updateCompletionFlags(resultBitFlags, access.resultFlags);
        
        // don't hold references
        access.clear();
        
        return (resultBitFlags & (1 << primaryLayer.ordinal())) != 0;
    }

    static final Direction[] DIRECTIONS = Direction.values();
    
    /**
     * Emulates standard model render with fabric lighter when consistent lighting is enabled.
     * 
     * Doing this here vs. hooking methods further down the stack offers better cache exploitation.
     */
    boolean standardTesselate(ExtendedBlockView blockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, long renderSeed)
    {
        final BlockRenderLayer renderLayer = blockState.getBlock().getRenderLayer();
        final ChunkRenderAccess access = MixinChunkRenderer.CURRENT_CHUNK_RENDER.get();
        // Retrieve buffer builder array - we'll need it
        BlockLayeredBufferBuilder builders = access.chunkRenderDataTask.getBufferBuilders();
        final ModelData data = MODEL_DATA.get().prepare(blockPos, blockView, blockState, renderSeed);
        final Random random = data.random;
        final boolean useAO = MinecraftClient.isAmbientOcclusionEnabled() && blockState.getLuminance() == 0 && bakedModel.useAmbientOcclusion();
        boolean didOutput = false;
        
        for(int i = 0; i < 6; i++) {
        	final Direction face = DIRECTIONS[i];
        	random.setSeed(renderSeed);
        	List<BakedQuad> quads = bakedModel.getQuads(blockState, face, random);
        	if (!quads.isEmpty() && data.shouldOutputSide(face)) {
                // TODO: need to be handled in lighter for flat
        		// int int_1 = blockState_1.getBlockBrightness(extendedBlockView_1, blockPos_1.offset(direction_1));
                access.lighter.lightStandardBakedQuads(quads, builders, data, renderLayer, useAO);
                didOutput = true;
             }
        }
        	
        random.setSeed(renderSeed);
        List<BakedQuad> quads = bakedModel.getQuads(blockState, (Direction)null, random);
        if (!quads.isEmpty()) {
        	access.lighter.lightStandardBakedQuads(quads, builders, data, renderLayer, useAO);
            didOutput = true;
        }
        
        // don't hold references
        access.clear();
        
        return didOutput;
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
    
    public static class ModelData implements RenderCacheView {
        BlockPos pos;
        ExtendedBlockView world;
        BlockState blockState;
        final Random random = new Random();
        
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
            this.random.setSeed(randomSeed);
            
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

		@Override
		public BlockEntity getBlockEntity(BlockPos var1) {
			// always null at render time
			return null;
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			return world.getBlockState(pos);
		}

		@Override
		public FluidState getFluidState(BlockPos pos) {
			return world.getFluidState(pos);
		}

		@Override
		public <T> T getCachedRenderData() {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
