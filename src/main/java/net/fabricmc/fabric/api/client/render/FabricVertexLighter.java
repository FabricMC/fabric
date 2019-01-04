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

import java.util.List;

import net.fabricmc.fabric.mixin.client.render.MixinBlockModelRenderer.ModelData;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilder;
import net.minecraft.client.render.model.BakedQuad;

public interface FabricVertexLighter {

	/**
	 * Outputs one or more quads to appropriate buffers with lighting and coloring
	 * based on provided metadata. Handles randomized position offsets 
	 * and block tinting as needed. Block tint is per-quad based on enable flags in metadata.
	 * 
	 * Face culling has already happened before this point.
	 */
	void lightFabricBakedQuad(int[] vertexData, int index, BlockLayeredBufferBuilder builders,
			ModelData data);

	void lightStandardBakedQuads(List<BakedQuad> quads, BlockLayeredBufferBuilder builders, ModelData data,
			BlockRenderLayer renderLayer, boolean useAO);

}