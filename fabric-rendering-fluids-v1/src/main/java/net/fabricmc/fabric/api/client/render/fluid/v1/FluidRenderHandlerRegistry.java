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

import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderRegistryImpl;

/**
 * Registry for {@link FluidRenderHandler} instances.
 *
 * <p>Notably, this supports querying, overriding and wrapping vanilla fluid
 * rendering.
 *
 * @deprecated Use {@link FluidRenderRegistry}
 */
@Deprecated
public interface FluidRenderHandlerRegistry {
	@Deprecated
	@SuppressWarnings("deprecation")
	FluidRenderHandlerRegistry INSTANCE = new FluidRenderHandlerRegistry() {
		@Override
		public FluidRenderHandler get(Fluid fluid) {
			return FluidRenderRegistryImpl.INSTANCE.getRenderHandler(fluid);
		}

		@Override
		public void register(Fluid fluid, FluidRenderHandler renderer) {
			FluidRenderRegistryImpl.INSTANCE.registerRenderHandler(fluid, renderer);
		}
	};

	/**
	 * Get a {@link FluidRenderHandler} for a given Fluid.
	 * Supports vanilla and Fabric fluids.
	 *
	 * @param fluid The Fluid.
	 * @return The FluidRenderHandler.
	 *
	 * @deprecated Use {@link FluidRenderRegistry#getRenderHandler(Fluid)}
	 */
	@Deprecated
	FluidRenderHandler get(Fluid fluid);

	/**
	 * Register a {@link FluidRenderHandler} for a given Fluid.
	 *
	 * @param fluid The Fluid.
	 * @param renderer The FluidRenderHandler.
	 *
	 * @deprecated Use {@link FluidRenderRegistry#registerRenderHandler(Fluid, FluidRenderHandler)}
	 */
	@Deprecated
	void register(Fluid fluid, FluidRenderHandler renderer);
}
