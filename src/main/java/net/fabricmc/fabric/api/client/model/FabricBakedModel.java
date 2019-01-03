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

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Interface for static models that use the extended quad/vertex specification
 * (emissive lighting, multi-layer, etc) available in FabricBakedQuads.<p>
 */
public interface FabricBakedModel extends BakedModel {
	/**
	 * Similar in purpose to BakedModel.getQuads(), except:<p>
	 * 
	 * <li> This method will always be called exactly one time per chunk rebuild,
	 * irrespective of which or how many block render layers included in the model.
	 * Models must output all quads in a single pass. Occlusion culling is handled
	 * automatically using face meta-data that include with the vertex data.</li><p>
	 * 
	 * <li> BakedModel.isAmbientOcclusion() is ignored because fabric quads
	 * specify this pre-bake, per layer.  The information is instead retrieved from 
	 * the vertex data in the quad.</li><p>
	 * 
	 * <li> The RenderView parameter provides access to cached
	 * block state, fluid state, and lighting information. Block Entity access
	 * will always return null to ensure thread safety because this method
	 * is called outside the main client thread.</li><p>
	 * 
	 * Because the method is called only one for all faces and render layers, 
	 * dynamic models can perform lookups or other expensive computations 
	 * at all once and do not need to cache results for other passes unless 
	 * the information will be useful across chunk rebuilds.<p>
	 * 
	 * Caller will not retain a list reference and list will be referenced
	 * using only indexed get methods. (No Iterator instance will be requested.)
	 * Implementations that generate quads dynamically are encouraged to reuse 
	 * list instances or facade objects to avoid object allocation at render time. 
	 * 
	 * Important distinction regarding random: getFabricBlockQuads is called only 1X.  
	 * Normally random is initialized with the same seed prior to each face/render layer.
	 * For sake of performance this isn't done with fabric baked models.  
	 * Implementations must apply the same random bits to parts of the model that expect it.<p>
	 * 
	 * Model should not retain references to any input parameters because they 
	 * may be mutable and/or threadlocal.<p>
	 * 
	 * @param renderView	Access to cached world state, render state.
	 * @param pos			Position of block model in the world. 
	 * @param state			BlockState at model position.
	 * @param random		Randomizer. See notes above.			
	 * @return				List of FabricBakedQuad.
	 *      				Caller will not retain a list reference and list will be referenced via get().
	 * 						Implementations that generate quads dynamically are encouraged to reuse 
	 * 						list instances or facade objects to avoid object allocation at render time. 
	 */
	List<FabricBakedQuad> getFabricBlockQuads(RenderCacheView renderView, BlockState state, BlockPos pos, Random random);

	@Override
	default List<BakedQuad> getQuads(BlockState var1, Direction var2, Random var3) {

		// TODO Add default handlers for compatibility.

		// This will never be called for block rendering, but will be called
		// for block breaking render, and possibly by other mods looking to "wrap" the model
		// or do something else with it, (e.g. multipart) and which don't recognize this interface.

		// Block breaking render only uses geometry so should be an optimized
		// special case, especially for multi - layer quads.  

		// May be good to also provide an abstract reference implementation so 
		// results can be lazily constructed and cached and encourage authors to extend
		// or do similar with the provided handlers.

		return null;
	}



}
