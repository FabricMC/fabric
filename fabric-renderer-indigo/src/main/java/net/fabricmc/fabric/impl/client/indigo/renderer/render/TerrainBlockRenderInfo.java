/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.fabric.impl.client.indigo.renderer.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class TerrainBlockRenderInfo extends BlockRenderInfo {
	private int cullCompletionFlags;
	private int cullResultFlags;

	@Override
	public void prepareForBlock(BlockState blockState, BlockPos blockPos, boolean modelAO) {
		super.prepareForBlock(blockState, blockPos, modelAO);
		cullCompletionFlags = 0;
		cullResultFlags = 0;
	}

	@Override
	boolean shouldDrawFace(Direction face) {
		if (face == null) {
			return true;
		}

		final int mask = 1 << face.getId();

		if ((cullCompletionFlags & mask) == 0) {
			cullCompletionFlags |= mask;

			if (Block.shouldDrawSide(blockState, blockView, blockPos, face)) {
				cullResultFlags |= mask;
				return true;
			} else {
				return false;
			}
		} else {
			return (cullResultFlags & mask) != 0;
		}
	}
}
