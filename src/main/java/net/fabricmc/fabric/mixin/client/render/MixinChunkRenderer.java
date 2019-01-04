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
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.fabric.api.client.render.FabricVertexLighter;
import net.fabricmc.fabric.impl.client.render.RenderConfiguration;
import net.minecraft.class_852;
import net.minecraft.class_853;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.ChunkRenderData;
import net.minecraft.client.render.chunk.ChunkRenderDataTask;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.util.math.BlockPos;

@Mixin(ChunkRenderer.class)
public class MixinChunkRenderer
{
    /**
     * Gives enhanced block tesselate access to stuff it needs layers/flags when it encounters an enhanced model.
     * Ugly and probably slow, but minimally invasive.<p>
     * 
     * Also shares ThreadLocal with lighter, vs having a separate ThreadLocal.
     * 
     * TODO: look for an instance that will reliably be in our render thread and mixin there,
     *  vs. a threadlocal lookup. Seems like any of our arguments might suffice.
     */
    static class ChunkRenderAccess
    {
        ChunkRenderer chunkRenderer;
        ChunkRenderData chunkRenderData;
        ChunkRenderDataTask chunkRenderDataTask;
        FabricVertexLighter lighter = RenderConfiguration.createLighter();
        
        boolean[] resultFlags;
        
        void prepare(ChunkRenderer chunkRenderer, ChunkRenderData chunkRenderData, ChunkRenderDataTask chunkRenderDataTask, boolean[] resultFlags) {
            this.chunkRenderer = chunkRenderer;
            this.chunkRenderData = chunkRenderData;
            this.chunkRenderDataTask = chunkRenderDataTask;
            this.resultFlags = resultFlags;
        }
        
        void clear()
        {
            this.chunkRenderer = null;
            this.chunkRenderData = null;
            this.chunkRenderDataTask = null;
            this.resultFlags = null;
        }
    }
   
    static final ThreadLocal<ChunkRenderAccess> CURRENT_CHUNK_RENDER = ThreadLocal.withInitial(ChunkRenderAccess::new);
    
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getRenderLayer()Lnet/minecraft/client/render/block/BlockRenderLayer;", ordinal = 0), method = "method_3652", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void hook(float f1, float f2, float f3, ChunkRenderDataTask task, CallbackInfo info, ChunkRenderData chunkRenderData, BlockPos posFrom, BlockPos posTo, class_852 class_852_1, Set set_1, class_853 worldView, boolean resultFlags[], Random random, BlockRenderManager blockRenderManager_1, Iterator var16, BlockPos.Mutable tesselatedBlockPos, BlockState blockState, Block block_1) {
        CURRENT_CHUNK_RENDER.get().prepare((ChunkRenderer)(Object)this, chunkRenderData, task, resultFlags);
    }
}
