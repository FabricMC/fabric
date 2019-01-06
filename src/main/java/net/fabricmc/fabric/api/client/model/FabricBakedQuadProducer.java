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

package net.fabricmc.fabric.api.client.model;

import java.util.Random;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.client.render.FabricVertexFormat;
import net.fabricmc.fabric.api.client.render.RenderPlugin;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;

/**
 * Interface {@link RenderPlugin} implementations will use to obtain {@link FabricBakedQuad}s
 * for vertex lighting and buffering.<p>
 * 
 * All {@link BakedModel} instances have a default implementation of this interface via Mixin and
 * can be safely cast to this interface for vertex lighting and buffering.<p>
 * 
 * Implementations of {@link FabricBakedModel} should implement this interface without
 * reference to or reliance on the default implementation in {@link BakedModel}.
 */
@FunctionalInterface
public interface FabricBakedQuadProducer {
    /**
     * Similar in purpose to BakedModel.getQuads(), with significant differences:<p>
     * 
     * <H1>General Notes</H1><p>
     *      
     * The {@link #produceFabricBakedQuads(ModelBlockView, BlockState, BlockPos, Random, long, Consumer)} method 
     * is used for block rendering, block entity rendering and item rendering, with 
     * differences described in the sections below.  In all contexts, the method
     * will be called exactly once, and must produce all quads that should be rendered.<p>
     *  
     * Implementations are expected to exploit this single-pass guarantee to 
     * to perform lookups or other expensive computations at all once and should 
     * only cache information for subsequent passes if the information will remain
     * useful across multiple invocations.<p>
     *
     * The producer/consumer pattern is used here so that implementations are not
     * forced to create collection instances. This helps to avoid allocation overhead
     * for mods doing dynamic mesh generation / baking at render time. (The consumer 
     * will already exist in the render pipeline, and so there is no overhead on that 
     * side of the relation.)<p>
     * 
     * For thread safety and data consistency, model must not retain references to any input parameters.<p>
     * 
     * <H1>Block Rendering</H1><p>
     * 
     * Implementation must infer that block model quads are requested when the block-specific
     * parameters (world view, block state and block position) are non-null.  Quads returned
     * in that context MUST use a block-compatible vertex format. (The first 28 elements must
     * match standard Minecraft block format.)  Render plug-ins are not <em>required</em> to translate
     * vertex formats if a mismatched format is provided, which could lead to visual defects.<p>
     * 
     * If vertex format is {@link FabricVertexFormat#STANDARD_UNSPECIFIED}
     * the render plug in will assume the vertex format is {@link FabricVertexFormat#STANDARD_BLOCK},
     * when this is a block-rendering context.  This should only be the case for standard Minecraft
     * {@link BakedQuad}s that are being cast to {@link FabricBakedQuad}.<p>
     * 
     * As in other contexts, this method will always be called exactly one time per block position 
     * per chunk rebuild, irrespective of which or how many faces or block render layers are included 
     * in the model. Models must output all quads in a single pass.<p>
     * 
     * Models with face-planar quads must handle face occlusion and omit quads on faces 
     * obscured by neighboring blocks.  Performing this check in the model allows implementations 
     * to exploit internal knowledge of geometry to avoid unnecessary checks.<p>
     * 
     * If vertex format for quads is something other than {@link FabricVertexFormat#STANDARD_UNSPECIFIED}
     * BakedModel.isAmbientOcclusion() will be ignored by the RenderPlugIn because it will 
     * expect {@link FabricBakedQuad}s to specify that information per-layer.<p>
     * 
     * The RenderView parameter provides access to cached block state, fluid state, 
     * and lighting information. Models should avoid using {@link ModelBlockView#getBlockEntity(BlockPos)}
     * to ensure thread safety because this method is called outside the main client thread.
     * Look to {@link ModelBlockView#getCachedRenderData(BlockPos)} for more information.<p>
     * 
     * With {@link BakedModel#getQuads(BlockState, net.minecraft.util.math.Direction, Random)}, the random 
     * parameter is normally initialized with the same seed prior to each face/render layer.
     * Because this method is called only once per block, implementations should reseed the 
     * provided Random for models that expect it. This will especially important for implementations 
     * that "wrap" existing models that do not implement this interface.<p>
     *
     * <H1>Block Entity Rendering</H1><p>
     * 
     * This method will be called to re-buffer block models for {@link BlockEntity}s that implement
     * {@link FastBlockEntityRenderer}.  Behavior in that case is almost identical to a conventional
     * block render, except that the block view parameter will always be null, because cached world
     * state will not be available. Implementations using that feature must capture all information that
     * relies on world state in an earlier call to {@link RenderDataProvidingBlockEntity#getRenderData()}.<p>
     * 
     * Consult {@link FastBlockEntityRenderer} for additional information.<p>
     *  
     *
     * <H1>Item Rendering</H1><p>
     * 
     * Implementations must infer that item model quads are requested when the block-specific
     * parameters (world view, block state and block position) are null.  Quads returned
     * in that context MUST use an item-compatible vertex format. (The first 28 elements must
     * match standard Minecraft item format.)  Render plug-ins are not <em>required</em> to 
     * translate vertex formats if a mismatched format is provided, which could lead to visual defects.<p>
     * 
     * If the vertex format of any quad is {@link FabricVertexFormat#STANDARD_UNSPECIFIED}
     * the render plug in will assume the vertex format is {@link FabricVertexFormat#STANDARD_ITEM},
     * when this call is made in an item-rendering context.  This should only be the case for standard Minecraft
     * {@link BakedQuad}s that are being cast to {@link FabricBakedQuad}.<p>
     * 
     * To remain consistent with Minecraft item rendering, the random parameter sent by the 
     * render plug in will be non-null, and the seed will always be 42.<p>
     * 
     * Item quads models are not expected to have dynamic customization at this stage.  Customization
     * of item models happens via {@link BakedModel#getItemPropertyOverrides()}, as it does with
     * standard {@link BakedModel}s.  This model should be the result of that method when {@link #produceFabricBakedQuads(ModelBlockView, BlockState, BlockPos, Random, long, Consumer)}
     * is called, and any necessary customization should have already occurred.<p>
     * 
     * @param blockView	    Access to cached world state, render state
     * @param state			BlockState at model position.
     * @param pos			Position of block model in the world. 
     * @param random		Randomizer. See notes above. Not pre-initialized.	
     * @param seed          Seed to initialize randomizer. 		
     */
    void produceFabricBakedQuads(ModelBlockView blockView, BlockState state, BlockPos pos, Random random, long seed, Consumer<FabricBakedQuad> consumer);
}
