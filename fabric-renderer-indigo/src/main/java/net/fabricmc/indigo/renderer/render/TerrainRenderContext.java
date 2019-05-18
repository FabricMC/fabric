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

package net.fabricmc.indigo.renderer.render;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.renderer.v1.render.TerrainBlockView;
import net.fabricmc.indigo.renderer.aocalc.AoCalculator;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.ChunkRenderTask;
import net.minecraft.client.render.chunk.ChunkRenderer;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;

/**
 * Implementation of {@link RenderContext} used during terrain rendering.
 * Dispatches calls from models during chunk rebuild to the appropriate consumer,
 * and holds/manages all of the state needed by them.
 */
public class TerrainRenderContext extends AbstractRenderContext implements RenderContext {
    public static final ThreadLocal<TerrainRenderContext> POOL = ThreadLocal.withInitial(TerrainRenderContext::new);
    private final TerrainBlockRenderInfo blockInfo = new TerrainBlockRenderInfo();
    private final ChunkRenderInfo chunkInfo = new ChunkRenderInfo(blockInfo);
    private final AoCalculator aoCalc = new AoCalculator(blockInfo, chunkInfo::cachedBrightness, chunkInfo::cachedAoLevel);
    private final TerrainMeshConsumer meshConsumer = new TerrainMeshConsumer(blockInfo, chunkInfo, aoCalc, this::transform);
    private final TerrainFallbackConsumer fallbackConsumer = new TerrainFallbackConsumer(blockInfo, chunkInfo, aoCalc, this::transform);
    private final BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
    
    public void setBlockView(ChunkRendererRegion blockView) {
        blockInfo.setBlockView((TerrainBlockView) blockView);
        chunkInfo.setBlockView(blockView);
    }
    
    public void setChunkTask(ChunkRenderTask chunkTask) {
        chunkInfo.setChunkTask(chunkTask);
    }
    
    public TerrainRenderContext prepare(ChunkRenderer chunkRenderer, BlockPos.Mutable chunkOrigin, boolean [] resultFlags) {
        chunkInfo.prepare(chunkRenderer, chunkOrigin, resultFlags);
        return this;
    }
    
    public void release() {
        chunkInfo.release();
        blockInfo.release();
    }
    
    /** Called from chunk renderer hook. */
    public boolean tesselateBlock(BlockState blockState, BlockPos blockPos) {
        try {
            final BakedModel model = blockRenderManager.getModel(blockState);
            aoCalc.clear();
            blockInfo.prepareForBlock(blockState, blockPos, model.useAmbientOcclusion());
            chunkInfo.beginBlock();
            ((FabricBakedModel)model).emitBlockQuads(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, blockInfo.randomSupplier, this);
        } catch (Throwable var9) {
           CrashReport crashReport_1 = CrashReport.create(var9, "Tesselating block in world - Indigo Renderer");
           CrashReportSection crashReportElement_1 = crashReport_1.addElement("Block being tesselated");
           CrashReportSection.addBlockInfo(crashReportElement_1, blockPos, blockState);
           throw new CrashException(crashReport_1);
        }
        return chunkInfo.resultFlags[blockInfo.defaultLayerIndex];
     }

    @Override
    public Consumer<Mesh> meshConsumer() {
        return meshConsumer;
    }

    @Override
    public Consumer<BakedModel> fallbackConsumer() {
        return fallbackConsumer;
    }

    @Override
    public QuadEmitter getEmitter() {
        return meshConsumer.getEmitter();
    }
}
