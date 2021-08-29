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

package net.fabricmc.fabric.api.client.render.fluid.v1;

import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderRegistryImpl;
import net.minecraft.fluid.Fluid;

/**
 * Registry for {@link FluidRenderHandler} and {@link CustomFluidRenderer} instances.
 *
 * <p>Notably, this supports querying, overriding and wrapping vanilla fluid
 * rendering.
 */
public interface FluidRenderRegistry {
	FluidRenderRegistry INSTANCE = FluidRenderRegistryImpl.INSTANCE;

	/**
	 * Get a {@link FluidRenderHandler} for a given Fluid.
	 * Supports vanilla and Fabric fluids.
	 *
	 * @param fluid The Fluid.
	 * @return The FluidRenderHandler.
	 */
	FluidRenderHandler getRenderHandler(Fluid fluid);

	/**
	 * Register a {@link FluidRenderHandler} for a given Fluid.
	 *
	 * @param fluid The Fluid.
	 * @param renderer The FluidRenderHandler.
	 */
	void registerRenderHandler(Fluid fluid, FluidRenderHandler renderer);

	/**
	 * Get a {@link CustomFluidRenderer} for a given Fluid.
	 * Supports vanilla and Fabric fluids, but returns null for unregistered
	 * fluids (vanilla fluids are by default not registered).
	 *
	 * @param fluid The Fluid.
	 * @return The CustomFluidRenderer, or null if none has been registered.
	 */
	CustomFluidRenderer getCustomRenderer(Fluid fluid);

	/**
	 * Register a {@link CustomFluidRenderer} for a given Fluid.
	 *
	 * @param fluid The Fluid.
	 * @param renderer The CustomFluidRenderer.
	 */
	void registerCustomRenderer(Fluid fluid, CustomFluidRenderer renderer);



	// Compatibility methods

	/**
	 * Get a {@link FluidRenderHandler} for a given Fluid.
	 * Supports vanilla and Fabric fluids.
	 *
	 * @param fluid The Fluid.
	 * @return The FluidRenderHandler.
	 *
	 * @deprecated Use {@link #getRenderHandler(Fluid)}
	 */
	@Deprecated
	default FluidRenderHandler get(Fluid fluid) {
		return getRenderHandler(fluid);
	}

	/**
	 * Register a {@link FluidRenderHandler} for a given Fluid.
	 *
	 * @param fluid The Fluid.
	 * @param renderer The FluidRenderHandler.
	 *
	 * @deprecated Use {@link #registerRenderHandler(Fluid, FluidRenderHandler)}
	 */
	@Deprecated
	default void register(Fluid fluid, FluidRenderHandler renderer) {
		registerRenderHandler(fluid, renderer);
	}
}
