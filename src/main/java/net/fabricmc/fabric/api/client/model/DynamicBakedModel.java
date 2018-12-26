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

import net.fabricmc.fabric.impl.client.model.RenderCacheHelperImpl;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Random;

// TODO NORELEASE add Nullables

/**
 * Interface for baked models which wish to generate quad data based on dynamically acquired
 * information (for example, neighbouring blocks or its block entity) as opposed to just the
 * BlockState.
 *
 * @see RenderCacheView
 * @see RenderDataProvidingBlockEntity
 *
 * @param <RenderDataType> The type of the object returned from {@link DynamicBakedModel#getRenderData(BlockState, RenderCacheView, BlockPos)}.
 */
public interface DynamicBakedModel<RenderDataType> extends BakedModel {
	/**
	 * Default implementation of {@link BakedModel#getQuads(BlockState, Direction, Random)},
	 * used for backwards compatibility and rendering item forms of the model.
	 *
	 * For overriding models based on the item information, please look into
	 * {@link BakedModel#getItemPropertyOverrides()}.
	 */
	@Override
	default List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
		return getQuads(null, RenderCacheHelperImpl.getRenderLayer(state), state, face, random);
	}

	/**
	 * Get the render data for this dynamic baked model.
	 *
	 * This method will be called during the regular chunk rendering pipeline on the
	 * chunk rendering thread and its output passed to getQuads(). The amount of times
	 * it is going to be called is implementation-specific.
	 *
	 * As this method is called outside the main thread, special attention has to be paid
	 * to thread safety.
	 *
	 * - Block states, fluid states and light values are safe to access by other threads.
	 * - Block entities are NOT safe to access by other threads. In the general case, please use
	 * the {@link RenderDataProvidingBlockEntity} on your BlockEntity and the
	 * {@link RenderCacheView#getCachedRenderData(BlockPos)} method to gather
	 * a data-containing object from the main thread and receive it safely on the render thread.
	 * However, as said method is called on the main thread, make sure to keep its processing
	 * as lean as possible - the more calculations you do calculations on another thread,
	 * the more time the main thread has to process other aspects of the engine.
	 * - If you really need to access information of other block entities, please keep in mind
	 * that they may be in the middle of ticking while your processing is being done. Do not
	 * assume any consistency in such block entity objects.
	 */
	RenderDataType getRenderData(BlockState state, RenderCacheView view, BlockPos pos);

	/**
	 * Get the
	 * @param data The render data passed from {@link DynamicBakedModel#getRenderData(BlockState, RenderCacheView, BlockPos)},
	 * or null if it is not available.
	 * @param layer The block rendering layer. If you would like to use multiple rendering layers,
	 * please look at {@link MultiRenderLayerBlock}.
	 * @param state The
	 * @param face
	 * @param random
	 * @return
	 */
	List<BakedQuad> getQuads(/*@Nullable*/ RenderDataType data, BlockRenderLayer layer, BlockState state, Direction face, Random random);
}
