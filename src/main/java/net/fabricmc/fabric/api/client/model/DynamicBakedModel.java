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
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

// TODO NORELEASE add Nullables
public interface DynamicBakedModel extends BakedModel {
	@Override
	default List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
		List<BakedQuad> list = Lists.newArrayList();
		gatherQuads(null, null, state, face, random, list::add);
		return list;
	}

	// TODO NORELEASE: Consumer<BakedQuad> is better for cases in which we always generate dynamically, but
	// passing a List<BakedQuad> would be more optimized for cases in which the data is "mostly hits".
	// How should we support both approaches?
	// TODO NORELEASE: Should this provide just the RenderData object (which could simply include a view/pos reference), or all three, or just the two
	// + use gets? (one array lookup either way)
	void gatherQuads(RenderCacheView view, BlockPos pos, BlockState state, Direction face, Random random, Consumer<BakedQuad> quadConsumer);
}
