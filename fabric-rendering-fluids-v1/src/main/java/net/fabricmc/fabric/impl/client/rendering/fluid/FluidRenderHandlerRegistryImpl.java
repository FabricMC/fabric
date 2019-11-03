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

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biomes;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;

public class FluidRenderHandlerRegistryImpl implements FluidRenderHandlerRegistry {
	public static final FluidRenderHandlerRegistryImpl INSTANCE = new FluidRenderHandlerRegistryImpl();
	private final Map<Fluid, FluidRenderHandler> handlers = new IdentityHashMap<>();
	private final Map<Fluid, FluidRenderHandler> modHandlers = new IdentityHashMap<>();

	private FluidRenderHandlerRegistryImpl() {
	}

	@Override
	public FluidRenderHandler get(Fluid fluid) {
		return handlers.get(fluid);
	}

	public FluidRenderHandler getOverride(Fluid fluid) {
		return modHandlers.get(fluid);
	}

	@Override
	public void register(Fluid fluid, FluidRenderHandler renderer) {
		handlers.put(fluid, renderer);
		modHandlers.put(fluid, renderer);
	}

	public void onFluidRendererReload(Sprite[] waterSprites, Sprite[] lavaSprites) {
		FluidRenderHandler waterHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
				return waterSprites;
			}

			@Override
			public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
				if (view != null && pos != null) {
					return BiomeColors.getWaterColor(view, pos);
				} else {
					return Biomes.DEFAULT.getWaterColor();
				}
			}
		};

		//noinspection Convert2Lambda
		FluidRenderHandler lavaHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
				return lavaSprites;
			}
		};

		register(Fluids.WATER, waterHandler);
		register(Fluids.FLOWING_WATER, waterHandler);
		register(Fluids.LAVA, lavaHandler);
		register(Fluids.FLOWING_LAVA, lavaHandler);
		handlers.putAll(modHandlers);
	}
}
