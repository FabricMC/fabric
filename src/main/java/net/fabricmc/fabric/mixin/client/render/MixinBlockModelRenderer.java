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
import net.fabricmc.fabric.api.client.render.LighterBlockView;
import net.fabricmc.fabric.impl.client.render.RenderConfiguration;
import net.fabricmc.fabric.mixin.client.render.MixinChunkRenderer.ChunkRenderAccess;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilder;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ExtendedBlockView;

@Mixin(BlockModelRenderer.class)
public class MixinBlockModelRenderer
{
    static interface RenderCachePrivateView extends RenderCacheView, ExtendedBlockView, LighterBlockView {
        
        boolean shouldOutputSide(Direction side);
        
        void prepare(BlockPos pos, BlockState blockState);
    }
    
    @Inject(at = @At("HEAD"), method = "tesselate", cancellable = true)
    public void onTesselate(ExtendedBlockView blockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean checkOcclusion, Random random, long renderSeed, CallbackInfoReturnable<Boolean> info) {
        if(bakedModel instanceof FabricBakedModel) {
            // checkOcclusion is always true for chunk rebuilds (hard-coded constant), so we don't bother to pass it
            // woudln't make sense for it to be otherwise
            random.setSeed(renderSeed);
            info.setReturnValue(this.fabricTesselate((RenderCachePrivateView) blockView, (FabricBakedModel) bakedModel, blockState, blockPos, bufferBuilder, random));
            info.cancel();
        } else if (RenderConfiguration.useConsistentLighting()) {
	        info.setReturnValue(this.standardTesselate((RenderCachePrivateView) blockView, bakedModel, blockState, blockPos, bufferBuilder, random, renderSeed));
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
     * this method has to handle initializing those buffers and setting flags as appropriate.
     */
    boolean fabricTesselate(RenderCachePrivateView blockView, FabricBakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, Random random)
    {
        final BlockRenderLayer primaryLayer = blockState.getBlock().getRenderLayer();
        final ChunkRenderAccess access = MixinChunkRenderer.CURRENT_CHUNK_RENDER.get();
        
        // Retrieve buffer builder array - we'll need it
        BlockLayeredBufferBuilder builders = access.chunkRenderDataTask.getBufferBuilders();
        
        // caller will have initialized the primary buffer for us
        int initializedFlags = 1 << primaryLayer.ordinal();
        int resultBitFlags = initializedFlags;
        
        blockView.prepare(blockPos, blockState);
        final List<FabricBakedQuad> quads = bakedModel.getFabricBakedQuads(blockView, blockState, blockPos, random);
        final int limit = quads.size();
        for(int i = 0; i < limit; i++) {
            final FabricBakedQuad quad = quads.get(i);
            final Direction blockFace = quad.getGeometricFace();
            if(blockFace == null || blockView.shouldOutputSide(blockFace)) {
                final int quadLayerFlags = quad.getRenderLayerFlags();
                resultBitFlags |= quadLayerFlags;
                initializedFlags = intializeBuffersAsNeeded(initializedFlags, quadLayerFlags, access, blockPos);
                access.lighter.lightFabricBakedQuad(quad, builders, blockView);
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
    boolean standardTesselate(RenderCachePrivateView blockView, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, Random random, long renderSeed)
    {
        final BlockRenderLayer renderLayer = blockState.getBlock().getRenderLayer();
        final ChunkRenderAccess access = MixinChunkRenderer.CURRENT_CHUNK_RENDER.get();
        // Retrieve buffer builder array - we'll need it
        BlockLayeredBufferBuilder builders = access.chunkRenderDataTask.getBufferBuilders();
        final boolean useAO = MinecraftClient.isAmbientOcclusionEnabled() && blockState.getLuminance() == 0 && bakedModel.useAmbientOcclusion();
        boolean didOutput = false;
        
        for(int i = 0; i < 6; i++) {
        	final Direction face = DIRECTIONS[i];
        	random.setSeed(renderSeed);
        	List<BakedQuad> quads = bakedModel.getQuads(blockState, face, random);
        	if (!quads.isEmpty() && blockView.shouldOutputSide(face)) {
                access.lighter.lightStandardBakedQuads(quads, builders, blockView, renderLayer, useAO);
                didOutput = true;
             }
        }
        	
        random.setSeed(renderSeed);
        List<BakedQuad> quads = bakedModel.getQuads(blockState, (Direction)null, random);
        if (!quads.isEmpty()) {
        	access.lighter.lightStandardBakedQuads(quads, builders, blockView, renderLayer, useAO);
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
}
