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
package net.fabricmc.fabric.api.client.render;

import net.fabricmc.fabric.api.client.model.FabricBakedQuad;
import net.fabricmc.fabric.mixin.client.render.MixinBlockModelRenderer.ModelData;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilder;

public class FabricVertexLighter {

	final AoCalculator aoCalc = new AoCalculator();
	
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
	public final void lightFabricBlockModel(FabricBakedQuad quad, int[] vertexData, int index,
			BlockLayeredBufferBuilder builders, ModelData data) {
		// TODO Auto-generated method stub
		
	}
}
