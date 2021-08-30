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

package net.fabricmc.fabric.test.client.rendering.fluid;

import org.jetbrains.annotations.Nullable;

import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;

public class ColoredFluidRenderer extends SimpleFluidRenderHandler {
	private final int tint;

	public ColoredFluidRenderer(Identifier stillTexture, Identifier flowingTexture, Identifier overlayTexture, int tint) {
		super(stillTexture, flowingTexture, overlayTexture);
		this.tint = tint;
	}

	public ColoredFluidRenderer(Identifier stillTexture, Identifier flowingTexture, int tint) {
		super(stillTexture, flowingTexture);
		this.tint = tint;
	}

	@Override
	public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return tint;
	}
}
