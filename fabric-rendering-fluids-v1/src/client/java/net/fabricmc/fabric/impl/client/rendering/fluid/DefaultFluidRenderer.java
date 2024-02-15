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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;

// This class allows FluidRenderHandler#renderFluid to work correctly if it is invoked directly without first invoking
// FluidRenderer#render.
public class DefaultFluidRenderer {
	private static final ThreadLocal<FluidRendererHookContainer> CURRENT_HANDLER = ThreadLocal.withInitial(FluidRendererHookContainer::new);
	private static FluidRenderer vanillaRenderer;

	public static void setVanillaRenderer(FluidRenderer vanillaRenderer) {
		DefaultFluidRenderer.vanillaRenderer = vanillaRenderer;
	}

	public static void render(FluidRenderHandler handler, BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState) {
		FluidRendererHookContainer ctr = CURRENT_HANDLER.get();
		ctr.setup(handler, world, pos, fluidState);

		try {
			vanillaRenderer.render(world, pos, vertexConsumer, blockState, fluidState);
		} finally {
			ctr.clear();
		}
	}

	public static FluidRendererHookContainer getCurrentHandler() {
		return CURRENT_HANDLER.get();
	}
}
