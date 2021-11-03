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

package net.fabricmc.fabric.api.fluid.v1.util;

import net.fabricmc.fabric.api.fluid.v1.ExtendedFlowableFluid;
import net.fabricmc.fabric.api.fluid.v1.tag.FabricFluidTags;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities for fluids.
 */
public class FluidUtils {
	/**
	 * @param state FluidState to check if is navigable.
	 * @return true if the fluid is navigable.
	 */
	public static boolean isNavigable(@NotNull FluidState state) {
		return isNavigable(state.getFluid());
	}

	/**
	 * @param fluid Fluid to check if is navigable.
	 * @return true if the fluid is navigable.
	 */
	public static boolean isNavigable(@NotNull Fluid fluid) {
		return fluid.isIn(FabricFluidTags.FABRIC_FLUID)
				//By default, all fabric_fluid are navigable
				? !(fluid instanceof ExtendedFlowableFluid eFluid) || eFluid.isNavigable()
				: fluid.isIn(FluidTags.WATER);
	}

	/**
	 * @param state FluidState to check if is swimmable.
	 * @return true if the fluid is swimmable.
	 */
	public static boolean isSwimmable(@NotNull FluidState state) {
		return isSwimmable(state.getFluid());
	}

	/**
	 * @param fluid Fluid to check if is swimmable.
	 * @return true if the fluid is swimmable.
	 */
	public static boolean isSwimmable(@NotNull Fluid fluid) {
		return fluid.isIn(FabricFluidTags.FABRIC_FLUID)
				//By default, is possible to swim in all fabric_fluid
				? !(fluid instanceof ExtendedFlowableFluid eFluid) || eFluid.isSwimmable()
				: fluid.isIn(FluidTags.WATER) || fluid.isIn(FluidTags.LAVA);
	}
}
