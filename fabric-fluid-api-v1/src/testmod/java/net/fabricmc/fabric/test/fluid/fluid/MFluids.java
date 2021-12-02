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

package net.fabricmc.fabric.test.fluid.fluid;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.test.fluid.core.FluidRenderer;
import net.fabricmc.fabric.test.fluid.core.ModCore;

public class MFluids {
	public static final Identifier BLUE_FLUID_ID = new Identifier(ModCore.ID, "blue_fluid");
	public static final BlueFluid BLUE_FLUID = Registry.register(Registry.FLUID, BLUE_FLUID_ID, new BlueFluid.Still());

	public static final Identifier BLUE_FLUID_FLOWING_ID = new Identifier(ModCore.ID, "blue_fluid_flowing");
	public static final BlueFluid BLUE_FLUID_FLOWING = Registry.register(Registry.FLUID, BLUE_FLUID_FLOWING_ID, new BlueFluid.Flowing());

	public static final Identifier CYAN_FLUID_ID = new Identifier(ModCore.ID, "cyan_fluid");
	public static final CyanFluid CYAN_FLUID = Registry.register(Registry.FLUID, CYAN_FLUID_ID, new CyanFluid.Still());

	public static final Identifier CYAN_FLUID_FLOWING_ID = new Identifier(ModCore.ID, "cyan_fluid_flowing");
	public static final CyanFluid CYAN_FLUID_FLOWING = Registry.register(Registry.FLUID, CYAN_FLUID_FLOWING_ID, new CyanFluid.Flowing());

	public static final Identifier GREEN_FLUID_ID = new Identifier(ModCore.ID, "green_fluid");
	public static final GreenFluid GREEN_FLUID = Registry.register(Registry.FLUID, GREEN_FLUID_ID, new GreenFluid.Still());

	public static final Identifier GREEN_FLUID_FLOWING_ID = new Identifier(ModCore.ID, "green_fluid_flowing");
	public static final GreenFluid GREEN_FLUID_FLOWING = Registry.register(Registry.FLUID, GREEN_FLUID_FLOWING_ID, new GreenFluid.Flowing());

	public static final Identifier RED_FLUID_ID = new Identifier(ModCore.ID, "red_fluid");
	public static final RedFluid RED_FLUID = Registry.register(Registry.FLUID, RED_FLUID_ID, new RedFluid.Still());

	public static final Identifier RED_FLUID_FLOWING_ID = new Identifier(ModCore.ID, "red_fluid_flowing");
	public static final RedFluid RED_FLUID_FLOWING = Registry.register(Registry.FLUID, RED_FLUID_FLOWING_ID, new RedFluid.Flowing());

	public static void load() {
	}

	public static void renderFluids() {
		FluidRenderer.render(BLUE_FLUID, BLUE_FLUID_FLOWING, BLUE_FLUID_ID, 0x0000ff);
		FluidRenderer.render(CYAN_FLUID, CYAN_FLUID_FLOWING, BLUE_FLUID_ID, 0x00ffff);
		FluidRenderer.render(GREEN_FLUID, GREEN_FLUID_FLOWING, RED_FLUID_ID, 0x00ff00);
		FluidRenderer.render(RED_FLUID, RED_FLUID_FLOWING, RED_FLUID_ID, 0xff0000);

		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), BLUE_FLUID, BLUE_FLUID_FLOWING);
		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), CYAN_FLUID, CYAN_FLUID_FLOWING);
		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), GREEN_FLUID, GREEN_FLUID_FLOWING);
		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), RED_FLUID, RED_FLUID_FLOWING);
	}
}
