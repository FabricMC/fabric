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

import java.util.Objects;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;

public class FluidRenderingClientInitializer implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Force FluidRenderHandlerRegistry to class load in the correct order to fix https://github.com/FabricMC/fabric/issues/1806 without breaking mods that use the impl.
		// See: https://github.com/FabricMC/fabric/pull/1808 for the full fix used in 1.18
		Objects.requireNonNull(FluidRenderHandlerRegistry.INSTANCE, "Failed to setup FluidRenderHandlerRegistry");
	}
}
