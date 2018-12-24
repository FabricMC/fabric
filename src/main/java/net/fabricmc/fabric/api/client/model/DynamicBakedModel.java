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

import com.google.common.collect.Lists;
import net.fabricmc.fabric.impl.client.model.RenderCacheHelperImpl;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

// TODO NORELEASE add Nullables
public interface DynamicBakedModel<RenderDataType> extends BakedModel {
	@Override
	default List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
		return getQuads(null, RenderCacheHelperImpl.getRenderLayer(state), state, face, random);
	}

	/**
	 * Get the render data for this dynamic baked model.
	 *
	 * Please note that this is called OUTSIDE the main thread! This means two things:
	 *
	 * (a) your call should be thread-safe - in vanilla's case, this means block states,
	 *     fluid states, cached block entity render data and - to some extent - light values,
	 * (b) any heavy computation you're doing, if it fits (a), should be done HERE!
	 */
	RenderDataType getRenderData(BlockState state, RenderCacheView view, BlockPos pos);

	List<BakedQuad> getQuads(/*@Nullable*/ RenderDataType dataType, BlockRenderLayer layer, BlockState state, Direction face, Random random);
}
