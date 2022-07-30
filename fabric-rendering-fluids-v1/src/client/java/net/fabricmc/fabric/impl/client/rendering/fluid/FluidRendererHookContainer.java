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

package net.fabricmc.fabric.impl.client.rendering.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;

public class FluidRendererHookContainer {
	public BlockRenderView view;
	public BlockPos pos;
	public BlockState blockState;
	public FluidState fluidState;
	public FluidRenderHandler handler;
	public final Sprite[] sprites = new Sprite[2];
	public Sprite overlay;
	public boolean hasOverlay;

	public void getSprites(BlockRenderView world, BlockPos pos, FluidState fluidState) {
		if (handler != null) {
			Sprite[] sprites = handler.getFluidSprites(world, pos, fluidState);

			this.sprites[0] = sprites[0];
			this.sprites[1] = sprites[1];

			if (sprites.length > 2) {
				hasOverlay = true;
				overlay = sprites[2];
			}
		} else {
			hasOverlay = false;
		}
	}

	public void clear() {
		view = null;
		pos = null;
		blockState = null;
		fluidState = null;
		handler = null;
		sprites[0] = null;
		sprites[1] = null;
		overlay = null;
		hasOverlay = false;
	}
}
