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
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.test.fluid.core.ModCore;

public class MFluids {
	public static final Identifier BLUE_FLUID_ID = new Identifier(ModCore.ID, "blue_fluid");
	public static final Identifier BLUE_FLUID_TID = new Identifier(ModCore.ID + ":block/blue_fluid_still");
	public static final BlueFluid BLUE_FLUID = Registry.register(Registry.FLUID, BLUE_FLUID_ID, new BlueFluid.Still());

	public static final Identifier BLUE_FLUID_FLOWING_ID = new Identifier(ModCore.ID, "blue_fluid_flowing");
	public static final Identifier BLUE_FLUID_FLOWING_TID = new Identifier(ModCore.ID + ":block/blue_fluid_flow");
	public static final BlueFluid BLUE_FLUID_FLOWING = Registry.register(Registry.FLUID, BLUE_FLUID_FLOWING_ID, new BlueFluid.Flowing());

	public static final Identifier RED_FLUID_ID = new Identifier(ModCore.ID, "red_fluid");
	public static final Identifier RED_FLUID_TID = new Identifier(ModCore.ID + ":block/red_fluid_still");
	public static final RedFluid RED_FLUID = Registry.register(Registry.FLUID, RED_FLUID_ID, new RedFluid.Still());

	public static final Identifier RED_FLUID_FLOWING_ID = new Identifier(ModCore.ID, "red_fluid_flowing");
	public static final Identifier RED_FLUID_FLOWING_TID = new Identifier(ModCore.ID + ":block/red_fluid_flow");
	public static final RedFluid RED_FLUID_FLOWING = Registry.register(Registry.FLUID, RED_FLUID_FLOWING_ID, new RedFluid.Flowing());

	public static final Identifier GREEN_FLUID_ID = new Identifier(ModCore.ID, "green_fluid");
	public static final GreenFluid GREEN_FLUID = Registry.register(Registry.FLUID, GREEN_FLUID_ID, new GreenFluid.Still());

	public static final Identifier GREEN_FLUID_FLOWING_ID = new Identifier(ModCore.ID, "green_fluid_flowing");
	public static final GreenFluid GREEN_FLUID_FLOWING = Registry.register(Registry.FLUID, GREEN_FLUID_FLOWING_ID, new GreenFluid.Flowing());

	public static void load() {
	}

	public static void renderFluids() {
		FluidRenderHandlerRegistry.INSTANCE.register(BLUE_FLUID, BLUE_FLUID_FLOWING,
				new SimpleFluidRenderHandler(BLUE_FLUID_TID, BLUE_FLUID_FLOWING_TID, 0x0000ff));
		FluidRenderHandlerRegistry.INSTANCE.register(RED_FLUID, RED_FLUID_FLOWING,
				new SimpleFluidRenderHandler(RED_FLUID_TID, RED_FLUID_FLOWING_TID, 0xff0000));
		FluidRenderHandlerRegistry.INSTANCE.register(GREEN_FLUID, GREEN_FLUID_FLOWING,
				new SimpleFluidRenderHandler(RED_FLUID_TID, RED_FLUID_FLOWING_TID, 0x00ff00));

		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(BLUE_FLUID_TID);
			registry.register(BLUE_FLUID_FLOWING_TID);
			registry.register(RED_FLUID_TID);
			registry.register(RED_FLUID_FLOWING_TID);
		});

		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), BLUE_FLUID, BLUE_FLUID_FLOWING);
		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), RED_FLUID, RED_FLUID_FLOWING);
		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), GREEN_FLUID, GREEN_FLUID_FLOWING);
	}
}
