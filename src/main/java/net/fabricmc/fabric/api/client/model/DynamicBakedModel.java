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

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

/**
 * Extension of {@link FabricBakedModel} for models needing access to world state other than
 * neighboring block states, fluid states and lighting.  Typical use case is to retrieve
 * Block Entity data, but can be used to access any world state that would normally be
 * unavailable in render threads. <p>
 * 
 * TODO: Evaluate using also for Block Entity Renderers to enable off-thread building.
 * In that case, would be called 1x per tick, or possibly have BE push updates, but that creates lookup overhead.<p>
 * 
 * @see RenderCacheView<p>
 *
 * @param <RenderDataType> The type of the object returned from {@link FabricBakedModel#getRenderData(BlockState, RenderCacheView, BlockPos)}.
 */
public interface DynamicBakedModel<RenderDataType> extends FabricBakedModel {

	/**
	 * Provides render data for this dynamic baked model that must be retrieved
	 * from Block Entity state or any other world state other than neighboring 
	 * block states, fluid state, or lighting.<p>
	 *
	 * This method will be called from the main client thread when a render chunk rebuild
	 * is initiated. The return value will then be available in the RenderCacheView
	 * passed to getFabricBlockQuads on the chunk rendering thread.<p>
	 *
	 * Because this method is called on the main thread, make sure to keep its processing
	 * as lean as possible - the more calculations you do on the render thread,
	 * the more time the main thread has to process other aspects of the engine. <p>
	 * 
	 * Please keep in mind that RenderCacheView will also be available to the model when
	 * baked quads are requested. Therefore implementations do not need to handle neighbor 
	 * lookups or other block state/fluid state lookups here.
	 */
	RenderDataType getRenderData(RenderCacheView view, BlockState state, BlockPos pos);
}
