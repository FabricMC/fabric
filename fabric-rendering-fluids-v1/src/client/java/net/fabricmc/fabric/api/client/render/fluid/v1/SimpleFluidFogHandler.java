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

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;

/**
 * A simple {@link FluidFogHandler} with fixed fog settings.
 */
public class SimpleFluidFogHandler implements FluidFogHandler {
	protected final int color;
	protected final FogParameters parameters;

	/**
	 * Creates a new handler with the specified fog settings.
	 *
	 * @param color       Fluid fog color RGB. Alpha is ignored.
	 * @param startRadius Distance in blocks, from the camera position, in which the fog starts rendering.
	 * @param endRadius   Distance in blocks, from the camera position, after which the fog is totally opaque.
	 * @param shape       Shape of the fog.
	 */
	public SimpleFluidFogHandler(int color, float startRadius, float endRadius, FogShape shape) {
		this.color = color;
		this.parameters = new FogParameters(startRadius, endRadius, shape);
	}

	/**
	 * Creates a new handler with the specified fog settings, with {@link FogShape#SPHERE} as its shape.
	 *
	 * @param color       Fluid fog color RGB. Alpha is ignored.
	 * @param startRadius Distance in blocks, from the camera position, in which the fog starts rendering.
	 * @param endRadius   Distance in blocks, from the camera position, after which the fog is totally opaque.
	 */
	public SimpleFluidFogHandler(int color, float startRadius, float endRadius) {
		this(color, startRadius, endRadius, FogShape.SPHERE);
	}

	/**
	 * Gets the fluid fog color RGB. Alpha is ignored.
	 *
	 * @param camera       Camera submerged by the fluid.
	 * @param tickDelta    Time passed from the last tick.
	 * @param world        Current client world.
	 * @param viewDistance Current view distance.
	 * @param skyDarkness  Current sky darkness.
	 * @return the color of the fog, or {@code -1} to make it hidden.
	 */
	@Override
	public int getFogColor(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness) {
		return color;
	}

	/**
	 * Gets the fog rendering parameters.
	 *
	 * @param camera       Camera submerged by the fluid.
	 * @param fogType      Type of fog.
	 * @param viewDistance Current view distance.
	 * @param thickFog     Specifies if a thick fog must be rendered.
	 * @param tickDelta    Time passed from the last tick.
	 */
	@Override
	public FogParameters getFogParameters(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta) {
		return parameters;
	}
}
