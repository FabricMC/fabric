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

package net.fabricmc.fabric.api.client.model.fabric;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

/**
 * Interface for baked models that need to generate or customize vertex data based on 
 * world state instead of or in addition to block state when render chunks are rebuilt.<p>
 * 
 * Dynamic elements are additive - models with static quads should output static quads 
 * via the less expensive {@link PackagedQuadConsumer} interface. The provided consumer
 * will implement this interface also.<p>
 * 
 * Note for {@link Renderer} implementors: Fabric causes BakedModel to extend this
 * interface with {@link #hasVertexData()} and to produce standard vertex data. This means any BakedModel instance
 * can be safely cast to this interface without an instanceof check.
 */
@FunctionalInterface
public interface BlockModelQuadProducer {
    /**
     * This method will be called during chunk rebuilds to generate both the static and
     * dynamic portions of a block model when the model implements this interface.<p>
     * 
     * This method will always be called exactly one time per block position 
     * per chunk rebuild, irrespective of which or how many faces or block render layers are included 
     * in the model. Models must output all quads/meshes in a single pass.<p>
     * 
     * Renderer will handle face occlusion and filter quads on faces 
     * obscured by neighboring blocks.  Models only need to consider "sides" to the
     * extent the model is driven by connection with neighbor blocks or other world state.<p>
     * 
     * The RenderView parameter provides access to cached block state, fluid state, 
     * and lighting information. Models should avoid using {@link ModelBlockView#getBlockEntity(BlockPos)}
     * to ensure thread safety because this method is called outside the main client thread.
     * Models that require Block Entity data should implement {@link DynamicModelBlockEntity}.
     * Look to {@link ModelBlockView#getCachedRenderData(BlockPos)} for more information.<p>
     * 
     * Note: with {@link BakedModel#getQuads(BlockState, net.minecraft.util.math.Direction, Random)}, the random 
     * parameter is normally initialized with the same seed prior to each face layer.
     * Model authors should note this method is called only once per block, and reseed if needed.
     * For wrapped vanilla baked models, it will probably be easier to use {@link RenderContext#fallbackModelConsumer()}.<p>
     */
    void produceQuads(ModelBlockView blockView, BlockState state, BlockPos pos, Random random, long seed, RenderContext context);
}
