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

import net.fabricmc.fabric.api.fluid.v1.rendering.FluidRendering;
import net.fabricmc.fabric.test.fluid.core.ModCore;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MFluids {
	public static final Identifier BLUE_FLUID_ID = new Identifier(ModCore.ID, "blue_fluid");
	public static final BlueFluid BLUE_FLUID = Registry.register(Registry.FLUID, BLUE_FLUID_ID, new BlueFluid.Still());

	public static final Identifier BLUE_FLUID_FlOWING_ID = new Identifier(ModCore.ID, "blue_fluid_flowing");
	public static final BlueFluid BLUE_FLUID_FlOWING = Registry.register(Registry.FLUID, BLUE_FLUID_FlOWING_ID, new BlueFluid.Flowing());

	public static final Identifier RED_FLUID_ID = new Identifier(ModCore.ID, "red_fluid");
	public static final RedFluid RED_FLUID = Registry.register(Registry.FLUID, RED_FLUID_ID, new RedFluid.Still());

	public static final Identifier RED_FLUID_FlOWING_ID = new Identifier(ModCore.ID, "red_fluid_flowing");
	public static final RedFluid RED_FLUID_FlOWING = Registry.register(Registry.FLUID, RED_FLUID_FlOWING_ID, new RedFluid.Flowing());

	public static final Identifier GREEN_FLUID_ID = new Identifier(ModCore.ID, "green_fluid");
	public static final GreenFluid GREEN_FLUID = Registry.register(Registry.FLUID, GREEN_FLUID_ID, new GreenFluid.Still());

	public static final Identifier GREEN_FLUID_FlOWING_ID = new Identifier(ModCore.ID, "green_fluid_flowing");
	public static final GreenFluid GREEN_FLUID_FlOWING = Registry.register(Registry.FLUID, GREEN_FLUID_FlOWING_ID, new GreenFluid.Flowing());


	public static void load() {}

	public static void renderFluids() {
		FluidRendering.render(BLUE_FLUID, BLUE_FLUID_FlOWING, new Identifier("minecraft", "water"), 0x0000ff);
		FluidRendering.render(RED_FLUID, RED_FLUID_FlOWING, RED_FLUID_ID, 0xff0000);
		FluidRendering.render(GREEN_FLUID, GREEN_FLUID_FlOWING, new Identifier("minecraft", "water"), 0x00ff00);
	}
}
