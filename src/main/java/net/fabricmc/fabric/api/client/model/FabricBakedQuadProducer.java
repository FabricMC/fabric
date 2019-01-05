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

import net.fabricmc.fabric.api.client.render.RenderPlugin;
import net.minecraft.block.BlockState;
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
public interface FabricBakedQuadProducer {
	/**
	 * Similar in purpose to BakedModel.getQuads(), except:<p>
	 * 
	 * This method will always be called exactly one time per block position per chunk rebuild,
	 * irrespective of which or how many faces or block render layers are included in the model.
	 * Models must output all quads in a single pass.<p>
	 * 
	 * Implementations are expected to exploit this single-pass guarantee to 
	 * to perform lookups or other expensive computations at all once and should 
	 * only cache information for subsequent passes if the information will remain
	 * useful across multiple block positions and/or chunk rebuilds.<p>
     * 
     * Models with face-planar quads must handle face occlusion and omit quads on faces 
     * obscured by neighboring blocks.  Performing this check in the model allows implementations 
     * to exploit internal knowledge of geometry to avoid unnecessary checks.
     * {@link ModelBlockView#shouldOutputSide(net.minecraft.util.math.Direction)} is provided
     * for this purpose<p>
     * 
	 * BakedModel.isAmbientOcclusion() will be ignored by the RenderPlugIn because 
	 * for {@link FabricBakedQuad}s that specify this information per-layer.<p>
	 * 
	 * The RenderView parameter provides access to cached block state, fluid state, 
	 * and lighting information. Models should avoid using {@link ModelBlockView#getBlockEntity(BlockPos)}
	 * to ensure thread safety because this method is called outside the main client thread.
	 * @See {@link ModelBlockView#getCachedRenderData(BlockPos)}<p>
	 * 
	 * With {@link BakedModel#getQuads(BlockState, net.minecraft.util.math.Direction, Random)}, the random 
	 * parameter is normally initialized with the same seed prior to each face/render layer.
	 * Because this method is called only once per block, implementations should reseed the 
	 * provided Random for models that expect it. This will especially important for implementations 
	 * that "wrap" existing models that do not implement this interface.<p>
	 * 
	 * The producer/consumer pattern is used here so that implementations are not
	 * forced to create collection instances. This helps to avoid allocation overhead
	 * for mods doing dynamic mesh generation / baking at render time. (The consumer 
	 * will already exist in the render pipeline, and so there is no overhead on that 
	 * side of the relation.)<p>
	 * 
	 * For thread safety and data consistency, model must not retain references to any input parameters.<p>
	 * 
	 * @param blockView	Access to cached world state, render state, & side tests.
	 * @param state			BlockState at model position.
	 * @param pos			Position of block model in the world. 
	 * @param random		Randomizer. See notes above. Not pre-initialized.	
	 * @param seed          Seed to initialize randomizer. 		
	 */
	void produceFabricBakedQuads(ModelBlockView blockView, BlockState state, BlockPos pos, Random random, long seed, Consumer<FabricBakedQuad> consumer);
}
