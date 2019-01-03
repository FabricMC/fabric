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

import net.minecraft.client.render.model.BakedModel;

/**
 * Implement for BakedModel sub-types that require dynamic, 
 * emissive or multi-layer texturing.<p>
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
     * <li> BakedModel.isAmbientOcclusion() is ignored because enhanced quads
     * specify this pre-bake, per layer.  The information is instead retrieved from 
     * the vertex data in the quad.</li><p>
     * 
     * <li> Single input access object vs multiple parameters. The input object
     * will retrieve/instantiate/prepare attributes only as requested and 
     * reuse cache hits across multiple models.  This also allows for non-breaking
     * extension of block model inputs in the future.</li><p>
     * 
     * This means dynamic models can perform lookups or other expensive computations 
     * within a single call and do not need to cache results for other passes unless 
     * the information will be useful across chunk rebuilds.<p>
     * 
     * Caller will not retain a list reference and list will be referenced
     * using only indexed get methods. (No Iterator instance will be requested.)
     * Implementations that generate quads dynamically are encouraged to reuse 
     * list instances or facade objects to avoid object allocation at render time. 
     * 
     */
     List<FabricBakedQuad> getBlockQuads(BlockModelData modelData);
     
     /**
      * Same purpose as {@link #getBlockQuads(BlockModelData)} but for items.
      * 
      * (Placeholder, very much WIP...)
      */
     List<FabricBakedQuad> getItemQuads(ItemModelData modelData);
}
