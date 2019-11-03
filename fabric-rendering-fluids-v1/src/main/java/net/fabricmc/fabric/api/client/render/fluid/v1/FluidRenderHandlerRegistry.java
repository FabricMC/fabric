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

import net.minecraft.fluid.Fluid;

import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;

/**
 * Registry for {@link FluidRenderHandler} instances.
 *
 * <p>Notably, this supports querying, overriding and wrapping vanilla fluid
 * rendering.
 */
public interface FluidRenderHandlerRegistry {
	FluidRenderHandlerRegistry INSTANCE = FluidRenderHandlerRegistryImpl.INSTANCE;

	/**
	 * Get a {@link FluidRenderHandler} for a given Fluid.
	 * Supports vanilla and Fabric fluids.
	 *
	 * @param fluid The Fluid.
	 * @return The FluidRenderHandler.
	 */
	FluidRenderHandler get(Fluid fluid);

	/**
	 * Register a {@link FluidRenderHandler} for a given Fluid.
	 *
	 * @param fluid The Fluid.
	 * @param renderer The FluidRenderHandler.
	 */
	void register(Fluid fluid, FluidRenderHandler renderer);
}
