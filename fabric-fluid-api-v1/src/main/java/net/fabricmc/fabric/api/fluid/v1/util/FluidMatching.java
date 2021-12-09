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

import org.jetbrains.annotations.NotNull;

import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.Tag;

import net.fabricmc.fabric.api.fluid.v1.FabricFlowableFluid;

/**
 * Utilities about comparing fluids.
 */
@SuppressWarnings("unused")
public class FluidMatching {
	/**
	 * Checks if two FluidState are equal.
	 *
	 * @param fluidState1 First FluidState.
	 * @param fluidState2 Second FluidState.
	 * @return True if the two FluidState are equal.
	 */
	public static boolean areEqual(FluidState fluidState1, FluidState fluidState2) {
		return fluidState1 != null && fluidState2 != null && areEqual(fluidState1.getFluid(), fluidState2.getFluid());
	}

	/**
	 * Checks if two Fluid are equal.
	 *
	 * @param fluid1 First Fluid.
	 * @param fluid2 Second Fluid.
	 * @return True if the two Fluid are equal.
	 */
	public static boolean areEqual(Fluid fluid1, Fluid fluid2) {
		return fluid1 != null && fluid2 != null && fluid1.matchesType(fluid2);
	}

	/**
	 * Checks if a FluidState is in a specified tag.
	 *
	 * @param fluidState FluidState to check.
	 * @param tag Tag to check.
	 * @return True if the FluidState is non-null and is in the specified tag.
	 */
	public static boolean isIn(FluidState fluidState, @NotNull Tag<Fluid> tag) {
		return fluidState != null && fluidState.isIn(tag);
	}

	/**
	 * Checks if a FluidState is in a specified tag.
	 *
	 * @param fluid Fluid to check.
	 * @param tag Tag to check.
	 * @return True if the Fluid is non-null and is in the specified tag.
	 */
	public static boolean isIn(Fluid fluid, @NotNull Tag<Fluid> tag) {
		return fluid != null && fluid.isIn(tag);
	}

	/**
	 * @param state FluidState to check if is a custom fabric fluid.
	 * @return True if the fluid is a custom fabric fluid.
	 */
	public static boolean isFabricFluid(FluidState state) {
		return state != null && isFabricFluid(state.getFluid());
	}

	/**
	 * @param fluid Fluid to check if is a custom fabric fluid.
	 * @return True if the fluid is a custom fabric fluid.
	 */
	public static boolean isFabricFluid(Fluid fluid) {
		return fluid instanceof FabricFlowableFluid;
	}
}
