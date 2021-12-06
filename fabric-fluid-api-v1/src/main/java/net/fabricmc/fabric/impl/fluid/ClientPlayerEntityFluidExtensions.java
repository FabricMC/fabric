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

package net.fabricmc.fabric.impl.fluid;

import org.jetbrains.annotations.NotNull;

import net.minecraft.fluid.FluidState;

/**
 * Implements some fluid-related client-side player entity features.
 */
public interface ClientPlayerEntityFluidExtensions {
	/**
	 * Executed when a player enters a fluid.
	 * @param fluidState Fluid in which the player is entered.
	 */
	void enterInFluid(@NotNull FluidState fluidState);

	/**
	 * Executed when a player exit from a fluid.
	 * @param fluidState Fluid from which the player is exited.
	 */
	void exitFromFluid(@NotNull FluidState fluidState);

	/**
	 * Executed when a player exit from a fluid and enters another.
	 * @param oldFluidState Fluid from which the player is exited.
	 * @param newFluidState Fluid in which the player is entered.
	 */
	void changedFluid(@NotNull FluidState oldFluidState, @NotNull FluidState newFluidState);
}
